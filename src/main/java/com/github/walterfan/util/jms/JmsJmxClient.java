/*
 * Client.java - JMX client that interacts with the JMX agent. It gets
 * attributes and performs operations on the Hello MBean and the QueueSampler
 * MXBean example. It also listens for Hello MBean notifications.
 */

package com.github.walterfan.util.jms;

import java.util.LinkedList;
import java.util.List;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.apache.activemq.broker.jmx.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author walter
 *
 */
public class JmsJmxClient {
	private String connectUrl = "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi";
	private String domain = "org.apache.activemq";
	private JMXServiceURL jmxUrl;
	private JMXConnector jmxConnctor;
	private MBeanServerConnection jmxConn;
	
	public JmsJmxClient() {	
		
	}
	
	public JmsJmxClient(String strUrl) {	
		this.connectUrl = strUrl;
	}
	
	public void setConnectUrl(String connectUrl) {
		this.connectUrl = connectUrl;
	}

	public void connect() throws Exception {
		this.jmxUrl =  new JMXServiceURL(connectUrl);
		this.jmxConnctor = JMXConnectorFactory.connect(jmxUrl, null);
		this.jmxConn = this.jmxConnctor.getMBeanServerConnection();
	}
	
	public void close() throws Exception {
		if(null != this.jmxConnctor) {
			this.jmxConnctor.close();
		}
	}
	
	public BrokerViewMBean getBrokerViewMBean(String brokerName) throws Exception {
		String objName = domain + ":BrokerName=" + brokerName + ",Type=Broker";
		ObjectName mbeanName = new ObjectName(objName);       
		return JMX.newMBeanProxy(jmxConn, mbeanName, BrokerViewMBean.class, true);    	
	}
    
	public QueueViewMBean getQueueViewMBean(String brokerName, String queueName) throws Exception {
		String objName = domain + ":BrokerName=" + brokerName +",Type=Queue,Destination=" + queueName;
		ObjectName mbeanName = new ObjectName(objName);
		return JMX.newMBeanProxy(jmxConn, mbeanName, QueueViewMBean.class, true);    	
	}
	
	public List<QueueViewMBean> getQueueViewMBeans(String brokerName) throws Exception {
		ObjectName[] arrObj = getBrokerViewMBean(brokerName).getQueues();
		if(null == arrObj) {
			return null;
		}
		List<QueueViewMBean> aList = new LinkedList<QueueViewMBean>();
		int len = "Destination=".length();
		for(ObjectName objName: arrObj) {
			//System.out.println("Queue: " + objName);
			int pos = StringUtils.indexOf("" + objName, "Destination=");
			if(pos < 0) {
				continue;
			}
			QueueViewMBean vb = getQueueViewMBean(brokerName,
					StringUtils.substring("" + objName, pos + len));
			if(null == vb) {
				continue;
			}
			aList.add(vb);
		}
		return aList;	
	}
	
	public static void main(String[] args) throws Exception {
		
		//System.setProperty("Dcom.sun.management.remote.port", "1616");
		//System.setProperty("Dcom.sun.management.remote.authenticate", "false");
		//System.setProperty("Dcom.sun.management.remote.ssl", "false");
		
		JmsJmxClient jmxClient = new JmsJmxClient();
		jmxClient.connect();
		
		/*ObjectName[] arrObj = jmxClient.getBrokerViewMBean("MdpJmsService").getQueues();
		for(ObjectName objName: arrObj) {
			System.out.println("Queue: " + objName);
			int pos = StringUtils.indexOf("" + objName, "Destination=");
			if(pos < 0) {
				continue;
			}
			System.out.println("\tEnqueueCount: " + jmxClient.getQueueViewMBean("MdpJmsService", 
						StringUtils.substring("" + objName, pos + 12)).getEnqueueCount());
			System.out.println("\tDequeueCount: " + jmxClient.getQueueViewMBean("MdpJmsService", 
					StringUtils.substring("" + objName, pos + 12)).getDequeueCount());
		}*/
		
		List<QueueViewMBean> aList = jmxClient.getQueueViewMBeans("MdpJmsService");
		for(QueueViewMBean vb: aList) {
			System.out.println(vb.getName()+": ");
			System.out.println("\tMemoryLimit: " + FileUtils.byteCountToDisplaySize(vb.getMemoryLimit()));
			System.out.println("\tMemoryUsagePortion: " + vb.getMemoryUsagePortion());
			System.out.println("\tConsumerCount: " + vb.getConsumerCount());
			System.out.println("\tEnqueueCount: " + vb.getEnqueueCount());
			System.out.println("\tDequeueCount: " + vb.getDequeueCount());
		}
		jmxClient.close();
	}
}
