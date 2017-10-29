package com.aquarius.simple.network.test;

import com.aquarius.simple.network.*;
import com.aquarius.simple.network.Error;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by aquarius on 2017/10/28.
 */
public class Test {

    // http://www.cnblogs.com/sphere
    public static void main(String[] argv) throws Error, InterruptedException{


        Request request = new Request(Request.Method.GET, "http://www.kuaidi100.com/query");
        request.setTimeOutMs(4000)
                .setRetryPolicy(new DefaultRetryPolicy())
                .setHeaders(fillHeaders())
                .setParams(fillParams())
                .setParamEncoding("UTF-8");

        HttpStack httpClientStack = new HttpClientStack(new DefaultHttpClient());
        BasicNetwork network = new BasicNetwork(httpClientStack);

        NetworkResponse response = network.performRequest(request);

        printExecuteResult(response);

        System.out.println("-------------------------------------------");

        Thread.sleep(2000);

        HttpStack httpUrlStack = new HurlStack();
        BasicNetwork network1 = new BasicNetwork(httpUrlStack);
        NetworkResponse response1 = network1.performRequest(request);
        printExecuteResult(response);


    }

    private static void printExecuteResult(NetworkResponse response) {
        System.out.println("statusCode="+response.statusCode);
        System.out.println("notModified="+response.notModified);
        printHeaderInfo(response.headers);
        System.out.println(new String(response.data));

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

    private static void printHeaderInfo(Map<String, String> header) {
        if (header != null && header.size() > 0) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }

}
