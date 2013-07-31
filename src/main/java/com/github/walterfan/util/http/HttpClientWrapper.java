package com.github.walterfan.util.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author walter
 *
 */
public class HttpClientWrapper {
    /**
     *  default content type
     */
    private static final String DEFAULT_CHARSET = "utf-8";
    /**
     *  default char set
     */
    private static final String DEFAULT_CONTENT_TYPE = "text/xml";
    /**
     *  Default dummy ssl socket factory
     */
    private static final String DEFAULT_SSL_SOCKET_FACTORY = "cn.fanyamin.util.http.DummySSLSocketFactory";
    /**
     * Default ssl port
     */
    private static final int DEFAULT_SSL_PORT = 443;
    /**
     * Default protocol is https
     */
    private static final String DEFAULT_PROTOCOL = "https";
    /**
     *  Default timeout is 1 minute, unit is ms
     */
    private static final int DEFAULT_TIMEOUT_MS = 60000;
    /**
     * Default retry count
     */
    private static final int DEFAULT_RETRY_COUNT = 1;

    private static final Map<String, String> SOAP_MAP;
    
    private static Log logger = LogFactory.getLog(HttpClientWrapper.class);
    
    private static final int SSL_PORT = DEFAULT_SSL_PORT;
    
    private static final String STR_HTTPS = DEFAULT_PROTOCOL;
    
    //private String siteUrl = "http://127.0.0.1:1975/webapp";
   
    private int timeout = DEFAULT_TIMEOUT_MS; // default is 1 min
   
    private int retryCount = DEFAULT_RETRY_COUNT; // default is 1, not to retry
    
    private final HttpClient httpClient = new HttpClient();
    
    private boolean checkSslFlag = true;
 
    private HttpConnectionManager httpConnMgr = new MultiThreadedHttpConnectionManager();
    
    
    public void setCheckSslFlag(boolean checkSslFlag) {
        this.checkSslFlag = checkSslFlag;
    }


    public boolean isCheckSslFlag() {
        return checkSslFlag;
    }



    static {
        SOAP_MAP = new HashMap<String,String>();
        SOAP_MAP.put("SOAPAction", "");
    }
    
    public HttpClientWrapper() {
        httpClient.setHttpConnectionManager(httpConnMgr);
    }
    
