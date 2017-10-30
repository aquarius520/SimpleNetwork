package com.aquarius.simple.network.test;

import com.aquarius.simple.network.Request;
import com.aquarius.simple.network.multipart.MultipartRequest;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by aquarius on 2017/10/30.
 */
public class MuiltPartRequestTest {
    public static void main(String[] argv) {

        String host = "http://www.chuantu.biz";

        MultipartRequest multipartRequest = new MultipartRequest(Request.Method.POST, host ) {
            @Override
            public Map<String, File> getFileUploads() {
                return getFilesOfDir("d:\\upload-image-for-test");

            }

            @Override
            public Map<String, String> getStringUploads() {
                return super.getStringUploads();
            }
        };
    }

    private static Map<String, File> getFilesOfDir(String dirPath) {
        Map<String, File> map = new LinkedHashMap<>();
        File dir = new File(dirPath);
        if (dir.exists() && dir.isDirectory() && dir.canRead()) {
            File[] files = dir.listFiles();
            for(int i = 0; i < files.length; i++) {
                map.put(files[i].getName(), files[i]);
            }
        }
        return map;
    }

    private static Map<String, String> getStringUploads() {

        return null;
    }
}
