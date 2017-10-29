package com.aquarius.simple.network;

/**
 * Created by aquarius on 2017/10/29.
 */
public class NoConnectionError extends NetworkError {
    public NoConnectionError() {
        super();
    }

    public NoConnectionError(Throwable reason) {
        super(reason);
    }
}
