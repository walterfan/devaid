package com.github.walterfan.util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * GW query runner to support current statement
 * 
 * @author Walter Fan
 * @version 1.0 10/28/2008
 */
public class DbQueryRunner extends QueryRunner {
    private static Log logger = LogFactory.getLog(DbQueryRunner.class);
    /**
     * Connection holder
     */
    private ConnectionHolder holder = null;
    
    private int queryTimeout = 0;
    
    private int fetchSize = 0;
    /**
     * constructor
     */
    public DbQueryRunner() {
        super();
    }

    /**
     * @param ds data source
     */
    public DbQueryRunner(DataSource ds) {
        super(ds);
    }

    /**
     * @param holder connection holder
     */
    public DbQueryRunner(ConnectionHolder holder) {
        this.holder = holder;
    }
    
    /* (non-Javadoc)
     * @see org.apache.commons.dbutils.QueryRunner#prepareStatement(java.sql.Connection, java.lang.String)
     */
    protected PreparedStatement prepareStatement(Connection conn, String sql)
    throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);
        if(holder != null) {
            holder.setCurStmt(stmt,sql);
            logger.debug("current sql is " + sql);
        }
        if (queryTimeout > 0) {
            stmt.setQueryTimeout(queryTimeout);
        }
        if(fetchSize>0){  
            stmt.setFetchSize(fetchSize);  
        }  
        return stmt;
    }
    
    /* (non-Javadoc)
     * @see org.apache.commons.dbutils.QueryRunner#close(java.sql.Statement)
     */
    protected void close(Statement stmt) throws SQLException {        
        if(holder != null) {
            logger.debug("close sql " + holder.getCurSql());            
            holder.setCurStmt(null,null); 
        }
        DbUtils.close(stmt);
        
    }
    public int getQueryTimeout() {
        return queryTimeout;
    }

    
    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

	public int getFetchSize() {
		return fetchSize;
	}

	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}
    
    
}
