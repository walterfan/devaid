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
    private boolean isConnected;

    public void connect(String nodes, String username, String password) {
    	if(isConnected) {
    		return;
    	}
    	cluster = Cluster.builder()
                .addContactPoints(nodes.split(","))
                .withCredentials(username, password)
                .build();
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n", 
              metadata.getClusterName());
        for ( Host host : metadata.getAllHosts() ) {
           System.out.printf("Datatacenter: %s; Host: %s; Rack: %s\n",
                 host.getDatacenter(), host.getAddress(), host.getRack());
        }
        
        session = cluster.connect();
        this.isConnected = true;
     }

    public void close() {
        cluster.close();
        this.isConnected = false;
    }

    
     public ResultSet executeSql(String sql) {
  	   if(null == session) return null;
  	   return  session.execute(sql);
  	   
     }

     public static String resultSetToString(ResultSet rs) {
    	 StringBuilder sb = new StringBuilder();
    	 List<Row> list = rs.all();
    	 int titleFlag = 0;
    	 for(Row row: list) {
    		 ColumnDefinitions defs = row.getColumnDefinitions();
    		 int colSize = defs.size();
    		 
			 if(titleFlag == 0) {
				 for(int i=0; i < colSize; ++i ) {
					 sb.append(defs.getName(i));
					 sb.append("(");
					 sb.append(defs.getType(i).getName());
					 sb.append("), ");
				 }
				 sb.append("\n");
				 sb.append("------------------------------------------------------------------------------------\n");
				 titleFlag = 1;
			 }    		 


    		 for(int i=0; i < colSize; ++i ) {
    			 DataType dataType = defs.getType(i);
    			 
    			 if(dataType == DataType.bigint()||dataType == DataType.decimal()) {
    				 sb.append(row.getLong(i));
    			 } else if(dataType == DataType.cint()) {
    				 sb.append(row.getInt(i));
    			 } else if(dataType == DataType.text() ||dataType == DataType.varchar()) {
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


}
