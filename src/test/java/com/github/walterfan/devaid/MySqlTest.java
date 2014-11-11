package com.github.walterfan.devaid;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.io.IOUtils;

import static java.lang.System.out;

public class MySqlTest {

	public static String driverClass = "com.mysql.jdbc.Driver";
	
	public static void main(String[] args) throws Exception {
		
		
		Connection conn = null;
		try {
		    conn = createConnection();
		    if(conn==null) {
		    	throw new RuntimeException("connection is null");
		    }
		    dropTable(conn);
		    createTable(conn);
		    insertRecord(conn, new File("./images/day.png"));
		    queryTable(conn);
		    
		    dropTable(conn);
		} finally {
		    DbUtils.closeQuietly(conn);
		}
	}
	
	public static Connection createConnection() throws Exception {
	    Class.forName(driverClass).newInstance();
        Connection conn = java.sql.DriverManager
                .getConnection(
                        "jdbc:mysql://localhost:3306/wfdb?useUnicode=true&characterEncoding=UTF-8",//&autoReconnect=true&failOverReadOnly=false",
                        "walter", "pass1234");
        return conn;
	}
	
	public static void createTable(Connection conn) throws Exception {
	    String sql = "create table wfphoto(id int(11) NOT NULL auto_increment," +
	    		"name varchar(32),photo longblob, PRIMARY KEY (id))";
	    Statement stmt = conn.createStatement();
	    stmt.executeUpdate(sql);
        DbUtils.closeQuietly(stmt);
	}
	
	public static void dropTable(Connection conn)  {
        String sql = "drop table wfphoto";
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            // e.printStackTrace();
        } finally {
        DbUtils.closeQuietly(stmt);
        }
    }
	
	public static void insertRecord(Connection conn, File imgFile) throws Exception {
        String sql = "insert into wfphoto(name, photo) values(?,?)";
        if(!imgFile.isFile()) {
            throw new RuntimeException("image file does not exist.");
        }
        PreparedStatement pstmt = null;
        
        FileInputStream fis = null;
        
        try {
            pstmt = conn.prepareStatement(sql);
            
            fis = new FileInputStream(imgFile);
            pstmt.setString(1, "Walter");
            pstmt.setBinaryStream(2, fis, (int)imgFile.length());
            pstmt.executeUpdate();
        } finally {
            IOUtils.closeQuietly(fis);
            DbUtils.closeQuietly(pstmt);
            //DbUtils.closeQuietly(conn);
            
        }
    }
	
	public static void queryTable(Connection conn) throws Exception {
	    Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery("select * from wfphoto limit 10");
        while (rset.next()) {
            out.println("<tr>");
            out.println("<td>" + rset.getString(1) + "</td>");
            out.println("</tr>\n");
        }
        DbUtils.closeQuietly(rset);
        DbUtils.closeQuietly(stmt);
	}
	
	public static Image getImage(Connection conn, String imgName) throws Exception {
	    
	    PreparedStatement pstmt = null;
	    ResultSet rset = null;
	    String sql = "select photo from wfphoto where name=?";
	    try {
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, "Walter");
	        rset = pstmt.executeQuery();
            while (rset.next()) {
                Blob blob = rset.getBlob(1);
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Image myImage = toolkit.createImage(blob.getBytes(1, (int)blob.length()));
                return myImage;
            }      
	    } finally {
	        DbUtils.closeQuietly(rset);
	        DbUtils.closeQuietly(pstmt);
	    }
	    
	    return null;
	}
}

