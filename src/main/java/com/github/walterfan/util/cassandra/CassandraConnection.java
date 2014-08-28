package com.github.walterfan.util.cassandra;

import java.util.List;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class CassandraConnection {
    private Cluster cluster;    
    private Session session;
    

    public void connect(String nodes) {
    	cluster = Cluster.builder()
                .addContactPoints(nodes.split(","))
                .build();
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n", 
              metadata.getClusterName());
        for ( Host host : metadata.getAllHosts() ) {
           System.out.printf("Datatacenter: %s; Host: %s; Rack: %s\n",
                 host.getDatacenter(), host.getAddress(), host.getRack());
        }
        
        session = cluster.connect();
     }

    public void close() {
        cluster.close();
    }

    
     public ResultSet executeSql(String sql) {
  	   if(null == session) return null;
  	   return  session.execute(sql);
  	   
     }

     public static String resultSetToString(ResultSet rs) {
    	 StringBuilder sb = new StringBuilder();
    	 List<Row> list = rs.all();
    	 for(Row row: list) {
    		 ColumnDefinitions defs = row.getColumnDefinitions();
    		 int colSize = defs.size(); 
    		 for(int i=0; i < colSize; ++i ) {
    			 DataType dataType = defs.getType(i);
    			 if(dataType == DataType.bigint()||dataType == DataType.decimal()) {
    				 sb.append(row.getLong(i));
    			 } /*else if(dataType == DataType.cint()) {
    				 sb.append(row.getInt(i));
    			 }*/ else if(dataType == DataType.text()) {
    				 sb.append(row.getString(i));
    			 } else {
   					 sb.append(row.getBytesUnsafe(i));

    			 }
    			 sb.append(", ");
    		 }
    		 sb.append("\n");
    	 }
    	 return sb.toString();
     }

     
     public static void main(String args[]) {
    	 CassandraConnection conn = new CassandraConnection();
    	 conn.connect("10.224.57.165,10.224.57.166,10.224.57.167");
    	 ResultSet rs = conn.executeSql("select * from waltertest");
    	 System.out.println(rs.toString());
    	 System.out.println(resultSetToString(rs));
    	 conn.close();
     }
}
