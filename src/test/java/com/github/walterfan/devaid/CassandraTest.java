package com.github.walterfan.devaid;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;


public class CassandraTest {
   private Cluster cluster;

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
   }

   public void close() {
      cluster.close();
   }

   public static void main(String[] args) {
	   CassandraTest client = new CassandraTest();
       client.connect("10.224.57.163");
       client.close();
   }
}