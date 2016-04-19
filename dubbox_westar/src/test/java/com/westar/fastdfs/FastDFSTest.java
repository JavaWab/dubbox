package com.westar.fastdfs;


import com.westar.wangab.fastdfs.FastDFSConfiguration;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by wanganbang on 4/18/16.
 */
public class  FastDFSTest{
    FastDFSConfiguration configuration;

    @Test
    public void testInitConfiguration() throws IOException{
        configuration = new FastDFSConfiguration(FastDFSTest.class.getClassLoader().getResource("fdfs.properties").getPath());
        String[] vals = configuration.getValues("tracker_server");
        System.out.println(vals[0]);
    }
}