    public HttpClientWrapper(String strUrl) {

        httpClient.getParams().setSoTimeout(timeout);
        httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                                        new DefaultHttpMethodRetryHandler(retryCount, false));
        httpClient.setHttpConnectionManager(httpConnMgr);
        supportSsl(strUrl);
    }



    public void setTimeout(int timeout) {
        this.timeout = timeout;
        this.httpClient.getParams().setSoTimeout(timeout);
    }


    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
        this.httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(retryCount, false));
    }


    public void setDefaultMaxConnectionsPerHost(int maxHostConnections) {
        httpConnMgr.getParams().setDefaultMaxConnectionsPerHost(maxHostConnections);
    }

    public void setMaxTotalConnections(int maxTotalConnections) {   
        httpConnMgr.getParams().setMaxTotalConnections(maxTotalConnections);
    } 
    
    private void supportSsl(String url) {
        if (StringUtils.isBlank(url)) {
            return;
        }
        String siteUrl = StringUtils.lowerCase(url);
        if (!(siteUrl.startsWith(STR_HTTPS))) {
            return;
        }
        
        try {
            this.setSslProtocol(siteUrl);
        } catch (Exception e) {
            logger.error("setProtocol error ", e);
        }
        Security.setProperty("ssl.SocketFactory.provider", 
        DEFAULT_SSL_SOCKET_FACTORY);
    }
    //added by walter to support https
    public void setSslProtocol(String strUrl) throws Exception {
        
        URL url = new URL(strUrl);
        String host = url.getHost();
        int port = url.getPort();

        if (port <= 0) {
            port = SSL_PORT;
        }
        setSslHostPort(host, port);

    }  
    
    public void setSslHostPort(String host, int port) {
        ProtocolSocketFactory factory = new SSLSocketFactory();
        Protocol authhttps = new Protocol(STR_HTTPS, factory, port);
        Protocol.registerProtocol(STR_HTTPS, authhttps);
        // set https protocol
        this.httpClient.getHostConfiguration().setHost(host, port, authhttps); 
    }
    
    public HttpResponse doGet(String url, Map<String, String> paraMap, Map<String, String> headMap) throws IOException {
        if(this.isCheckSslFlag()) {
            supportSsl(url);
        }
        GetMethod httpMethod = new GetMethod(url);
        
        if (paraMap != null) {
            NameValuePair[] params = map2NameValuePairs(paraMap);        
            httpMethod.setQueryString(params);
        }
        if (headMap != null) {
            for (Map.Entry<String, String> entry : headMap.entrySet()) {
                httpMethod.setRequestHeader(entry.getKey(), entry.getValue());
            }            
        }
        return doHttpRequest(httpMethod);
    }
    
    public HttpResponse doGet(String url) throws IOException {
        return this.doGet(url, null);
    }
    
    public HttpResponse doGet(String url, Map<String, String> paraMap) throws IOException {       
        return doGet(url, paraMap, null);
    }


    public HttpResponse doPost(String url, Map<String, String> paraMap, Map<String, String> headMap) throws IOException {
        if(this.isCheckSslFlag()) {
            supportSsl(url);
        }
        PostMethod httpMethod = new PostMethod(url);
        if (paraMap != null) {
            for (Map.Entry<String, String> entry : paraMap.entrySet()) {
                httpMethod.setParameter(entry.getKey(), entry.getValue());
                
            }
        }
        if (headMap != null) {
            for (Map.Entry<String, String> entry : headMap.entrySet()) {
                httpMethod.setRequestHeader(entry.getKey(), entry.getValue());
                
            }
        }
        return doHttpRequest(httpMethod);
    }
    
    public HttpResponse doPost(String url, String form) throws IOException {
        return doPost(url, form, null);
    }

    public HttpResponse doPost(String url, Map<String, String> paraMap) throws IOException {
        return doPost(url, paraMap, null);
    }
    
    public HttpResponse doPut(String url, String request) throws IOException {
        if(this.isCheckSslFlag()) {
            supportSsl(url);
        }
        
        PutMethod httpMethod = new PutMethod(url);
        StringRequestEntity entity = new StringRequestEntity(request,"text/xml","utf-8");
        httpMethod.setRequestEntity(entity);
        return doHttpRequest(httpMethod);
    }
    
    public HttpResponse doDelete(String url,String request) throws IOException {
        if(this.isCheckSslFlag()) {
            supportSsl(url);
        }
        
        DeleteMethod httpMethod = new DeleteMethod(url);
           
        return doHttpRequest(httpMethod);
    }

    
    public HttpResponse doSoapPost(String url, String form) throws IOException {
        return doPost(url, form, SOAP_MAP);
    }
	
    public HttpResponse doPost(String url, String form, Map<String, String> headMap) throws IOException  {
        if(this.isCheckSslFlag()) {
            supportSsl(url);
        }
        PostMethod httpMethod = new PostMethod(url);
        try {
            StringRequestEntity entity = new StringRequestEntity(form, DEFAULT_CONTENT_TYPE, DEFAULT_CHARSET);
            httpMethod.setRequestEntity(entity);
            if (headMap != null) {
                for (Map.Entry<String, String> entry : headMap.entrySet()) {
                    httpMethod.setRequestHeader(entry.getKey(), entry.getValue());
                    
                }
            }
            //httpMethod.setRequestHeader("SOAPAction", "");
            return doHttpRequest(httpMethod);
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }
        return null;
    }
    /**
     * 
     * @param method HttpMethod
     * @return HttpResponse
     * @throws IOException -
     */
    public synchronized HttpResponse doHttpRequest(HttpMethod method) throws IOException {

        try {
            //method.setURI(new URI(siteUrl,false));
            method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
            if (logger.isDebugEnabled()) {
                logger.debug("Begin do a Http Request. URI:" + method.getURI());
            }
            httpClient.executeMethod(method);
            HttpResponse response = new HttpResponse(method.getStatusLine(), method.getResponseBodyAsStream());
            if (logger.isDebugEnabled()) {
                logger.debug("Http Response:" + response);
            }
            return response;

        } finally {
            method.releaseConnection();
        }
    }
    
    
    private NameValuePair[] map2NameValuePairs(Map<String, String> paraMap) {
        ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : paraMap.entrySet()) {
            NameValuePair pair = new NameValuePair(entry.getKey(), entry.getValue());
            paramList.add(pair);
        }
        
        NameValuePair[] params = new NameValuePair[paramList.size()];
        for (int i = 0; i < paramList.size(); i++) {
            params[i] = paramList.get(i);
        }
        return params;
    }

}
