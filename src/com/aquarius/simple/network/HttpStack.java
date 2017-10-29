package com.aquarius.simple.network;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Map;

/**
 * Created by aquarius on 2017/10/28.
 */
public interface HttpStack {

    public abstract HttpResponse performRequest(Request request) throws IOException;

}
