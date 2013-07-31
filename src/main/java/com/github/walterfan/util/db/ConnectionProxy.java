package com.github.walterfan.util.db;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * connection proxy
 * 
 * @author Walter Fan
 * @version 1.0 10/28/2008
 */

class ConnectionProxy implements java.lang.reflect.InvocationHandler {    
    private Object obj = null;
    private static int connNum = 0;
    private Log logger = LogFactory.getLog(ConnectionProxy.class);
    public ConnectionProxy(Object obj) {
        this.obj = obj;
        if(obj instanceof Connection && obj!=null) {
            connNum++;
        }
    }
    
    public static int getConnNum() {
        return connNum;
    }
    
    public static void setConnNum(int num) {
        connNum=num;
    }

    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
        Object result = null;
        try {            
            result = m.invoke(obj, args);
            if(obj instanceof Connection && m.getName().equals("close")){
                connNum--;                
            }
            logger.debug("invoke method "+ m.getName());
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (Exception e) {
            throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
        } 
        return result;
    }
    
   
}
