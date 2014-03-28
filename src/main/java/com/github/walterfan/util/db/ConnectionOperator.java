package com.github.walterfan.util.db;

import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

/**
 * interface of ConnectionOperator
 * 
 * @author Walter Fan
 * @version 1.0 10/28/2008
 */
public interface ConnectionOperator extends ConnectionHolder {

    /**
     * Executes the given SELECT SQL query and returns a result object.
     * 
     * @param sql
     *            input sql
     * @param rsh
     *            ResultSetHandler the callback obj to get data from resultset
     * @return result
     * @throws SQLException
     *             jdbc exception
     */
    Object query(String sql, ResultSetHandler rsh) throws SQLException;

    /**
     * Executes the given SELECT SQL with a single replacement parameter
     * 
     * @param sql
     *            input sql
     * @param param
     *            replacement parameter
     * @param rsh
     *            ResultSetHandler the callback obj to get data from resultset
     * @return result
     * @throws SQLException
     *             jdbc exception
     */
    Object query(String sql, Object param, ResultSetHandler rsh) throws SQLException;

    /**
     * Execute an SQL SELECT query with replacement parameters
     * 
     * @param sql
     *            input sql
     * @param params
     *            replacement parameters
     * @param rsh
     *            ResultSetHandler the callback obj to get data from resultset
     * @return result
     * @throws SQLException
     *             jdbc exception
     */
    Object query(String sql, Object[] params, ResultSetHandler rsh) throws SQLException;

    /**
     * Execute an SQL INSERT, UPDATE, or DELETE query.
     * 
     * @param sql
     *            the input sql
     * @return affected rows
     * @throws SQLException
     *             jdbc exception
     */
    int update(String sql) throws SQLException;

    /**
     * @param sql
     *            the input sql
     * @param param
     *            replacement parameter
     * @return affected rows
     * @throws SQLException
     *             jdbc exception
     */
    int update(String sql, Object param) throws SQLException;

    /**
     * @param sql
     *            the input sql
     * @param params
     *            replacement parameters
     * @return affected rows
     * @throws SQLException
     *             jdbc exception
     */
    int update(String sql,Object... params) throws SQLException;

    /**
     * Execute a batch of SQL INSERT, UPDATE, or DELETE queries.
     * 
     * @param sql
     *            the input sql
     * @param params
     *            replacement parameters
     * @return affected rows
     * @throws SQLException
     *             jdbc exception
     */
    int[] batch(String sql, Object[][] params) throws SQLException;
    void setQueryTimeout(int seconds);

}
