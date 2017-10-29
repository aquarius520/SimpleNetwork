package com.aquarius.simple.network;

import org.apache.http.*;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by aquarius on 2017/10/29.
 */
public class BasicNetwork implements Network {

    private static final boolean DEBUG = true;

    protected HttpStack mHttpStack;

    public BasicNetwork(HttpStack httpStack) {
        this.mHttpStack = httpStack;
    }

    @Override
    public NetworkResponse performRequest(Request request) throws Error {
        long requestStart = System.currentTimeMillis();
        while (true) {
            HttpResponse httpResponse = null;
            byte[] responseContents = null;
            Map<String, String> responseHeaders = new LinkedHashMap<>();
            try {
                httpResponse = mHttpStack.performRequest(request);
                StatusLine statusLine = httpResponse.getStatusLine();

                int statusCode = statusLine.getStatusCode();
                responseHeaders = convertHeaders(httpResponse.getAllHeaders());
                if (statusCode == HttpStatus.SC_NOT_MODIFIED) {
                    return new NetworkResponse(statusCode, null, responseHeaders, true,
                            System.currentTimeMillis() - requestStart);
                }

                if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                    String newUrl = responseHeaders.get("Location");
                    request.setRedirectUrl(newUrl);
                }

                // 有时响应码是204 则返回的实体内没有内容
                if (httpResponse.getEntity() != null) {
                    responseContents = entityToBytes(httpResponse.getEntity());
                } else {
                    responseContents = new byte[0];
                }

                if (statusCode < 200 || statusCode > 299) {
                    throw new IOException();
                }

                // everything is ok
                return new NetworkResponse(statusCode, responseContents, responseHeaders, false,
                        System.currentTimeMillis() - requestStart);

            } catch (SocketTimeoutException e) {
                attemptRetryOnException("socket", request, new TimeOutError());
            } catch (ConnectTimeoutException e) {
                attemptRetryOnException("connection", request, new TimeOutError());
            } catch (MalformedURLException e) {
                attemptRetryOnException("Bad URL", request, new TimeOutError());
            } catch (IOException e) {
                int statusCode = 0;
                NetworkResponse networkResponse = null;
                if (httpResponse != null) {
                    statusCode = httpResponse.getStatusLine().getStatusCode();
                } else {
                    throw new NoConnectionError(e);
                }
                if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY ||
                        statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                    System.out.println(String.format("Request at %s has been redirected to %s",
                            request.getUrl(), request.getRedirectUrl()));
                } else {
                    System.out.println(String.format("Unexpected response code %d for %s", statusCode, request.getUrl()));
                }

                if (responseContents != null) {
                    networkResponse = new NetworkResponse(statusCode, responseContents,
                            responseHeaders, false, System.currentTimeMillis() - requestStart);

                    if (statusCode == HttpStatus.SC_UNAUTHORIZED || statusCode == HttpStatus.SC_FORBIDDEN) {
                        attemptRetryOnException("auth", request, new AuthFailureError(networkResponse));
                    } else if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY ||
                            statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                        attemptRetryOnException("redirect",
                                request, new AuthFailureError(networkResponse));
                    } else {
                        // TODO: Only throw ServerError for 5xx status codes.
                        throw new ServerError(networkResponse);
                    }
                } else {
                    throw new NetworkError(networkResponse);
                }
            }
        }
    }

    private void attemptRetryOnException(String type, Request request, Error e) throws Error{
        RetryPolicy retryPolicy = request.getRetryPolicy();
        int oldTimeOut = (int) request.getTimeOutMs();

        try {
            retryPolicy.retry(e);
        } catch (Error error) {
//            request.addMarker(
//                    String.format("%s-timeout-giveup [timeout=%s]", logPrefix, oldTimeout));
            throw e;
        }
    }

    // 可能有多个Header
    private Map<String, String> convertHeaders(Header[] headers) {
        Map<String, String> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for(int i = 0; i < headers.length; i++) {
            result.put(headers[i].getName(), headers[i].getValue());
        }
        return result;
    }

    private byte[] entityToBytes(HttpEntity entity) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            is = entity.getContent();
            byte[] buffer = new byte[1024 * 8];
            int len ;

            while ((len = is.read(buffer)) != -1) {
                bos.write(buffer, 0 , len);
            }
            bos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            return null;

        }finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
