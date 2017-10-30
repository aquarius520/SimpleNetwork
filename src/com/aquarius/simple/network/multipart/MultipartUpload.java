package com.aquarius.simple.network.multipart;

import java.io.File;
import java.util.Map;

/**
 * Created by aquarius on 2017/10/30.
 */
public interface MultipartUpload {
    public void addFileUpload(String param,File file);

    public void addStringUpload(String param,String content);

    public Map<String,File> getFileUploads();

    public Map<String,String> getStringUploads();
}
