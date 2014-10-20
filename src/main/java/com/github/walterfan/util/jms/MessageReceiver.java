package com.github.walterfan.util.jms;


import java.util.concurrent.TimeUnit;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.github.walterfan.util.io.ConsoleUtils;

/**
 * @author walter
 *
 */
public class MessageReceiver implements IJmsReceiver {
	private static Log logger = LogFactory.getLog(MessageReceiver.class);
	private volatile boolean  isStarted_ = false;
	private String serviceName = "messageReceiver";
	private MessageListener messageListener_;
	
	private AbstractMessageListenerContainer container;
	
	public MessageReceiver() {

	}
	
	public void setMessageListener(MessageListener listener) {
		this.messageListener_ = listener;
	}
 	
	public void setContainer(AbstractMessageListenerContainer aContainer) {
		this.container = aContainer;
	}
	
	public void onMessage(Message msg) {
		messageListener_.onMessage(msg);	
	}

	public boolean isStarted() {
		return this.isStarted_;
	}
	
	public void start() {
		if(this.isStarted_) {
			return;
		}
		this.container.start();
		this.isStarted_ = true;
		logger.info(getName() + " started");
	}
	
	public void stop() {
		if(!this.isStarted_) {
			return;
		}
		ConnectionFactory factory = this.container.getConnectionFactory();
		if(factory instanceof PooledConnectionFactory) {
			((PooledConnectionFactory)factory).stop();	
		}				
		this.container.stop();
		
		this.isStarted_ = false;
		logger.info(getName() + " stopped");
	}
	

	public void clean() throws Exception {
		this.container.shutdown();
		logger.info(getName() + " shutdowned");
	}


	public String getName() {
		return serviceName;
	}

	public void setName(String name) {
		this.serviceName = name;
	}

	public void init() throws Exception {
		this.container.initialize();
		
	}
	//for testing
	void init(String brokerUrl, String queueName) {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
		factory.setBrokerURL(brokerUrl);
		
		ActiveMQQueue queue = new ActiveMQQueue(queueName);
		//AbstractMessageListenerContainer aContainer = new SimpleMessageListenerContainer();
		DefaultMessageListenerContainer aContainer = new DefaultMessageListenerContainer();
		aContainer.setConnectionFactory(factory);
		aContainer.setDestination(queue);
		//aContainer.setSessionTransacted(true);
		aContainer.setSessionAcknowledgeMode(ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE);
		//Note that any ordering guarantees are lost once multiple consumers are registered.
		//In general, stick with 1 consumer for low-volume queues
		aContainer.setConcurrentConsumers(1);
		aContainer.setCacheLevel(DefaultMessageListenerContainer.CACHE_NONE);
		aContainer.setMessageListener(this);
		aContainer.setExceptionListener(this);
		
		this.setContainer(aContainer);
		this.container.initialize();
	}

	public void onException(JMSException jmsexception) {
		logger.error("JMS Error: " + jmsexception.getMessage());
		
	}
	
	static int receivedCount = 0;
	
	public static void main(String[] args) throws Exception {
		
		MessageReceiver receiver = new MessageReceiver();
		receiver.init("tcp://10.224.57.155:61616", "WalterTestQueue");
		receiver.setMessageListener(new MessageListener() {

			public void onMessage(Message msg) {
				receivedCount ++;
				try {
					System.out.println(receivedCount + "received: " + msg);
					if(msg instanceof ObjectMessage) {
						ObjectMessage objMsg = (ObjectMessage)msg;
						System.out.println("object: " + objMsg.getObject());
						if(1 == receivedCount || 3 == receivedCount) {
							objMsg.acknowledge();
							System.out.println("ack: " + objMsg.getObject());
						}
					} else if(msg instanceof TextMessage) {
						TextMessage txtMsg = (TextMessage)msg;
						System.out.println("object: " + txtMsg.getText());
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
		//receiver.setDestinationName("USERDATAQUEUE_QZWD");
		receiver.init();
		receiver.start();
		TimeUnit.SECONDS.sleep(1);
		receiver.stop();
	}

}
