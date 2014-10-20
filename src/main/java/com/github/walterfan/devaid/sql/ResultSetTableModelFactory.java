package com.github.walterfan.devaid.sql;

import java.sql.*;

import javax.swing.table.*;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;

import com.github.walterfan.util.db.DbConfig;

/**
 * This class encapsulates a JDBC database connection and, given a SQL query as
 * a string, returns a ResultSetTableModel object suitable for display in a
 * JTable Swing component
 **/
public class ResultSetTableModelFactory {

    Connection conn; // Holds the connection to the database

    DbConfig dbCfg = null;

    public ResultSetTableModelFactory() {

    }

    public ResultSetTableModelFactory(DbConfig dbCfg)
            throws ClassNotFoundException {
        setDbCfg(dbCfg);
    }

    /** The constructor method uses the arguments to create db Connection */
    public ResultSetTableModelFactory(String driverClassName, String url,
            String username, String password) throws ClassNotFoundException,
            SQLException {
        this(new DbConfig(driverClassName, url, username, password));
    }

    public TableModel executeSqlBlock(String sqlBlock) throws SQLException {
        checkConnection();
        CallableStatement cstmt = (CallableStatement) conn
                .prepareCall(sqlBlock);
        boolean ret = cstmt.execute(sqlBlock);
        return new KeyValueTableModel("executed", ret);
    }

    /**
     * This method takes a SQL query, passes it to the database, obtains the
     * results as a ResultSet, and returns a ResultSetTableModel object that
     * holds the results in a form that the Swing JTable component can use.
     **/
    public TableModel execute4TableModel(String query) throws SQLException {
        checkConnection();

        String sql = StringUtils.lowerCase(query);
        if (sql.startsWith("commit")) {
            conn.commit();
            return new KeyValueTableModel("Commited", "Successful");
        } else if (sql.startsWith("rollback")) {
            conn.rollback();
            return new KeyValueTableModel("Rollbacked", "Successful");
        } else if (StringUtils.contains(dbCfg.getDriverClass(),
                "org.sqlite.JDBC")) {
            Statement stmt = conn.createStatement();
            boolean status = stmt.execute(query);
            if (status) { // it was a query and returns a ResultSet
                ResultSet rs = stmt.getResultSet(); // Get results
                return new ResultTableModel(rs);
            } else {
                int cnt = stmt.getUpdateCount();
                return new KeyValueTableModel("UpdateCount", cnt);
            }

        } else {
            // it's TYPE_FORWARD_ONLY, CONCUR_READ_ONLY by default
            // Create a Statement that will be used to excecute the query.
            // The arguments specify that the returned ResultSet will be
            // scrollable, read-only, and insensitive to changes in the db.
            Statement stmt = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            // Run the query, creating a ResultSet
            ResultSet rs = null;
            boolean status = stmt.execute(query);
            if (status) { // it was a query and returns a ResultSet
                rs = stmt.getResultSet(); // Get results
                return new ResultSetTableModel(rs);
            } else {
                int cnt = stmt.getUpdateCount();
                return new KeyValueTableModel("UpdateCount", cnt);
            }

        }
    }

    private void checkConnection() throws SQLException, IllegalStateException {
        // If we've called close(), then we can't call this method
        // DbUtils.closeQuietly(connection);
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(dbCfg.getUrl(),
                    dbCfg.getUserName(), dbCfg.getPassword());
        }
        if (conn == null) {
            throw new IllegalStateException("Connection cannot be established.");
        } else {
            conn.setAutoCommit(false);
        }
    }

    public void setDbCfg(DbConfig dbCfg) throws ClassNotFoundException {
        this.dbCfg = dbCfg;
        Class<?> driver = Class.forName(dbCfg.getDriverClass());
    }

    /**
     * Call this method when done with the factory to close the DB connection
     **/
    public void close() {
        DbUtils.closeQuietly(conn);
        conn = null;
    }

    /** Automatically close the connection when we're garbage collected */
    protected void finalize() {
        close();
    }

    /**
     * @return the dbCfg
     */
    public DbConfig getDbCfg() {
        return dbCfg;
    }

}
