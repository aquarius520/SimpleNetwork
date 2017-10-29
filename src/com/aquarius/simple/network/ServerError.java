package com.aquarius.simple.network;

/**
 * Created by aquarius on 2017/10/29.
 */
public class ServerError extends Error {
    public ServerError(NetworkResponse networkResponse) {
        super(networkResponse);
    }

    public ServerError() {
        super();
    }
}
