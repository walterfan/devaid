package com.github.walterfan.util.net;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author walter
 *
 */
public final class SocketUtils {
    private static final Log logger = LogFactory.getLog(SocketUtils.class);
    private static String localhostIP = "";
    
    private SocketUtils() {
        
    }
    
    /**
     * @return localhost's IP
     */
    public static synchronized String getLocalhostIP() {
        if("".equals(localhostIP)) {
            localhostIP = "127.0.0.1";
            try {
                localhostIP = InetAddress.getLocalHost().getHostAddress();
            } catch (Exception e) {
                logger.error("getLocalHost error", e);
            }
            return localhostIP;
        }
        return localhostIP;
        
    }
    
    public static void close(Socket sock) {
    	if(sock!=null) {
    		try {
				sock.close();
			} catch (IOException e) {}
    	}
    }
    
    public static void close(ServerSocket sock) {
    	if(sock!=null) {
    		try {
				sock.close();
			} catch (IOException e) {}
    	}
    }
    
    public static void close(DatagramSocket dsock) {
    	if(dsock != null) {
    		dsock.close();
    	}
    }
    
    public static void disconnect(DatagramSocket dsock) {
    	if(dsock != null) {
    		dsock.disconnect();
    	}
    }
}
