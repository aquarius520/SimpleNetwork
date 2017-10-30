package com.aquarius.simple.network.multipart;

import com.aquarius.simple.network.Request;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by aquarius on 2017/10/30.
 */
public class MultipartRequest extends Request implements MultipartUpload{

    /* To hold the parameter name and the File to upload */
    private Map<String,File> fileUploads = new LinkedHashMap<String,File>();

    /* To hold the parameter name and the string content to upload */
    private Map<String,String> stringUploads = new LinkedHashMap<String,String>();

    public MultipartRequest(int method, String url) {
        super(method, url);
    }

    @Override
    public void addFileUpload(String param, File file) {
        fileUploads.put(param, file);
    }

    @Override
    public void addStringUpload(String param, String content) {
        stringUploads.put(param, content);
    }

    @Override
    public Map<String, File> getFileUploads() {
        return fileUploads;
    }

    @Override
    public Map<String, String> getStringUploads() {
        return stringUploads;
    }

}
