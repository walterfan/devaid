package com.github.walterfan.util.db;

import java.sql.Connection;

import java.sql.SQLException;
import java.sql.Statement;


import org.apache.commons.dbutils.ResultSetHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * db operator class
 * 
 * @author Walter Fan
 * @version 1.0 10/28/2008
 */
public class DbOperator implements ConnectionOperator {

    /**
     * logger of this class
     */
    private static Log logger = LogFactory.getLog(DbOperator.class);

    /**
     * connection holder
     */
    private ConnectionHolder connHolder = null;

    /**
     * query runner
     */
    private DbQueryRunner runner = null;

    /**
     * @param holder
     *            connection holder
     */
    public DbOperator(ConnectionHolder holder) {
        this.connHolder = holder;
        this.runner = new DbQueryRunner(holder);
    }

    /**
     * constructor
     */
    public DbOperator(Connection conn) {
        if(conn == null) {
            throw new IllegalArgumentException("Connection is null");
        }
        this.connHolder = new DbConn(conn);
        this.runner = new DbQueryRunner(connHolder);
    }

    /**
     * constructor
     */
    public DbOperator(ConnectionProvider provider) {
        this.connHolder = new DbConn(provider);
        this.runner = new DbQueryRunner(connHolder);
    }
    /*public WFDbOperator() {
        this.connHolder = new DbConn();
        this.runner = new WFQueryRunner(connHolder);
    }*/

    /*
     * (non-Javadoc)
     * @see
     * cn.fanyamin.jdbc.ConnectionOperator#batch(java.lang.String,
     * java.lang.Object[][])
     */
    
    public int[] batch(String sql, Object[][] params) throws SQLException {
        return runner.batch(getConnection(), sql, params);
    }

    /*
     * (non-Javadoc)
     * @see
     * cn.fanyamin.jdbc.ConnectionOperator#query(java.lang.String,
     * org.apache.commons.dbutils.ResultSetHandler)
     */
    
    public Object query(String sql, ResultSetHandler rsh) throws SQLException {
        return runner.query(getConnection(), sql, rsh);
    }

    /*
     * (non-Javadoc)
     * @see
     * cn.fanyamin.jdbc.ConnectionOperator#query(java.lang.String,
     * java.lang.Object, org.apache.commons.dbutils.ResultSetHandler)
     */
    
    public Object query(String sql, Object param, ResultSetHandler rsh) throws SQLException {
        return runner.query(getConnection(), sql, param, rsh);
    }

    /*
     * (non-Javadoc)
     * @see
     * cn.fanyamin.jdbc.ConnectionOperator#query(java.lang.String,
     * java.lang.Object[], org.apache.commons.dbutils.ResultSetHandler)
     */
    
    public Object query(String sql, Object[] params, ResultSetHandler rsh) throws SQLException {
        return runner.query(getConnection(), sql, params, rsh);
    }

    /*
     * (non-Javadoc)
     * @see
     * cn.fanyamin.jdbc.ConnectionOperator#update(java.lang.String)
     */
    
    public int update(String sql) throws SQLException {
        return runner.update(getConnection(), sql);
    }

    /*
     * (non-Javadoc)
     * @see
     * cn.fanyamin.jdbc.ConnectionOperator#update(java.lang.String,
     * java.lang.Object)
     */
    
    public int update(String sql, Object param) throws SQLException {
        return runner.update(getConnection(), sql, param);
    }

    /*
     * (non-Javadoc)
     * @see
     * cn.fanyamin.jdbc.ConnectionOperator#update(java.lang.String,
     * java.lang.Object[])
     */
    
    public int update(String sql, Object... params) throws SQLException {
        return runner.update(getConnection(), sql, params);
    }

    /*
     * (non-Javadoc)
     * @see
     * cn.fanyamin.jdbc.ConnectionHolder#setCurStmt(java.sql.
     * Statement)
     */
    
    public void setCurStmt(Statement stmt, String sql) {
        if (connHolder == null) {
            return;
        }
        connHolder.setCurStmt(stmt,sql);

    }

    public String getCurSql() {
        if (connHolder == null) {
            return null;
        }
        return connHolder.getCurSql();
    }
    /* (non-Javadoc)
     * @see cn.fanyamin.jdbc.ConnectionHolder#setConnection(java.sql.Connection)
     */
    public void setConnection(Connection conn) {
        this.connHolder.setConnection(conn);
    }    
    /*
     * (non-Javadoc)
     * @see cn.fanyamin.jdbc.ConnectionHolder#cancel()
     */
    
    public void cancel() {
        if (connHolder == null) {
            return;
        }
        
        connHolder.cancel();
        
    }

    /*
     * (non-Javadoc)
     * @see cn.fanyamin.jdbc.ConnectionHolder#closeConnection()
     */
    
    public void closeConnection() {
        if (connHolder == null) {
            return;
        }
        connHolder.closeConnection();
    }

    /*
     * (non-Javadoc)
     * @see cn.fanyamin.jdbc.ConnectionHolder#commit()
     */
    
    public void commit() throws SQLException {
        if (connHolder == null) {
            return;
        }
        connHolder.commit();

    }

    /*
     * (non-Javadoc)
     * @see cn.fanyamin.jdbc.ConnectionHolder#getConnection()
     * @throws SQLException sql exception
     */
    
    public Connection getConnection() throws SQLException {
        if (connHolder == null) {
            throw new IllegalStateException("connHolder is null");
        }
        Connection conn = connHolder.getConnection();
        if(conn == null) {
            throw new SQLException("connection is null");
        }
        return conn;
    }

    /*
     * (non-Javadoc)
     * @see cn.fanyamin.jdbc.ConnectionHolder#createConnection()
     */
    
    public Connection createConnection() throws SQLException {
        if (connHolder == null) {
            throw new SQLException("connHolder is null");
        }
        return connHolder.createConnection();
        
    }

    /*
     * (non-Javadoc)
     * @see cn.fanyamin.jdbc.ConnectionHolder#rollback()
     */
    
    public void rollback() throws SQLException {
        if (connHolder == null) {
            return;
        }
        connHolder.rollback();
    }

    public boolean isClosed() {
        if (connHolder == null) {
            return true;
        }
        return connHolder.isClosed();
    }
    
    public void setQueryTimeout(int seconds) {
        if(runner!=null) {
            runner.setQueryTimeout(seconds);
        }
    }
    
    public void setFetchSize(int fetchSize) {
        if(runner!=null) {
            runner.setFetchSize(fetchSize);
        }
    }
}
