package com.github.walterfan.util.db;

import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * DBCP configure item: "defaultAutoCommit" 
 * "defaultReadOnly"
 * "defaultTransactionIsolation" 
 * "defaultCatalog" 
 * "driverClassName" 
 * "maxActive"
 * "maxIdle" 
 * "minIdle" 
 * "initialSize" 
 * "maxWait" 
 * "testOnBorrow" 
 * "testOnReturn"
 * "timeBetweenEvictionRunsMillis" 
 * "numTestsPerEvictionRun"
 * "minEvictableIdleTimeMillis" 
 * "testWhileIdle" 
 * "password" 
 * "url" 
 * "username"
 * "validationQuery" 
 * "accessToUnderlyingConnectionAllowed" 
 * "removeAbandoned"
 * "removeAbandonedTimeout" 
 * "logAbandoned" 
 * "poolPreparedStatements"
 * "maxOpenPreparedStatements" 
 * "connectionProperties"
 */
/**
 * dbcp connection pool wrapper
 * 
 * @author Walter Fan
 * @version 1.0 10/28/2008
 */
public class DbcpConnectionPool extends DataSourceProvider  {

    /**
     * dbcp configure item name for DBCP BasicDataSource
     */
    public static final String PROP_DRIVERCLASSNAME = "driverClassName";

    /**
     * dbcp configure item name
     */
    public static final String PROP_PASSWORD = "password";

    /**
     * dbcp configure item name
     */
    public static final String PROP_URL = "url";

    /**
     * dbcp configure item name
     */
    public static final String PROP_USERNAME = "username";

    /**
     * dbcp configure file name
     */
    public static final String DBCP_CFG_FILE = "dbcp.properties";

    /**
     * commons log
     */
    private static Log logger = LogFactory.getLog(DbcpConnectionPool.class);

    private Properties prop = new Properties();
    /**
     * dbcp datasouce
     */
    private BasicDataSource dataSource = null;
    
    public DbcpConnectionPool() {

    }
    
    public DbcpConnectionPool(DbConfig cfg) {
        setDbConfig(cfg);
    }
    /**
     * close the connection provider
     */
    @Override
    public void close() {
        if (dataSource == null) {
            return;
        }
        try {
            dataSource.close();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public synchronized DataSource getDataSource() {
        return this.dataSource;
    }
    
    private boolean isOracle10g() {
        if("oracle.jdbc.driver.OracleDriver".equalsIgnoreCase(getDbCfg().getDriverClass())) {
            return true;
        } 
        return false;        
    }
    
    public void setDbConfig(DbConfig dbCfg) {
    	super.setDbConfig(dbCfg);
    	
    	prop.setProperty(PROP_DRIVERCLASSNAME, dbCfg.getDriverClass());
        prop.setProperty(PROP_URL, dbCfg.getUrl());
        prop.setProperty(PROP_USERNAME, dbCfg.getUserName());
        prop.setProperty(PROP_PASSWORD, dbCfg.getPassword());
    }

    @Override
    public int getConnectionCount() {
        if(dataSource != null) {
            return dataSource.getNumActive();
        } else {
            return 0;
        }
            
    }
    

    @Override
    public DataSource createDataSource() throws SQLException {
        if (this.dataSource != null) {
            return this.dataSource;
        }
        
        //this.cfgProp.setProperty("oracle.jdbc.V8Compatible", "true");
        try {
            this.dataSource = (BasicDataSource)BasicDataSourceFactory.createDataSource(prop);
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
        if(isOracle10g()&& dataSource != null) {
            dataSource.addConnectionProperty("oracle.jdbc.V8Compatible", "true");
            dataSource.addConnectionProperty("oracle.net.CONNECT_TIMEOUT" , "" + getLoginTimeoutMS());
        }
        return this.dataSource;
    }
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder("DbcpConnectionPool:\n");
    	for(Map.Entry<Object, Object> entry: prop.entrySet()) {
    		sb.append( entry.getKey() + "=" + entry.getValue());
    		sb.append("\n");
    	}
    	return sb.toString();
    }

}
