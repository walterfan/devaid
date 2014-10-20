package com.github.walterfan.devaid.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.walterfan.util.system.OSExecute;

public class WebCmdHandler implements WebHandler {
	private static final String WRITE_DIARY = "python /workspace/cpp/cwhat/minute/dailylog.py"; 

    private static Log logger = LogFactory.getLog(WebCmdHandler.class);
	
    private static int maxHandlerNum = 1000;
    
    private static AtomicInteger handleNumber = new AtomicInteger(0);
    
    
    protected static final String CONTENT_TYPE = "text/html; charset=utf-8";
    
    protected static final String CHARSET = "UTF-8";

    protected static final String DEFAULT_RESPONSE = "Welcome to Web Service";
    
	@Override
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
			if (method.equals("GET")
					|| method.equals("POST")) {
				WebContent webReq = this.getRequestInfo(request);
				if("diary".equals(webReq.getInfo("cmd"))) {
					OSExecute.command(WRITE_DIARY);
					writeResponse("Executed " + WRITE_DIARY, response);
				} else {
					writeResponse(request.getServletPath(), response);
				}
				return;
			}

			response.sendError(HttpServletResponse.SC_NOT_FOUND);

		} finally {
			handleNumber.getAndDecrement();
		}
    

	}

	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getNeedAuth() {
		// TODO Auto-generated method stub
		return false;
	}
	
    protected WebContent getRequestInfo(HttpServletRequest request) throws IOException {
        WebContent queryInfo = new WebContent();
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

}
