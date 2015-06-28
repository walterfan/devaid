/*
 * Copyright (c) jwhat (China) Software Co., Ltd. HeFei Branch No. 308
 * Xiangzhang Drive, Hefei New and High Technology Area, Hefei, Anhui, China All
 * Rights Reserved.
 */
package com.github.walterfan.devaid.webmonitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * Web Handler base class
 * 
 * @version 1.0 2 June 2008
 * @author Walter Fan Ya Min
 */
public class BaseWebMonitorHandler implements WebHandler {

    private static Log logger = LogFactory.getLog(BaseWebMonitorHandler.class);

    private static int maxHandlerNum = 100;
    
    private static AtomicInteger handleNumber = new AtomicInteger(0);

    protected WebMonitor monitor = null;

    protected WebRender render = null;
    
    protected static final String CONTENT_TYPE = "text/html; charset=utf-8";
    
    protected static final String CHARSET = "UTF-8";

    protected static final String DEFAULT_RESPONSE = "Welcome to Web Service";
    
    private boolean needAuth;
    
    private String path = "/";
    // to avoid client query frequently,unit is millisecond
    private static final long QUERY_INTERVAL_MS = 10;

    public BaseWebMonitorHandler() {

    }

    public BaseWebMonitorHandler(String path) {
        setPath(path);
    }

    public BaseWebMonitorHandler(WebMonitor monitor, WebRender render) {
        this.monitor = monitor;
        this.render = render;
    }

    
    public String getPath() {
        return path;
    }

    
    public void setPath(String path) {
        this.path = path;

    }
    
   
    public void onHandle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        MonitorInfo queryInfo = getRequestInfo(request);
        
        logger.debug("query: "+ queryInfo);
        
        if (monitor != null && render != null) {
            MonitorInfo monitorInfo = monitor.queryMonitorInfo(queryInfo);
            writeResponse(render.render(monitorInfo),response);
        } else {
            writeResponse(DEFAULT_RESPONSE, response);
        }

    }

    protected MonitorInfo getRequestInfo(HttpServletRequest request) throws IOException {
        MonitorInfo queryInfo = new MonitorInfo();
        Map<String, String[]> map = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            String[] arrPara = entry.getValue();
            if(ArrayUtils.isEmpty(arrPara)) {
                continue;
            }
            if(arrPara.length==1) {
                queryInfo.putInfo(entry.getKey(), arrPara[0]);
            } else {
                queryInfo.putInfo(entry.getKey(), arrPara);
            }
        }
        
        int contentLength = request.getContentLength();
        if( contentLength > 0) {
            queryInfo.setContentType(request.getContentType());
            InputStream is = request.getInputStream();
            byte[] bytes = IOUtils.toByteArray(is);
            queryInfo.setContent(new String(bytes));
            IOUtils.closeQuietly(is);
        }
        return queryInfo;
    }
    protected String getDefaultContentType() {
    	return CONTENT_TYPE;
    }
    
    protected void writeResponse(String content, HttpServletResponse response) throws IOException {
    	writeResponse(content,getDefaultContentType(),  response);
    }
    
    protected void writeResponse(String content, String contentType, HttpServletResponse response) throws IOException {
        OutputStream os = null;
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(contentType);
            if(null != content) {
	            byte[] data = content.getBytes("UTF-8");
	            response.setContentLength(data.length);
	            os = response.getOutputStream();
	            os.write(data);
            }
        } catch (Exception e) {
            logger.error("writeResponse error " + e.getMessage());
        } finally {
            try {
                os.close();
            } catch (Exception e1) {
                logger.error("writeResponse os.close" +  e1.getMessage());
            }

        }

    }

    public WebMonitor getWebMonitor() {
        return monitor;
    }

    public void setWebMonitor(WebMonitor monitor) {
        this.monitor = monitor;
    }

    public WebRender getWebRender() {
        return render;
    }

    public void setWebRender(WebRender render) {
        this.render = render;
    }
    
        
	@Override
	public String toString() {
		return "BaseWebMonitorHandler [isNeedAuth=" + needAuth + ", monitor="
				+ monitor + ", path=" + path + ", render=" + render + "]";
	}

	public void handle(HttpServletRequest request, HttpServletResponse response)
		  throws ServletException, IOException {
		try {
			if (handleNumber.incrementAndGet() > maxHandlerNum) {
				writeResponse("handler number overflow "
						+ handleNumber.getAndDecrement(), response);
				return;
			}

			String method = request.getMethod();
			if (null == method) {
				return;
			}
			// judge request method, support GET/POST method for nows
			//if (method.equals(HttpMethods.GET)
			//		|| method.equals(HttpMethods.POST)) {
				onHandle(request, response);
				return;
			//}

			//response.sendError(HttpServletResponse.SC_NOT_FOUND);
			// sleep for a while to avoid query frequently
			//sleep4AWhile();
		} finally {
			handleNumber.getAndDecrement();
		}
    }

    protected void sleep4AWhile() {
        try {
            TimeUnit.MILLISECONDS.sleep(QUERY_INTERVAL_MS);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

	public boolean getNeedAuth() {
		return needAuth;
	}

	public void setNeedAuth(boolean isNeedAuth) {
		this.needAuth = isNeedAuth;
	}
	
	
}
