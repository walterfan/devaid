package com.github.walterfan.util.jms;

import java.io.Serializable;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;


/**
 * @author walter
 *
 */
public class JmsSender extends AbstractMessageSender implements IJmsSender {
	
	private int recvTimeoutMS = 5000;
	
	private int idleTimeoutMS = 30000;
	
	private long timeToLive = 5000;
	
	private JmsTemplate jmsTemplate = new JmsTemplate();
	
	private PooledConnectionFactory factory;
	
	private volatile boolean isStarted;

	
	public JmsSender() {
		
	}
	
	
	
	public void setRecvTimeoutMS(int recvTimeoutMS) {
		this.recvTimeoutMS = recvTimeoutMS;
	}


	public void setIdleTimeoutMS(int idleTimeoutMS) {
		this.idleTimeoutMS = idleTimeoutMS;
	}


	public void setTimeToLive(long timeToLive) {
		this.timeToLive = timeToLive;
	}

	public void init() {

		ActiveMQConnectionFactory factory0 = new ActiveMQConnectionFactory();			
		if(StringUtils.isNotEmpty(this.userName) 
				&& StringUtils.isNotEmpty(this.password)) {
			factory0.setUserName(this.userName);
			factory0.setPassword(this.password);
		}
		factory0.setBrokerURL(connectionUrl);
		this.factory = new PooledConnectionFactory(factory0);			
		this.factory.setIdleTimeout(idleTimeoutMS);
		this.jmsTemplate.setConnectionFactory(factory);
		this.jmsTemplate.setReceiveTimeout(recvTimeoutMS);
		//this.jmsTemplate.setExplicitQosEnabled(true);
		this.jmsTemplate.setTimeToLive(timeToLive);
	}

	public void send(final Serializable logMsg)  {
		this.send(logMsg, this.destination);
	}
	
	public void send(final Serializable logMsg, Destination aDest) {
		
		jmsTemplate.send(aDest, new MessageCreator() {

			public Message createMessage(Session session) throws JMSException {
				ObjectMessage msg = session.createObjectMessage(logMsg);				
				return msg;
			}			
		});
	}
	
	public void start() {
		if(isStarted) {
			return;
		}
		
		factory.start();			
		isStarted = true;
	}
	
	public void stop()  {
		if(!isStarted) {
			return;
		}

		factory.stop();			
		isStarted = false;
		
	}
}
