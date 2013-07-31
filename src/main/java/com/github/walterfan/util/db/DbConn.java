package com.github.walterfan.util.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static java.lang.System.out;

/**
 * class: DbConn
 * 
 * @author Walter fan
 * @version:1.0
 * @see jdbc API
 */
public class DbConn implements ConnectionHolder {

    /**
     * logger of the class
     */
    private static Log logger = LogFactory.getLog("DbConn.class");

    /**
     * conneciton provider
     */
    private ConnectionProvider provider = null;

    /**
     * db connection it hold
     */
    private Connection conn = null;

    /**
     * current statement by the above connection
     */
    private Statement curStmt = null;
    private String curSql = null;
    /**
     * debug status
     */
    private boolean debug = false;

    public DbConn(Connection conn) {
        this.conn = conn;
    }
    /**
     * @param provider ConnectionProvider
     */
    public DbConn(ConnectionProvider provider) {
        this.provider = provider;
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }
    /**
     * (non-Javadoc)
     * @see cn.fanyamin.util.db.ConnectionHolder#createConnection()
     * @return DB connection
     */
    public Connection createConnection() throws SQLException {
		if (provider == null) {
			throw new RuntimeException("Please set connection provider firstly");
		}

		if (this.conn != null && !this.conn.isClosed()) {
			conn.close();
		}
		this.conn = provider.getConnection();
		if (this.conn != null) {
			this.conn.setAutoCommit(false);
		}

		return this.conn;
	}

    /**
     * (non-Javadoc)
     * @see cn.fanyamin.util.db.ConnectionHolder#closeConnection()
     */
    public void closeConnection() {
        DbUtils.closeQuietly(this.conn);
        this.conn = null;
    }

    public boolean isClosed() {        
        if (this.conn == null ) {
            return true;
        }
        
        try {
            return this.conn.isClosed();                        
        } catch (SQLException e) {
            logger.error(e);
            return true;
        }
    }
    /**
     * (non-Javadoc)
     * @see cn.fanyamin.util.db.ConnectionHolder#getConnection()
     * @return db connection
     */
    public Connection getConnection() {
        return this.conn;
    }

    /**
     * @see
     * cn.fanyamin.util.db.ConnectionHolder#setCurStatement(java.sql.
     * Statement)
     * @param stmt statement
     */
    public synchronized void setCurStmt(Statement stmt, String sql) {
        this.curStmt = stmt;
        this.curSql = sql;
    }
    
    /* (non-Javadoc)
     * @see cn.fanyamin.jdbc.ConnectionHolder#getCurSql()
     */
    public String getCurSql() {
        return this.curSql;
    }

    /**
     * @throws SQLException sql exception
     */
    public void commit() throws SQLException {
        if (conn != null) {
            conn.commit();
        }
    }

    /**
     * @throws SQLException sql exception
     */
    public void rollback() throws SQLException {
        if (conn != null) {
            conn.rollback();
        }
    }

    /**
     * @see cn.fanyamin.util.db.ConnectionHolder#cancel()
     */
    public void cancel() {
        if (curStmt != null) {
            try {
                logger.info("Cancel " + curSql);
                curStmt.cancel();
            } catch (Exception e) {
                logger.error(e);
            }
        } else {
            logger.debug("curStmt is null");
        }
    }

    /**
     * method: close close database connection,etc
     */
    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                logger.error(e);
            }

        }
    }

    /**
     * @param sql
     *            input sql
     * @throws Exception
     *             if db exception
     */
    public void execute(String sql) throws Exception {
        if (conn == null) {
            return;
        }
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            setCurStmt(stmt,sql);
            boolean status = stmt.execute(sql);
            do {
                if (status) { // it was a query and returns a ResultSet
                    rs = stmt.getResultSet(); // Get results
                    
                        DbHelper.printResultsTable(rs, out); // Display them
                    
                } else {
                    int numUpdates = stmt.getUpdateCount();
                    if (this.debug) {
                        logger.info("Ok. " + numUpdates + " rows affected.");
                    }
                }
                status = stmt.getMoreResults();
            } while (status || stmt.getUpdateCount() != -1);
        } finally { // print out any warnings that occurred
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(stmt);
            
            setCurStmt(null,null);
        }
    }

    /**
     * @param args
     *            none
     */
    public static void main(String[] args) {

        if(args.length < 3) {
            System.out.println("Arguments: url username password");
            return;
        }
        
        DbConfig dbConn = new DbConfig(args[0], args[1],args[2]);
        ConnectionProvider provider = new DriverManagerProvider();
        DbConn dc = new DbConn(provider);

        try {
            if (dc.createConnection() == null) {
                logger.info("getConnection error, please check the specified jdbc parameters");
                return;
            }
            
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
            out.print("SQL> "); // prompt the user
            out.flush(); // make the prompt appear now.
            String strsql = in.readLine(); // get a line of input from user
            if ((strsql == null) || strsql.equals("")) {
                strsql = "SELECT to_char(sysdate,'mm/dd/yy hh24:mi:ss') as now from dual";
            }
            dc.execute(strsql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dc.closeConnection();
        }
    } 
}

