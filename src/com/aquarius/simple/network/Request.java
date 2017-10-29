package com.aquarius.simple.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

/**
 * Created by aquarius on 2017/10/28.
 */
public class Request{

    /**
     * post和get 默认的参数编码
     */
    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    private static final long DEFAULT_TIMEOUT_MS = 5000;

    /** 请求方法 */
    private int mMethod;

    /** 参数编码  */
    private String mParamEncoding;

    /** 超时时间  */
    private long mTimeOutMs;

    /** 请求url  */
    private String mUrl;

    /** 重定向的url */
    private String mRedirectUrl;

    /*** 请求头部信息 */
    private Map<String, String> mHeaders;

    /*** 请求头部信息 */
    private Map<String, String> mParams;

    private RetryPolicy mRetryPolicy;

    public Request(int method, String url) {
        this.mMethod = method;
        this.mUrl = url;
    }


    public interface Method {
        /* public static final*/ int DEPRECATED_GET_OR_POST = -1 ;
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }

    public int getMethod() {
        return mMethod;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getRedirectUrl() {
        return mRedirectUrl;
    }

    public String getParamEncoding() {
        return mParamEncoding == null ? DEFAULT_PARAMS_ENCODING : mParamEncoding;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public Map<String, String> getParams() {
        return mParams;
    }

    public long getTimeOutMs() {
        if (mRetryPolicy != null) {
            return mRetryPolicy.getCurrentTimeout();
        }
        return mTimeOutMs == 0 ? DEFAULT_TIMEOUT_MS : mTimeOutMs;
    }

    public RetryPolicy getRetryPolicy() {
        return mRetryPolicy;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public void setRedirectUrl(String url) {
        this.mRedirectUrl = url;
    }

    public Request setTimeOutMs(long mTimeOutMs) {
        this.mTimeOutMs = mTimeOutMs;
        return this;
    }

    public Request setRetryPolicy(RetryPolicy mRetryPolicy) {
        this.mRetryPolicy = mRetryPolicy;
        return this;
    }

    public Request setHeaders(Map<String, String> header) {
        if (header == null || header.size() == 0) {
            mHeaders = Collections.emptyMap();
        }else {
            mHeaders = header;
        }
         return this;
    }

    public Request setParams(Map<String, String> params) {
        if (params == null || params.size() == 0) {
            mParams = Collections.emptyMap();
        } else {
            mParams = params;
            // 如果是GET方法，但是却设置了参数，自动将查询参数拼接到url上
            if (mMethod == Method.GET) {
                String querySuffix = fillCompleteRequestSuffix(params);
                setUrl(mUrl + querySuffix);
            }
        }
        return this;
    }

    private String fillCompleteRequestSuffix(Map<String, String> params){
        if (params == null || params.size() == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        try {

            builder.append("?");
            for (String key : params.keySet()) {
                builder.append(URLEncoder.encode(key, getParamEncoding()))
                        .append("=")
                        .append(URLEncoder.encode(params.get(key), getParamEncoding()))
                        .append("&");
            }
            String query = builder.toString();
            int lastIndex = query.lastIndexOf("&");
            return query.substring(0, lastIndex);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public Request setParamEncoding(String encoding) {
        if (encoding == null || encoding.length() == 0) {
            mParamEncoding = DEFAULT_PARAMS_ENCODING;
        } else {
            mParamEncoding = encoding;
        }
        return this;
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded;charset=" + getParamEncoding();
    }

    public byte[] getBody() {
        return getPostBody();
    }

    public byte[] getPostBody() {
        Map<String, String> postParams = getParams();
        if (postParams != null && postParams.size() > 0) {
            return encodeParameters(postParams, getParamEncoding());
        }
        return null;
    }

    private byte[] encodeParameters(Map<String, String> params, String encoding) {
        StringBuilder builder = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.append(URLEncoder.encode(entry.getKey(), encoding))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encoding))
                        .append("&");
            }
            return builder.toString().getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported : " + encoding, e);
        }
    }

/*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Request request = (Request) o;

        if (mMethod != request.mMethod) return false;
        if (!mUrl.equals(request.mUrl)) return false;
        if (mHeaders != null ? !mHeaders.equals(request.mHeaders) : request.mHeaders != null) return false;
        return mParams != null ? mParams.equals(request.mParams) : request.mParams == null;
    }

    @Override
    public int hashCode() {
        int result = mMethod;
        result = 31 * result + mUrl.hashCode();
        result = 31 * result + (mHeaders != null ? mHeaders.hashCode() : 0);
        result = 31 * result + (mParams != null ? mParams.hashCode() : 0);
        return result;
    }
    */
}
