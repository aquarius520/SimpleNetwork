package com.aquarius.simple.network;

import org.apache.http.HttpStatus;

import java.util.Collections;
import java.util.Map;

/**
 * Created by aquarius on 2017/10/29.
 */
public class NetworkResponse {

    /** 响应码 */
    public final int statusCode;

    /** raw data from response */
    public final byte[] data;

    /** 响应头 */
    public final Map<String, String> headers;

    public final boolean notModified;

    public final long networkTimeMs;

    /**
     * Creates a new network response.  创建一个网络的响应实体
     * @param statusCode the HTTP status code  HTTP状态码
     * @param data Response body   响应体【以字节数组表示】
     * @param headers Headers returned with this response, or null for none 响应头信息
     * @param notModified True if the server returned a 304 and the data was already in cache  如果服务器返回304状态码则表示资源未修改
     * @param networkTimeMs Round-trip network time to receive network response  接收到网络响应的往返时间
     */
    public NetworkResponse(int statusCode, byte[] data, Map<String, String> headers,
                           boolean notModified, long networkTimeMs) {
        this.statusCode = statusCode;
        this.data = data;
        this.headers = headers;
        this.notModified = notModified;
        this.networkTimeMs = networkTimeMs;
    }

    public NetworkResponse(int statusCode, byte[] data, Map<String, String> headers, boolean notModified) {
        this(statusCode, data, headers, notModified, 0);
    }

    public NetworkResponse(byte[] data) {
        this(HttpStatus.SC_OK, data, Collections.emptyMap(), false, 0);
    }

    public NetworkResponse(byte[] data, Map<String, String> headers) {
        this(HttpStatus.SC_OK, data, headers, false, 0);
    }
}
