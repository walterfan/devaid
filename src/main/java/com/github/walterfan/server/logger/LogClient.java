package com.github.walterfan.server.logger;

import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.eclipse.jetty.io.RuntimeIOException;

import com.github.walterfan.server.logger.codec.LogCodecFactory;
import com.github.walterfan.server.logger.message.LogMessage;
import com.github.walterfan.server.logger.message.ResultMessage;

public class LogClient implements ResultListener, Runnable {
	public static final int CONNECT_TIMEOUT = 60; // seconds
	public static final int CONNECT_RETRY_TIMES = 3; // seconds
	public static final int BATCH_COUNT = 100;
	private static Log logger = LogFactory.getLog(LogClient.class);

	private String myHost;
	private int myPort;
	private volatile boolean finished = false;
	private SocketConnector myConnector = new NioSocketConnector();
	private IoHandler myHandler;
	private IoSession mySession;

	private BlockingQueue<LogMessage> myQueue = new ArrayBlockingQueue<LogMessage>(
			1000);

	public LogClient() {
		myConnector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new LogCodecFactory(true)));
		myConnector.getFilterChain().addLast("logger", new LoggingFilter());
		myHandler = new LogClientIoHandler(this);
	}

	public LogClient(String host, int port) {
		this();
		this.myHost = host;
		this.myPort = port;
	}

	public void connect() {
		for (int i = 0;; i++) {
			try {
				myConnector.setHandler(myHandler);
				ConnectFuture connectFuture = myConnector.connect(new InetSocketAddress(myHost, myPort));
				connectFuture.join(CONNECT_TIMEOUT);
				mySession = connectFuture.getSession();
				break;
			} catch (RuntimeIOException e) {
				logger.error("Failed to connect.", e);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					// ignore;
				}
				if (CONNECT_RETRY_TIMES == i) {
					throw e;
				}
			}
		}

	}

	public void disconnect() {
		if (mySession != null) {
			mySession.close().join(CONNECT_TIMEOUT);
			mySession = null;
			logger.info("disconnected");
		}
	}

	public void sendRequest(LogMessage logMsg) {
		if (mySession == null) {
			logger.error("not connected");
		} else {
			myQueue.offer(logMsg);
			//mySession.write(logMsg);
		}
	}

	public void run() {
		while (!finished) {
			LogMessage logMsg = myQueue.poll();
			if (null != logMsg) {
				logger.info("send " + logMsg.getSequence());
				mySession.write(logMsg);
				continue;
			}
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {
				// ignore
			}

		}
	}
	
	public boolean isFinished() {
		return this.finished;
	}

	public static void main(String[] args) {

		LogClient client = new LogClient("127.0.0.1", 2010);

		client.connect();
		Thread th = new Thread(client);
		th.start();
		logger.info("--- send begin ---" + BATCH_COUNT);
		long beginTime = System.currentTimeMillis();
		for (int i = 0; i < BATCH_COUNT; i++) {
			LogMessage logMsg = getLogMessage(i);
			try {
				client.sendRequest(logMsg);
			} catch (Exception e) {
				logger.error("connect/send error: ", e);
			}
		}
		try {
			th.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("--- send end ---"
				+ (System.currentTimeMillis() - beginTime));
		client.disconnect();

	}

	private static LogMessage getLogMessage(int min) {
		LogMessage logMsg = new LogMessage();
		for (int i = 0; i < 10; i++) {
			LogData data0 = new LogData();
			data0.setContent("test log " + (min + i));
			data0.setOffset(10 * (min + i));
			data0.setLogType("logService");
			data0.setTimestamp(System.currentTimeMillis());
			
			logMsg.add(data0);
		}
		logMsg.setSequence(min);
		return logMsg;
	}

	public void onResult(ResultMessage msg) {
		logger.info("received "  + msg);
	/*	if (myQueue.isEmpty()) {
			return;
		}
		LogMessage logMsg = myQueue.remove();*/
		if(msg.getSequence() == BATCH_COUNT -1) {
			finished = true;
		}
	}

	public void onException(Throwable throwable) {
		logger.error("exception: ", throwable);

	}
}
