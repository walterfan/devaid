package com.github.walterfan.util.jms;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * Another implementation, not use spring jms template
 * 
 * @author walter
 *
 */
public class JmsSender2 extends AbstractMessageSender implements IJmsSender {
	private static Log logger = LogFactory.getLog(JmsSender.class);

	private boolean transacted = true;
	private boolean persistent = true;

	private long timeToLive = 5000;
	
	private ConnectionFactory factory;

	private Connection connectionToClose = null;
	
	private volatile boolean isStarted;
	
	
	public void init() {
		ActiveMQConnectionFactory factory0 = new ActiveMQConnectionFactory(connectionUrl);
		if(StringUtils.isNotEmpty(this.userName) 
				&& StringUtils.isNotEmpty(this.password)) {
			factory0.setUserName(this.userName);
			factory0.setPassword(this.password);
		}
		
		this.factory = factory0;
	}
	
	public void send(final Serializable logMsg)  {
		this.send(logMsg, this.destination);
	}
	
	public void send(final Serializable logMsg, Destination dest) {
        
        Session sessionToClose   = null;
        MessageProducer producer = null;
        try {           
            // Create the session
            sessionToClose = connectionToClose.createSession(transacted, Session.AUTO_ACKNOWLEDGE);

            // Create the producer.
            producer = sessionToClose.createProducer(destination);
            if (persistent) {
                producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            } else {
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            }
            if (timeToLive != 0) {
                producer.setTimeToLive(timeToLive);
            }
            ObjectMessage msg = sessionToClose.createObjectMessage(logMsg);
            producer.send(msg);
            if (transacted) {
                sessionToClose.commit();
            }
            
        } catch (Exception e) {
           logger.error("send error: ", e);
           
        }finally {
			if(null != producer) {
				try {
					producer.close();
					producer = null;
				} catch (JMSException e) {
					logger.error(e.getMessage());
				}
			}
			if(null != sessionToClose) {
				try {
					sessionToClose.close();
					sessionToClose = null;
				} catch (JMSException e) {
					logger.error(e.getMessage());
				}
			}
			
		}
    
	}
	
		
	public void start() throws JMSException {
		if(isStarted) {
			return;
		}

		connectionToClose = factory.createConnection();
		connectionToClose.start();

		isStarted = true;
	}
	
	public void stop()  {
		if(!isStarted) {
			return;
		}
		
	
		if(null != connectionToClose) {
			try {
				connectionToClose.stop();
				connectionToClose.close();
			} catch(JMSException e) {
				logger.error(e);
			}
		}			
		isStarted = false;
		
	}
}
