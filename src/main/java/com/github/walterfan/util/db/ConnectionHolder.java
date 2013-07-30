package com.github.walterfan.util.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * interface of ConnectionHolder
 * 
 * @author:<a href="mailto:walter.fan@gmail.com">Walter Fan</a>
 * @version 1.0 10/28/2008
 */
public interface ConnectionHolder {

    /**
     * get a connection
     * @return Connection jdbc connection
     * @throws SQLException jdbc exception
     */
    Connection getConnection() throws SQLException;
    /**
     * create a connection
     * @return Connection jdbc connection
     * @throws SQLException jdbc exception
     */
    Connection createConnection() throws SQLException;
    
    boolean isClosed();
    /**
     * close a connection
     * 
     */
    void closeConnection();
    
    /**
     * @param conn DB connection
     */
    void setConnection(Connection conn);
    /**
     * cancel the current operation on the connection
     */    
    void cancel();
    /**
     * set the current operation on the connection
     * @param stmt current statement
     */
    void setCurStmt(Statement stmt,String sql);
    
    /**
     * @return current SQL
     */
    String getCurSql();
    /**
     * commit transaction
     * @throws SQLException jdbc exception
     */
    void commit() throws SQLException;
    /**
     * rollback transaction
     * @throws SQLException jdbc exception
     */
    void rollback() throws SQLException;

}
