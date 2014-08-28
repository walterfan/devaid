package com.github.walterfan.devaid;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * wrapper interface of HttpHandler of monitor module
 * 
 * @version 1.0 2 June 2008
 * @author <a href="mailto:walter.fan@gmail.com">Walter Fan</a>
 */
public interface WebHandler {

    /**
     * process get request and return response
     * 
     * @param request
     *            http request
     * @param response
     *            http response
     * @throws Exception exception
     */
    void handle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException;

    /**
     * 
     * @return String
     */
    String getPath();

    /**
     * 
     * @return boolean
     */
    boolean getNeedAuth();
}
