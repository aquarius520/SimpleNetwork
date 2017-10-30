package com.aquarius.simple.network;

import org.apache.http.*;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.HttpConnectionParams;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aquarius on 2017/10/29.
 */
public class HurlStack implements HttpStack {

    private final SSLSocketFactory mSslSocketFactory;

    public HurlStack() {
        this(null);
    }

    public HurlStack(SSLSocketFactory mFactory) {
        this.mSslSocketFactory = mFactory;
    }

    @Override
    public HttpResponse performRequest(Request request) throws IOException {

        String url = request.getUrl();

        Map<String, String> map = new HashMap<>();
        map.putAll(request.getHeaders());

        URL parsedUrl = new URL(url);
        HttpURLConnection conn = openConnection(request, parsedUrl);
        // 添加请求头部信息到HttpUrlConnection中
        for (String headName : map.keySet()) {
            conn.addRequestProperty(headName, map.get(headName));
        }
        // 针对不同的请求方式做不同的处理
        setConnectionParametersForRequest(conn, request);

        ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
        int responseCode = conn.getResponseCode();

        if (responseCode == -1) {
            // -1 is returned by getResponseCode() if the response code could not be retrieved.
            // Signal to the caller that something was wrong with the connection.
            throw new IOException("Could not retrieve response code from HttpUrlConnection.");
        }

        StatusLine statusLine = new BasicStatusLine(protocolVersion, conn.getResponseCode(), conn.getResponseMessage());
        BasicHttpResponse response = new BasicHttpResponse(statusLine);

        response.setEntity(entityFromConnection(conn));

        for (Map.Entry<String, List<String>> header : conn.getHeaderFields().entrySet()) {
            if (header.getKey() != null) {
                Header h = new BasicHeader(header.getKey(), header.getValue().get(0));
                response.addHeader(h);
            }
        }

        return response;
    }

    private HttpEntity entityFromConnection(HttpURLConnection connection) {
        BasicHttpEntity entity = new BasicHttpEntity();
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            inputStream = connection.getErrorStream();
        }
        entity.setContent(inputStream);
        entity.setContentLength(connection.getContentLength());
        entity.setContentEncoding(connection.getContentEncoding());
        entity.setContentType(connection.getContentType());
        return entity;
    }

    private HttpURLConnection openConnection(Request request, URL url) throws IOException{
        // 创建HttpUrlConnection对象
        HttpURLConnection conn = createConnection(url);

        int timeOutMs = (int) request.getTimeOutMs();
        conn.setConnectTimeout(timeOutMs);
        conn.setReadTimeout(timeOutMs);
        conn.setUseCaches(false);
        /** A URL connection can be used for input and/or output.  Set the DoInput
            flag to true if you intend to use the URL connection for input,
            false if not.  The default is true.
         * */
        conn.setDoInput(true);

        //
        if ("https".equalsIgnoreCase(url.getProtocol()) && mSslSocketFactory != null) {
            ((HttpsURLConnection)conn).setSSLSocketFactory(mSslSocketFactory);
        }

        return conn;
    }

    private HttpURLConnection createConnection(URL url) throws IOException{
        return  (HttpURLConnection) url.openConnection();
    }

    /** 设置请求方法 参数等 */
    private void setConnectionParametersForRequest(HttpURLConnection connection, Request request) throws IOException {
        switch (request.getMethod()) {
            case Request.Method.DEPRECATED_GET_OR_POST:
                // This is the deprecated way that needs to be handled for backwards compatibility.
                // If the request's post body is null, then the assumption is that the request is
                // GET.  Otherwise, it is assumed that the request is a POST.
                byte[] postBody = request.getPostBody();
                if (postBody != null) {
                    // Prepare output. There is no need to set Content-Length explicitly,
                    // since this is handled by HttpURLConnection using the size of the prepared
                    // output stream.
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.addRequestProperty(Constant.Header.HEADER_CONTENT_TYPE, request.getBodyContentType());
                    DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
                    dos.write(postBody);
                    dos.close();
                }
                break;

            case Request.Method.GET:
                // Not necessary to set the request method because connection defaults to GET but
                // being explicit here.
                connection.setRequestMethod("GET");
                break;

            case Request.Method.POST:
                connection.setRequestMethod("POST");
                addBodyIfExists(connection, request);
                break;

            case Request.Method.DELETE:
                connection.setRequestMethod("DELETE");
                break;

            case Request.Method.PUT:
                connection.setRequestMethod("PUT");
                addBodyIfExists(connection, request);
                break;

            case Request.Method.HEAD:
                connection.setRequestMethod("HEAD");
                break;

            case Request.Method.OPTIONS:
                connection.setRequestMethod("OPTIONS");
                break;

            case Request.Method.TRACE:
                connection.setRequestMethod("TRACE");
                break;

            case Request.Method.PATCH:
                connection.setRequestMethod("PATCH");
                addBodyIfExists(connection, request);
                break;

            default:
                throw new IllegalStateException("Unknown method type.");
        }
    }

    private void addBodyIfExists(HttpURLConnection connection, Request request) throws IOException {
        byte[] body = request.getBody();
        if (body != null) {
            connection.setDoOutput(true);
            connection.addRequestProperty(Constant.Header.HEADER_CONTENT_TYPE, request.getBodyContentType());
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(body);
            out.close();
        }

    }
}
