package com.github.walterfan.util.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * interface of ConnectionOperator
 * 
 * @author Walter Fan
 * @version 1.0 10/28/2008
 */
public interface ConnectionProvider {
    
    void setDbConfig(DbConfig dbCfg);
    /**
     * get Connection from DB
     * @return DB connection
     * @throws Exception DB exception
     */
    Connection getConnection() throws SQLException;

    /**
     * get how many connections
     * @return connection count
     */
    int getConnectionCount();

    /**
     * close the connection provider
     */
    void close();
}
