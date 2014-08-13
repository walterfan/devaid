package com.github.walterfan.util.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

import com.github.walterfan.util.io.ConsoleUtils;

public class JmsReceiver implements IJmsReceiver {
	
	private static Log logger= LogFactory.getLog(JmsReceiver.class);
	
	private volatile boolean  started = false;
	
	private String strUrl;
	
	private Queue destination;
	
	private SimpleMessageListenerContainer msgContainer;

	private MessageListener msgListener;
	
	private String userName;
	
	private String password;
	
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public JmsReceiver() {
		
	}
	
	public JmsReceiver(String url) {
		this.strUrl = url;
	}
	
	public void setConnectionUrl(String url) {
		this.strUrl = url;
	}
	
	public void setDestinationName(String name) {
		this.destination = new ActiveMQQueue(name);
	}
	
	public void onMessage(Message msg) {
		this.msgListener.onMessage(msg);				
	}
	
	public boolean isStarted() {
		return this.started;
	}
	
	public void start() {
		if(this.started) {
			return;
		}
		this.msgContainer.start();
		this.started = true;
	}
	
	public void stop() {
		this.msgContainer.stop();
		this.msgContainer.shutdown();
		this.started = false;
	}
	
	public void onException(JMSException arg0) {
		logger.error(arg0);
		
	}

	public void clean() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void init() throws Exception {
		if(null == this.destination || null == this.msgListener) {
			throw new RuntimeException("Have not set destination name or message listener");
		}
		
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
		factory.setBrokerURL(this.strUrl);
		if(StringUtils.isNotEmpty(userName) && StringUtils.isNotEmpty(password)) {
			factory.setUserName(userName);
			factory.setPassword(password);
		}
		
		this.msgContainer = new SimpleMessageListenerContainer();
		this.msgContainer .setConnectionFactory(factory);
		this.msgContainer .setDestination(this.destination);
		this.msgContainer .setMessageListener(this);
		
	}


	public static void main(String[] args) throws Exception {
		JmsReceiver receiver = new JmsReceiver("tcp://localhost:61616");
		receiver.setMessageListener(new MessageListener() {

			public void onMessage(Message arg0) {
				System.out.println("received: " + arg0);
				
			}
			
		});
		receiver.setDestinationName("WalterTestQueue");
		receiver.init();
		receiver.start();
		ConsoleUtils.wait4Input("stop?");
		receiver.stop();
	}

	public void setMessageListener(MessageListener listener) {
		this.msgListener = listener;
		
	}

	
}
