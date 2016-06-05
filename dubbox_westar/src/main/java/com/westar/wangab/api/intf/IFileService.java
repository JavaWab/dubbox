package com.westar.wangab.api.intf;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Created by wanganbang on 4/19/16.
 */
public interface IFileService {
    public ResponseEntity<Map> deleteFile(DeleteParams params);
    public ResponseEntity<Map> upload(MultipartFile file, RequestExtParam param);
}
