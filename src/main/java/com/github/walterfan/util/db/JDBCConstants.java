package com.github.walterfan.util.db;

/**
 * constant class of jdbc module
 * 
 * @author Walter Fan
 * @version 1.0 10/28/2008
 */
public final class JDBCConstants {

    private JDBCConstants() {

    }

    /**
     *  jdbc driver item name in configure file
     */
    public static final String DB_DRIVER = "jdbc.driverClassName";
    /**
     *  jdbc url item name in configure file
     */
    public static final String DB_URL = "jdbc.url";
    /**
     *  jdbc user item name in configure file
     */
    public static final String DB_USER = "jdbc.username";
    /**
     *  jdbc password item name in configure file
     */
    public static final String DB_PWD = "jdbc.password";
    /**
     *  jdbc configure file path and name
     */
    public static final String CFG_FILE = "jdbc.properties";

    /**
     *  if need reconnect db 
     */
    public static final String AUTO_RECONN = "jdbc.autoReconn";
    
    /**
     *  the interval of reconnect
     */
    public static final String RETRY_INTERVAL = "jdbc.retryInterval";
    
    /**
     *  the times of reconnect
     */
    public static final String RECONN_TIMES= "jdbc.reconnTimes";
    
}
