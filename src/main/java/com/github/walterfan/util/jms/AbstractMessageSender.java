package com.github.walterfan.util.jms;

import javax.jms.Destination;

/**
 * @author walter
 *
 */
public abstract class AbstractMessageSender {
	
	protected String connectionUrl;
	
	protected Destination destination;
	
	protected String userName;
	
	protected String password;
	
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

	public String getConnectionUrl() {
		return connectionUrl;
	}


	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}

	public Destination getDestination() {
		return destination;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

}
