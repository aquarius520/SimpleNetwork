package com.aquarius.simple.network;

/**
 * Created by aquarius on 2017/10/29.
 */
public class NetworkError extends Error {
    public NetworkError() {
        super();
    }

    public NetworkError(Throwable cause) {
        super(cause);
    }

    public NetworkError(NetworkResponse networkResponse) {
        super(networkResponse);
    }
}
