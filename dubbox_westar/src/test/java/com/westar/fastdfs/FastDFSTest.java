package com.westar.fastdfs;


import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by wanganbang on 4/18/16.
 */
public class  FastDFSTest{

    @Test
    public void testUploadFiles() throws Exception{
        ClientGlobal.init(FastDFSTest.class.getClassLoader().getResource("fdfs.properties").getPath());
        TrackerGroup trackerGroup = ClientGlobal.getG_tracker_group();
        TrackerServer trackerServer = trackerGroup.getConnection();
        StorageServer storageServer = null;

        StorageClient client = new StorageClient(trackerServer, storageServer);

        String local_filename = "/home/wanganbang/Downloads/tupian.jpg";
        String file_ext_name = "jpg";
        NameValuePair[] meta_list = new NameValuePair[2];
        meta_list[0] = new NameValuePair("height", "300");
        meta_list[1] = new NameValuePair("weight", "200");

        String[] results = client.upload_file(local_filename, file_ext_name, meta_list);

        System.out.println(results[0] + "\t" + results[1]);
        Assert.assertTrue((results[0] != null) && (results[1] != null));
    }
    @Test
    public void TestDeleteFile() throws Exception{
        //group1	M00/00/00/ezn5RFcVpI2AP-pXAADcDOxaQk8399.jpg
        ClientGlobal.init(FastDFSTest.class.getClassLoader().getResource("fdfs.properties").getPath());
        TrackerGroup trackerGroup = ClientGlobal.getG_tracker_group();
        TrackerServer trackerServer = trackerGroup.getConnection();
        StorageServer storageServer = null;

        StorageClient client = new StorageClient(trackerServer, storageServer);
        int n = client.delete_file("group1", "M00/00/00/ezn5RFcVpDmANvxcAADcDOxaQk8518.jpg");
        Assert.assertTrue("文件删除未成功", (n==0));
    }
}
