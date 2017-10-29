package com.aquarius.simple.network;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.util.Map;

/**
 * Created by aquarius on 2017/10/28.
 */
public class HttpClientStack implements HttpStack {

    private HttpClient mHttpClient;

    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    public HttpClientStack(HttpClient client) {
        this.mHttpClient = client;
    }

    @SuppressWarnings("deprecated")
    @Override
    public HttpResponse performRequest(Request request) throws IOException {
        HttpUriRequest httpRequest = createHttpRequest(request);
        addHeaders(httpRequest, request.getHeaders());
        //onPrepareRequest(httpRequest);

        HttpParams httpParams = httpRequest.getParams();
        int timeOutMs = (int) request.getTimeOutMs();

        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
        HttpConnectionParams.setSoTimeout(httpParams, timeOutMs);
        return mHttpClient.execute(httpRequest);
    }

    private HttpUriRequest createHttpRequest(Request request) {
        switch (request.getMethod()) {
            case Request.Method.DEPRECATED_GET_OR_POST:
                // 如果请求体的POST body为空，则假定是GET请求，否则认为是POST请求
                byte[] postBody = request.getPostBody();
                if (postBody == null) {
                    return new HttpGet(request.getUrl());
                } else {
                    HttpPost httpPostRequest = new HttpPost(request.getUrl());
                    httpPostRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
                    HttpEntity entity = new ByteArrayEntity(postBody);
                    httpPostRequest.setEntity(entity);
                    return httpPostRequest;
                }

            case Request.Method.GET:
                return new HttpGet(request.getUrl());

            case Request.Method.POST:
                HttpPost postRequest = new HttpPost(request.getUrl());
                postRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody(request, postRequest);

                // ------------------- for  proxy ---------------------
//                HttpHost httpHost = new HttpHost("192.168.97.122", 8888);
//                RequestConfig config = RequestConfig.custom().setProxy(httpHost).build();
//                postRequest.setConfig(config);
                return postRequest;

            case Request.Method.DELETE:
                return new HttpDelete(request.getUrl());

            case Request.Method.PUT:
                HttpPut httpPutRequest = new HttpPut(request.getUrl());
                httpPutRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody(request, httpPutRequest);
                return httpPutRequest;

            case Request.Method.HEAD:
                return new HttpHead(request.getUrl());

            case Request.Method.OPTIONS:
                return new HttpOptions(request.getUrl());

            case Request.Method.TRACE:
                return new HttpTrace(request.getUrl());

            case Request.Method.PATCH:
                HttpPatch patchRequest = new HttpPatch(request.getUrl());
                patchRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody(request, patchRequest);
                return patchRequest;

            default:
                throw new IllegalStateException("Unknown request method.");
        }
    }


    private void setEntityIfNonEmptyBody(Request request, HttpEntityEnclosingRequestBase httpRequest) {
        byte[] body = request.getBody();
        if (body != null) {
            HttpEntity entity = new ByteArrayEntity(body);
            httpRequest.setEntity(entity);
        }
    }

    private void addHeaders(HttpRequest request, Map<String, String> headers) {
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    // Called before the request is executed using the underlying HttpClient.
    private void onPrepareRequest(HttpRequest httpRequest) throws IOException {
        // do nothing
    }
}
