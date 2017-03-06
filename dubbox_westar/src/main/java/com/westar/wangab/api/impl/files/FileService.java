package com.westar.wangab.api.impl.files;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.westar.wangab.api.intf.DeleteParams;
import com.westar.wangab.api.intf.IFileService;
import com.westar.wangab.api.intf.RequestExtParam;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by wanganbang on 4/19/16.
 */
@Controller
public class FileService implements IFileService {
    private static final Logger LOG = LoggerFactory.getLogger(FileService.class);
    private static ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 10);
    @Resource
    private Mongo mongo;

    static {
        try {
            ClientGlobal.init(FileService.class.getClassLoader().getResource("fdfs.properties").getPath());
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("FileService init Failed, maybe not fdfs.properties");
            System.exit(1);
        }
    }

    @Override
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<Map> upload(final MultipartFile file, final @Validated RequestExtParam param) {
        final JSONObject jsonObject = JSON.parseObject(param.getExtInfo());
        String effect = jsonObject.getString("effect");
        final String userid = jsonObject.getString("uid");
        final String cardid = jsonObject.getString("cid");

        DBCollection collection = mongo.getDB("westar").getCollection("ws_user_file");
        final Map<String, Object> collections = new HashMap<String, Object>();
        if (effect != null && userid != null) {
            if ("mkicon".equals(effect)) {
                collection = mongo.getDB("westar").getCollection("ws_user_mkicon");
                collections.put("ext", "mk");
                collections.put("collection", collection);
            } else if ("mkphoto".equals(effect)) {
                collection = mongo.getDB("westar").getCollection("ws_user_mkphoto");
                collections.put("ext", "mk");
                collections.put("collection", collection);
            } else if ("icon".equals(effect)) {
                collection = mongo.getDB("westar").getCollection("ws_user_icon");
                collections.put("ext", "usr");
                collections.put("collection", collection);
            } else if ("photo".equals(effect)) {
                collection = mongo.getDB("westar").getCollection("ws_user_photo");
                collections.put("ext", "usr");
                collections.put("collection", collection);
            } else if ("file".equals(effect)) {
                collection = mongo.getDB("westar").getCollection("ws_user_file");
                collections.put("ext", "usr");
                collections.put("collection", collection);
            }
        } else {
            Map map = new HashMap();
            map.put("errormsg", "RequestExtParam is not null");
            return new ResponseEntity<Map>(map, HttpStatus.BAD_REQUEST);
        }

        Future<ResponseEntity<Map>> result = service.submit(new Callable<ResponseEntity<Map>>() {
            @Override
            public ResponseEntity<Map> call() throws Exception {
                Map<String, Object> map = new HashMap<String, Object>();
                if (file == null) {
                    map.put("errormsg", "File is not null");
                    return new ResponseEntity<Map>(map, HttpStatus.BAD_REQUEST);
                }
                try {
                    TrackerGroup trackerGroup = ClientGlobal.getG_tracker_group();
                    TrackerServer trackerServer = trackerGroup.getConnection();
                    StorageServer storageServer = null;

                    StorageClient client = new StorageClient(trackerServer, storageServer);

                    NameValuePair[] meta_list = new NameValuePair[1];
                    meta_list[0] = new NameValuePair("param", param.getExtInfo());

                    String[] results = client.upload_file(file.getBytes(), param.getExtType(), meta_list);

                    map.put("group", results[0]);
                    map.put("path", results[1]);
                    map.put("url", ClientGlobal.getG_base_url_prefixes() + results[1]);

                    DBObject temp = new BasicDBObject();
                    temp.put("uid", userid);
                    temp.put("group", results[0]);
                    temp.put("path", results[1]);
                    temp.put("url", ClientGlobal.getG_base_url_prefixes() + results[1]);
                    temp.put("time", new Date());

                    String ext = (String) (collections.get("ext"));
                    DBCollection dbCollection = (DBCollection) (collections.get("collection"));
                    if ("mk".equals(ext)) {
                        temp.put("cid", cardid);
                    }
                    dbCollection.save(temp);

                    return new ResponseEntity<Map>(map, HttpStatus.OK);
                } catch (Exception e) {
                    LOG.error(e.getMessage());
                    e.printStackTrace();
                    map.put("errormsg", e.getMessage());
                    return new ResponseEntity<Map>(map, HttpStatus.BAD_REQUEST);
                }
            }
        });
        Map map = new HashMap();
        try {
            return result.get(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            map.put("errormsg", e.getMessage());
        } catch (ExecutionException e) {
            map.put("errormsg", e.getMessage());
        } catch (TimeoutException e) {
            map.put("errormsg", e.getMessage());
        }
        return new ResponseEntity<Map>(map, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @RequestMapping("/delete")
    public ResponseEntity<Map> deleteFile(@Validated DeleteParams params) {
        Map<String, Object> map = new HashMap<String, Object>();
        DBCollection collection = mongo.getDB("westar").getCollection("ws_user_file");
        String effect = params.getEffect();
        String userid = params.getUerid();
        String url = params.getUrl();

        if ("mkicon".equals(effect)) {
            collection = mongo.getDB("westar").getCollection("ws_user_mkicon");
        } else if ("mkphoto".equals(effect)) {
            collection = mongo.getDB("westar").getCollection("ws_user_mkphoto");
        } else if ("icon".equals(effect)) {
            collection = mongo.getDB("westar").getCollection("ws_user_icon");
        } else if ("photo".equals(effect)) {
            collection = mongo.getDB("westar").getCollection("ws_user_photo");
        } else if ("file".equals(effect)) {
            collection = mongo.getDB("westar").getCollection("ws_user_file");
        }

        try {
            DBObject temp = new BasicDBObject();
            temp.put("uid", userid);
            temp.put("url", url);
            DBObject result = collection.findAndRemove(temp);
            LOG.info(result.toString());
            if (result != null) {
                TrackerGroup trackerGroup = ClientGlobal.getG_tracker_group();
                TrackerServer trackerServer = trackerGroup.getConnection();
                StorageServer storageServer = null;

                String group = result.get("group").toString();
                String path = result.get("path").toString();
                LOG.info("delete file :" + group + "\t" + path);
                StorageClient client = new StorageClient(trackerServer, storageServer);
                int n = client.delete_file(group, path);
                LOG.info("delete result number " + n);
                map.put("result", true);
                map.put("retmdg", "success");
                return new ResponseEntity<Map>(map, HttpStatus.OK);
            } else {
                map.put("result", false);
                map.put("retmdg", "file is not find");
                return new ResponseEntity<Map>(map, HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            LOG.error(e.getMessage());
            map.put("result", false);
            map.put("retmsg", e.getMessage());
            return new ResponseEntity<Map>(map, HttpStatus.BAD_REQUEST);
        }

    }

}
