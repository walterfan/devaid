package com.github.walterfan.util.http;

import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class TestHttpHandler implements GetHandler {

	public String get(String request) {
		// TODO Auto-generated method stub
		return "test for " + request;
	}
	
}

public class SimpleHttpServer {
	private static final int POOL_MULTIPLE = 2;
	private final int port_;
	private ServerSocketChannel serverSocketChannel = null;
	private ExecutorService executorService;
	private GetHandler getHandler_; 
	
	public SimpleHttpServer(int port)  {
		this.port_ = port;
	}

	public void setGetHandler(GetHandler getHandler) {
		getHandler_ = getHandler;
	}
	
	public void service() throws Exception {
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
				.availableProcessors()* POOL_MULTIPLE);
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().setReuseAddress(true);
		serverSocketChannel.socket().bind(new InetSocketAddress(port_));
		System.out.println("server started");
			
		while (true) {
			SocketChannel socketChannel = null;
			try {
				socketChannel = serverSocketChannel.accept();
				SimpleHttpHandler handler = new SimpleHttpHandler(socketChannel);
				handler.setGetHandler(getHandler_);
				executorService.execute(handler);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) throws Exception {
		SimpleHttpServer svr = new SimpleHttpServer(8080);
		svr.setGetHandler(new TestHttpHandler());
		svr.service();
	}

	
}

