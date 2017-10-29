package com.aquarius.simple.network.test;

import com.aquarius.simple.network.Constant;
import com.aquarius.simple.network.Request;

import java.io.*;
import java.util.List;

/**
 * Created by aquarius on 2017/10/29.
 */
public class UploadImageRequest extends Request {

    private static final String BOUNDARY = "--------------2017-10-29";
    private static final String CONTENT_TYPE = "multipart/form-data";


    private List<File> mImageFiles;

    public UploadImageRequest(int method, String url, List<File> files) {
        super(method, url);
        this.mImageFiles = files;
    }


//        POST http://www.chuantu.biz/upload.php HTTP/1.1
//        Host: www.chuantu.biz
//        Connection: keep-alive
//        Content-Length: 63852
//        Cache-Control: max-age=0
//        Origin: http://www.chuantu.biz
//        Upgrade-Insecure-Requests: 1
//        Content-Type: multipart/form-data; boundary=----WebKitFormBoundaryrJP52DFixBrhNaCU
//        User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36
//        Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8
//        Referer: http://www.chuantu.biz/
//        Accept-Encoding: gzip, deflate
//        Accept-Language: zh-CN,zh;q=0.8
//        Cookie: __cfduid=de502ee40ef3f57e77e178ada82efe95d1505384915; Hm_lvt_8c37727332894355e0e23138462805ac=1509272285; Hm_lpvt_8c37727332894355e0e23138462805ac=1509275763

//        ------WebKitFormBoundaryrJP52DFixBrhNaCU
//            Content-Disposition: form-data; name="MAX_FILE_SIZE"
//
//                    200000000
//                    ------WebKitFormBoundaryrJP52DFixBrhNaCU
//            Content-Disposition: form-data; name="uploadimg"; filename="gradle-version.png"
//            Content-Type: image/png
//
//                    PNG
//        
//
//            IHDR     
//                       x  <    IDATx
//        \Te ? _k  KP Z  : c%  F  	  mJ[ j  6 M   .ؖX  E 9  -F
//            ;
//            Bll"    37 >g     !  3 <繟g     ?Xijj ~
//
//                    ------WebKitFormBoundaryrJP52DFixBrhNaCU--
    @Override
    public byte[] getBody() {

        if (mImageFiles != null || mImageFiles.size() > 0) {
            ByteArrayOutputStream bos = null;
            for (File file : mImageFiles) {
                String filename = file.getName();
                String mimeType = getMimeType(filename.substring(filename.indexOf(".") + 1));

                StringBuilder builder = new StringBuilder();
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    bos = new ByteArrayOutputStream();

                    // 1. 构建表单数据
                    builder.append("--").append(BOUNDARY).append("\r\n")
                            .append("Content-Disposition: form-data; ").append("name=").append("\"MAX_FILE_SIZE\"").append("\r\n")
                            .append("\r\n")
                            .append("200000000").append("\r\n")

                            .append("--").append(BOUNDARY).append("\r\n")
                            .append("Content-Disposition: form-data; ").append("name=").append("\"uploadimg\"").append("; ")
                            .append("filename=" + "\"" + filename + "\"")
                            .append("\r\n")
                            .append("Content-Type:" + mimeType).append("\r\n")
                            .append("\r\n");


                    // 表单数据写入流中
                    bos.write(builder.toString().getBytes());

                    byte[] buffer = new byte[1024 * 8];
                    int len;

                    while ((len = fis.read(buffer)) != -1) {
                        bos.write(buffer, 0, len);
                    }

                    // 二进制文件写入结束后，写入结尾标识
                    bos.write("\r\n".getBytes());
                    bos.write("\r\n".getBytes());


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            try {
                bos.write(("--" + BOUNDARY + "--" + "\r\n").getBytes());
                bos.flush();
                return  bos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }

        }
        return null;
    }

    @Override
    public String getBodyContentType() {
        return CONTENT_TYPE + ";" + "boundary=" + BOUNDARY;
    }

    private String getMimeType(String type) {
        if (type.equalsIgnoreCase("png")) {
            return Constant.MIMETYPE.IMAGE_PNG;
        }

        if (type.equalsIgnoreCase("jpg") || type.equalsIgnoreCase("jpeg")) {
            return Constant.MIMETYPE.IMAGE_JPG;
        }

        if (type.equalsIgnoreCase("gif")) {
            return Constant.MIMETYPE.IMAGE_GIF;
        }

        if (type.equalsIgnoreCase("bmp")) {
            return Constant.MIMETYPE.IMAGE_BMP;
        }

        if (type.equalsIgnoreCase("webp")) {
            return Constant.MIMETYPE.IMAGE_WEBP;
        }

        return null;
    }
}
