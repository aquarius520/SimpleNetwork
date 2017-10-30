package com.aquarius.simple.network.test;

import com.aquarius.simple.network.*;
import com.aquarius.simple.network.Error;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by aquarius on 2017/10/28.
 */
public class Test {

    public static void main(String[] argv) throws Error, InterruptedException{

        Request stringRequest = new Request(Request.Method.GET, "http://www.kuaidi100.com/query");
        stringRequest.setTimeOutMs(4000)
                .setRetryPolicy(new DefaultRetryPolicy())
                .setHeaders(fillHeaders())
                .setParams(fillParams())
                .setParamEncoding("UTF-8");

        System.setProperty("http.proxyHost", "192.168.97.104");
        System.setProperty("https.proxyHost", "192.168.97.104");
        System.setProperty("http.proxyPort", "8888");
        System.setProperty("https.proxyPort", "8888");

        List<File> imageFiles = getFilesOfDir("d:\\upload-image-for-test");


        UploadImageRequest imageRequest = new UploadImageRequest(Request.Method.POST, "http://www.chuantu.biz/upload.php",imageFiles);
        imageRequest.setHeaders(fillUploadImageHeaders());

//        HttpStack httpClientStack = new HttpClientStack(new DefaultHttpClient());
//        BasicNetwork network = new BasicNetwork(httpClientStack);

        HttpStack httpUrlStack = new HurlStack();
        BasicNetwork network = new BasicNetwork(httpUrlStack);

        NetworkResponse response = network.performRequest(imageRequest);

        printExecuteResult(response);

        System.out.println("-------------------------------------------");

//        Thread.sleep(2000);
//
//        HttpStack httpUrlStack = new HurlStack();
//        BasicNetwork network1 = new BasicNetwork(httpUrlStack);
//        NetworkResponse response1 = network1.performRequest(request);
//        printExecuteResult(response);


    }

    private static void printExecuteResult(NetworkResponse response) {
        System.out.println("statusCode="+response.statusCode);
        System.out.println("notModified="+response.notModified);
        printHeaderInfo(response.headers);
        try {
            System.out.println(new String(response.data, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private static Map<String, String> fillHeaders() {
        Map<String, String> header = new HashMap<>();
        header.put("Host", "www.kuaidi100.com");
        header.put("Connection", "close");
        //header.put("Accept-Encoding", "gzip, deflate, br");
        header.put("Accept-Language", "zh-CN,zh;q=0.8");
        return header;
    }

    private static Map<String, String> fillParams() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("type", "shunfeng");
        params.put("postid", "258203901201");
        return  params;
    }

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

    private static Map<String, String> fillUploadImageHeaders() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("Host", "www.chuantu.biz");
        params.put("Connection", "keep-alive");
        params.put("Cache-Control", "max-age=0");
        params.put("Origin", "http://www.chuantu.biz");
        params.put("Upgrade-Insecure-Requests", "1");
        params.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
        params.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        params.put("Referer", "http://www.chuantu.biz");
        //params.put("Accept-Encoding", "gzip,deflate");
        params.put("Accept-Language", "zh-CN,zh;q=0.8");
        return params;
    }

    private static List<File> getFilesOfDir(String dirPath) {
        List<File> list = new ArrayList<>();
        File dir = new File(dirPath);
        if (dir.exists() && dir.isDirectory() && dir.canRead()) {
            File[] files = dir.listFiles();
            for(int i = 0; i < files.length; i++) {
                list.add(files[i]);
            }
        }
        return list;
    }

    private static void printHeaderInfo(Map<String, String> header) {
        if (header != null && header.size() > 0) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }

}
