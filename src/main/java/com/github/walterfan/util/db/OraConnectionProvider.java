package com.github.walterfan.util.db;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import oracle.jdbc.pool.OracleDataSource;
/**
 * oracle connection provider
 * 
 * @author Walter Fan
 * @version 1.0 10/28/2008
 */
public class OraConnectionProvider 
    extends DataSourceProvider  {

    public final static int LOGIN_TIMEOUT = 30;
    
    private OracleDataSource dataSource = null;
    
    private static Log logger = LogFactory.getLog(OraConnectionProvider.class);
    
    public OraConnectionProvider() {

    }
    
    public OraConnectionProvider(DbConfig dbCfg) {
        setDbConfig(dbCfg);
    }
    
    public OraConnectionProvider(String url, String user, String pwd) {
        setDbConfig(new DbConfig("oracle.jdbc.driver.OracleDriver",url, user, pwd));
    }
    
    @Override
    public int getConnectionCount() {
        return ConnectionProxy.getConnNum();
    }
       
    @Override
    public Connection getConnection() 
        throws SQLException {        
        Connection conn = super.getConnection();
        if (conn != null) {
            conn = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),
                                                       new Class[] { Connection.class }, new ConnectionProxy(conn));
            conn.setAutoCommit(false);
        }
        
        
        return conn;        
    }
    
   
    @Override
    public void close() {
        
        if (dataSource != null) {
            try {
                this.dataSource.close();         
                ConnectionProxy.setConnNum(0);
            } catch(SQLException e) {
                logger.error(e);
            }
        }

    }            
    
    public DataSource getDataSource() {
        return dataSource;
    }
    
    /*public static void main(String[] args) {
        ConnectionProvider provider = new OraConnectionProvider();
        //provider.setDriverClass("");
        Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rset = null;
        try {
            conn = provider.getConnection();
            stmt = conn.createStatement();
            rset = stmt.executeQuery("select sysdate from dual");
            while(rset.next()) {
                System.out.println("result="+rset.getObject(1));
            }
            System.out.println("connection count = " + provider.getConnectionCount());
        } catch (Exception e) {
            e.printStackTrace();            
        } finally {
            org.apache.commons.dbutils.DbUtils.closeQuietly(rset);
            org.apache.commons.dbutils.DbUtils.closeQuietly(stmt);
            org.apache.commons.dbutils.DbUtils.closeQuietly(conn);
        }
        System.out.println("connection count = " + provider.getConnectionCount());
    }
*/
    @Override
    public DataSource createDataSource() throws SQLException {
        if(this.dataSource != null) {
            return this.dataSource;
        }
        
        Properties prop = new Properties(); 
        prop.setProperty("oracle.jdbc.V8Compatible", "true");                
        dataSource = new OracleDataSource();                
        dataSource.setUser(dbConfig.getUserName());
        dataSource.setPassword(dbConfig.getPassword());
        dataSource.setURL(dbConfig.getUrl());        
        dataSource.setLoginTimeout(getLoginTimeoutMS());
        dataSource.setConnectionProperties(prop);
        return this.dataSource;
    }
}
