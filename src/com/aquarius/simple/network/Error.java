package com.aquarius.simple.network;

/**
 * Created by aquarius on 2017/10/29.
 */
public class Error extends Exception {

    public final NetworkResponse networkResponse;
    private long networkTimeMs;

    public Error() {
        networkResponse = null;
    }

    public Error(NetworkResponse response) {
        networkResponse = response;
    }

    public Error(String exceptionMessage) {
        super(exceptionMessage);
        networkResponse = null;
    }

    public Error(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
        networkResponse = null;
    }

    public Error(Throwable cause) {
        super(cause);
        networkResponse = null;
    }

    /* package */ void setNetworkTimeMs(long networkTimeMs) {
        this.networkTimeMs = networkTimeMs;
    }

    public long getNetworkTimeMs() {
        return networkTimeMs;
    }
}
