package com.github.walterfan.util.jms;

import java.io.Serializable;

import javax.jms.Destination;

/**
 * @author walter
 * 
 * @param <T>
 */
public interface IJmsSender {
	/**
	 * @param url
	 */
	void setConnectionUrl(String url);

	/**
	 * @param destination
	 */
	void setDestination(Destination destination);

	/**
	 * @param username
	 */
	void setUserName(String username);

	/**
	 * @param password
	 */
	void setPassword(String password);

	/**
	 * initialize sender
	 */
	void init();
	
	/**
	 * start sender
	 */
	void start() throws Exception;

	/**
	 * stop sender
	 */
	void stop();

	/**
	 * send jms
	 * @param msg
	 */
	void send(Serializable msg);
	
	void send(Serializable msg, Destination aDest);
}
