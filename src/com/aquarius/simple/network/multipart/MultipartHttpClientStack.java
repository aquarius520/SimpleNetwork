package com.aquarius.simple.network.multipart;

import com.aquarius.simple.network.Constant;
import com.aquarius.simple.network.HttpStack;
import com.aquarius.simple.network.Request;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by aquarius on 2017/10/30.
 */
public class MultipartHttpClientStack implements HttpStack {

    private HttpClient mHttpClient;

    public MultipartHttpClientStack(HttpClient httpClient) {
        this.mHttpClient = httpClient;
    }

    @Override
    public HttpResponse performRequest(Request request) throws IOException {

        HttpUriRequest httpRequest = createMultiHttpRequest(request);
        addHeaders(httpRequest, request.getHeaders());

        HttpParams params = httpRequest.getParams();
        int timeOutMs = (int) request.getTimeOutMs();
        HttpConnectionParams.setConnectionTimeout(params, timeOutMs);
        HttpConnectionParams.setSoTimeout(params, timeOutMs);

        return mHttpClient.execute(httpRequest);
    }

    private HttpUriRequest createMultiHttpRequest(Request request) {
        switch (request.getMethod()) {
            case Request.Method.DEPRECATED_GET_OR_POST:
                byte[] postBody = request.getPostBody();
                if (postBody != null) {
                    HttpPost postRequest = new HttpPost(request.getUrl());
                    if (request.getBodyContentType() != null) {
                        postRequest.addHeader(Constant.Header.HEADER_CONTENT_TYPE, request.getBodyContentType());
                    }
                    HttpEntity entity = new ByteArrayEntity(postBody);
                    postRequest.setEntity(entity);
                    return postRequest;
                }else {
                    return new HttpGet(request.getUrl());
                }

            case Request.Method.GET:
                return new HttpGet(request.getUrl());

            case Request.Method.DELETE:
                return new HttpDelete(request.getUrl());

            case Request.Method.POST:
                HttpPost httpPost = new HttpPost(request.getUrl());
                httpPost.addHeader(Constant.Header.HEADER_CONTENT_TYPE, request.getBodyContentType());
                setMultiPartBody(httpPost, request);
                return httpPost;

            case Request.Method.PUT:
                HttpPut putRequest = new HttpPut(request.getUrl());
                if (request.getBodyContentType() != null) {
                    putRequest.addHeader(Constant.Header.HEADER_CONTENT_TYPE, request.getBodyContentType());
                }
                setMultiPartBody(putRequest, request);
                return putRequest;

            case Request.Method.HEAD:
                HttpHead headPost = new HttpHead(request.getUrl());
                return headPost;

            case Request.Method.OPTIONS:
                HttpOptions optionsRequest = new HttpOptions(request.getUrl());
                return optionsRequest;

            case Request.Method.PATCH:
                HttpPatch patchRequest = new HttpPatch(request.getUrl());
                if (request.getBodyContentType() != null) {
                    patchRequest.addHeader(Constant.Header.HEADER_CONTENT_TYPE, request.getBodyContentType());
                }
                setMultiPartBody(patchRequest, request);
                return patchRequest;
            default:
                throw new IllegalStateException("Unknown request method");
        }
    }

    private void setMultiPartBody(HttpEntityEnclosingRequestBase httpRequest, Request request) {
        if (!(request instanceof MultipartRequest)) {
            byte[] body = request.getBody();
            if (body != null) {
                HttpEntity entity = new ByteArrayEntity(body);
                httpRequest.setEntity(entity);
            }
        }else {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
            // string upload
            Map<String, String> stringUpload = ((MultipartRequest) request).getStringUploads();
            for (Map.Entry<String, String> entry : stringUpload.entrySet()) {
                builder.addPart(( entry.getKey()),
                        new StringBody(entry.getValue(), contentType));
            }

            // for file upload
            Map<String, File> fileUpload = ((MultipartRequest)request).getFileUploads();
            for (Map.Entry<String, File> entry : fileUpload.entrySet()) {
                builder.addPart(entry.getKey(), new FileBody(entry.getValue()));
            }

            httpRequest.setEntity(builder.build());
        }
    }

    private void addHeaders(HttpUriRequest request, Map<String, String> headers) {
        if (headers == null || headers.size() == 0) {
            return;
        }

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            request.addHeader(entry.getKey(), entry.getValue());
        }

    }
}
