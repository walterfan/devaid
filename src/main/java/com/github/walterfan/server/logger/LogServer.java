package com.github.walterfan.server.logger;

import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.github.walterfan.server.IServer;
import com.github.walterfan.server.logger.codec.LogCodecFactory;

public class LogServer implements IServer {
	private static Log logger = LogFactory.getLog(LogServer.class);
	private volatile boolean stopRequested = false;
	private int myPort = 2010;
	
	private IoHandler myHandler = new LogServerIoHandler();
	private IoAcceptor myAcceptor = new NioSocketAcceptor();
	
	public boolean stopRequested() {
		return stopRequested;
	}
	
	public void stopRequest() {
		stopRequested = true;
	}
	
	
	public void start() throws Exception {
		myAcceptor.getFilterChain().addLast("protocol", 
				new ProtocolCodecFilter(new LogCodecFactory(false)));
		myAcceptor.getFilterChain().addLast("logger", new LoggingFilter());
		myAcceptor.setHandler(myHandler);
	    myAcceptor.bind(new InetSocketAddress(myPort));

	    System.out.println("server is listenig at port " + myPort);

	}

	public void stop() throws Exception {
		
		myAcceptor.unbind();
	}
	
	public boolean isStarted() {
		return stopRequested;
	}
	
	public static void main(String[] args) {
		LogServer logSvr = new LogServer();
		try {
			logSvr.start();
			System.out.println("Log server started" );
			while(!logSvr.stopRequested()) {
				Thread.sleep(1000);
			}
			logSvr.stop();
		} catch (Exception e) {
			logger.error(e);
		}
		System.out.println("byebye" );
	}

}
