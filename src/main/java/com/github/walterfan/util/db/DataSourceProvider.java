package com.github.walterfan.util.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;




/**
 * @author walter
 *
 */
public abstract class DataSourceProvider implements ConnectionProvider {
  
    private int loginTimeoutMS = 3000;
    private int retryTimes = 1;
    private int retryIntervalSec = 1;
    protected DbConfig dbConfig;
    
    
    public void close() {
        
    }

       
    public int getConnectionCount() {
        return 0;
    }


    public abstract DataSource getDataSource();
    
    public abstract DataSource createDataSource()  throws SQLException;
     
      
    /** 
     * get a connection from DB
     * @return DB Connection
     * @throws Exception for db issue
     */
    public Connection getConnection() throws SQLException {        
        DataSource ds = getDataSource();
        if(ds == null) {
            ds = this.createDataSource();
        }
        if (ds == null) {
            return null;
        }

        int retryTime = 0;
        Connection conn = null;
        do {            
            conn = ds.getConnection();
            if(conn == null) {                
                try {
                    TimeUnit.SECONDS.sleep(retryIntervalSec);
                } catch (InterruptedException e) {
                    throw new SQLException(e.getMessage());
                }                 
            } else {
                conn.setAutoCommit(false);
            }
        } while (conn == null && ++retryTime <= retryTimes);
        
        return conn;
    }



    
    /**
     * @return the loginTimeoutMS
     */
    public int getLoginTimeoutMS() {
        return loginTimeoutMS;
    }

    
    /**
     * @param loginTimeoutMS the loginTimeoutMS to set
     */
    public void setLoginTimeoutMS(int loginTimeoutMS) {
        this.loginTimeoutMS = loginTimeoutMS;
    }

    
    /**
     * @return the retryTimes
     */
    public int getRetryTimes() {
        return retryTimes;
    }

    
    /**
     * @param retryTimes the retryTimes to set
     */
    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    
    /**
     * @return the retryIntervalSec
     */
    public int getRetryIntervalSec() {
        return retryIntervalSec;
    }

    
    /**
     * @param retryIntervalSec the retryIntervalSec to set
     */
    public void setRetryIntervalSec(int retryIntervalSec) {
        this.retryIntervalSec = retryIntervalSec;
    }

    
    /**
     * @return the dbCfg
     */
    public DbConfig getDbCfg() {
        return dbConfig;
    }

    
    /**
     * @param dbCfg the dbCfg to set
     */
    public void setDbConfig(DbConfig dbCfg) {
        this.dbConfig = dbCfg;
    }

    @Override
    public String toString() {
    	return "DataSourceProvider: " + this.dbConfig.toString();
    }
}
