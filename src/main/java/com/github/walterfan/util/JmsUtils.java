package com.github.walterfan.util;


import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import javax.jms.MessageListener;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.github.walterfan.util.jms.JmsSender;


public class JmsUtils {
	public static void sendMessage(String url, String dest, String userName, String password,
			Serializable msg) {
		JmsSender jmsSender = new JmsSender();
	
		if(StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
			jmsSender.setUserName(userName);
			jmsSender.setPassword(password);
		}
		
		try {
			jmsSender.setConnectionUrl(url);
			jmsSender.setDestination(new ActiveMQQueue(dest));
			jmsSender.init();
			jmsSender.start();
			jmsSender.send(msg);
			jmsSender.stop();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	
	public static PooledConnectionFactory createPooledConnectionFactory(String url, String userName, String password) {
		ActiveMQConnectionFactory factory  = new ActiveMQConnectionFactory();
		factory.setBrokerURL(url);
		if(StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
			factory.setUserName(userName);
			factory.setPassword(password);
		}
		
		PooledConnectionFactory pool = new PooledConnectionFactory();
		pool.setConnectionFactory(factory);
		return pool;
	}
	
	public static DefaultMessageListenerContainer createContainer(String url, String dest, String userName, String password,
			MessageListener listener) {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
		factory.setBrokerURL(url);
		factory.setUserName(userName);
		factory.setPassword(password);		
		
		DefaultMessageListenerContainer aContainer = new DefaultMessageListenerContainer();
		aContainer.setConnectionFactory(new PooledConnectionFactory(factory));
		aContainer.setDestination(new ActiveMQQueue(dest));
		aContainer.setMessageListener(listener);
		aContainer.setSessionTransacted(true);
		//Note that any ordering guarantees are lost once multiple consumers are registered.
		//In general, stick with 1 consumer for low-volume queues
		aContainer.setConcurrentConsumers(1);
		aContainer.setCacheLevel(DefaultMessageListenerContainer.CACHE_NONE);
		return aContainer;
	}
	
	public static String readString(ObjectInput in, int len) throws IOException {
    	if(len > 0) {
    		byte[] bytes = new byte[len];		
			in.read(bytes, 0, len);
			return new String(bytes);
    	} else {
    		return null;
    	}
    }
    
    public static void writeString(ObjectOutput buf, String str) throws IOException {

		if(null==str) {
			buf.writeInt(0);
		} else {			
			byte[] bytes = str.getBytes(); 
			buf.writeInt(bytes.length);
			buf.write(bytes);
		}
    }
}
