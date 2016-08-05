package com.github.walterfan.devaid.webmonitor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;



public class JettyHandlerAdapter extends AbstractHandler{
    private WebHandler webHandler = null;
    private static Log logger = LogFactory.getLog(JettyHandlerAdapter.class);
    
    public JettyHandlerAdapter(WebHandler handler) {
        this.webHandler = handler;
    }
    
    public String getPath() {
        if(webHandler!=null) {
            return webHandler.getPath();
        } else {
            return "/";
        }
    }
    
    public WebHandler getWebHandler() {
        return webHandler;
    }
    
    //@Override
	public void handle(String target, Request base_request, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
    
         if (response.isCommitted() || base_request.isHandled()) {
            return;
        }
        base_request.setHandled(true);
        if(webHandler!=null) {
            try {
                webHandler.handle(request, response);
            } catch(IOException e) {
                throw e;
            } catch(ServletException e) {
                throw e;
            } catch(Exception e) {
                logger.error("handle error ", e);
            }
        }
        
    }

	public String toString() {
		if (null == webHandler) {
			return super.toString();
		} else {
			return webHandler.toString();
		}
	}


}
