package com.aquarius.simple.network;

/**
 * Created by aquarius on 2017/10/29.
 */
public interface Network {

    /**
     * NetworkResponse 执行请求后返回的结果
     * @param request
     * @return
     */
    public NetworkResponse performRequest(Request request) throws Error;

}
