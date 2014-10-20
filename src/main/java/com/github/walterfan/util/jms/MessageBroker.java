package com.github.walterfan.util.jms;

import org.apache.activemq.broker.BrokerService;

import com.github.walterfan.service.IServer;
import com.github.walterfan.util.io.ConsoleUtils;
import com.github.walterfan.util.net.SocketUtils;

/**
 * @author walter
 *
 */
public class MessageBroker implements IServer {

	private volatile boolean  started = false;
	
	private String hostAddr = SocketUtils.getLocalhostIP();

	private int listenPort = 61616;
	
	private BrokerService broker;
	
	public MessageBroker() {
		broker = new BrokerService();
		broker.setUseJmx(true);
        
	}

	public void setHostAddr(String hostAddr) {
		this.hostAddr = hostAddr;
	}

	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}

	public boolean isStarted() {
		return this.started;
	}


	public void start() throws Exception {
		if(this.started) {
			return;
		}

		broker.addConnector("tcp://" + hostAddr + ":" + this.listenPort);
        broker.start();
		this.started = true;
	}


	public void stop() throws Exception {
		broker.stop();
		this.started = false;
	}
	
	public String getName() {
		return "MessageBroker";
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		MessageBroker mb = new MessageBroker();
		mb.setHostAddr("127.0.0.1");
		mb.start();
		ConsoleUtils.wait4Input("stop?");
		mb.stop();

	}

}
