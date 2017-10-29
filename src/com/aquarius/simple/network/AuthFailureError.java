package com.aquarius.simple.network;

/**
 * Created by aquarius on 2017/10/29.
 */
public class AuthFailureError extends Error {

    public AuthFailureError() { }

    public AuthFailureError(NetworkResponse response) {
        super(response);
    }

    public AuthFailureError(String message) {
        super(message);
    }

    public AuthFailureError(String message, Exception reason) {
        super(message, reason);
    }


    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
