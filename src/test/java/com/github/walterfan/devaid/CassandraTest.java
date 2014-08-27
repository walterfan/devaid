package com.github.walterfan.devaid;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;


public class CassandraTest {
   private Cluster cluster;
   private Session session = null;
   
   public void connect(String node) {
      cluster = Cluster.builder()
            .addContactPoint(node)
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
   
   public ResultSet executeSql(String sql) {
	   if(null == session) return null;
	   return  session.execute(sql);
	   
   }

   public void close() {
      cluster.close();
   }


}