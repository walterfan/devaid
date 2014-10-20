package com.github.walterfan.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.StatusLine;

/*
 * TODO: how to deal with the kind of response:
 * Content-disposition: APPLICATION/OCTET-STREAM; charset=utf-8");
 * Content-disposition" "attachment; filename=\"" + fileName + "\""); 
 * */
public class HttpResponse {

    private Header[] headers;

    private StatusLine statusLine;

    private String body = null;

    /**
     * Default Constructor
     */
    public HttpResponse() {

    }

    /**
     * Constructor
     * 
     * @param statusCode
     *            int
     * @param body
     *            String
     */
    public HttpResponse(StatusLine statusLine, String body) {
        this.statusLine = statusLine;
        this.body = body;
    }

    public HttpResponse(StatusLine statusLine, byte[] bytes) {
        this.statusLine = statusLine;
        this.body = new String(bytes);
    }

    public HttpResponse(StatusLine statusLine, InputStream is) {
        this.statusLine = statusLine;
        this.body = convertStreamToString(is);
    }

    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    /**
     * 
     * @return String
     */
    public String toString() {
        StringBuffer buf = new StringBuffer(100);
        buf.append("statusLine:");
        buf.append(this.statusLine);

        buf.append(", content:");
        buf.append(this.body);

        return buf.toString();
    }
}