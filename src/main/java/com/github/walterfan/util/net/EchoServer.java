package com.github.walterfan.util.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.walterfan.service.IServer;


/**
 * @author Walter
 *
 */
public class EchoServer implements IServer, Runnable {
	private class EchoHandler implements SocketHandler {
		private Socket clientSocket_;
		
		public EchoHandler(Socket clientSocket) {
			this.clientSocket_ = clientSocket;
		}
		
		public void run() {
			handle(clientSocket_);
		}
		
		public void handle(Socket aSocket) {
			try {
				os = new BufferedWriter(new OutputStreamWriter(clientSocket_
						.getOutputStream()));
				is = new BufferedReader(new InputStreamReader(clientSocket_
						.getInputStream()));
			
				String line = is.readLine();
				if (line != null) {
					if(EchoServer.logger_.isDebugEnabled()) {
						EchoServer.logger_.debug(line);
					}
					os.write(line, 0, line.length());
					os.flush();
				}
			} catch (IOException e) {
				logger_.error("I/O error while processing client's print file.");
			} finally {
				IOUtils.closeQuietly(is);
				IOUtils.closeQuietly(os);
				SocketUtils.close(clientSocket_);
			}

		}

	}
	
	public static final int LISTEN_PORT = 2010;
	private static  Log logger_ = LogFactory.getLog(EchoServer.class);
	
	private ServerSocket serverSocket_;
	private int port_ = LISTEN_PORT;
	private Thread mainThread_ = new Thread(this);
	private volatile boolean stopRequest_ = false;
	private ExecutorService exec_ = Executors.newFixedThreadPool(4);

	BufferedReader is = null;
	BufferedWriter os = null;

	public EchoServer(int port) {
		port_ = port;
	}

	public boolean isStarted() {
		return !exec_.isShutdown();
	}

	public void run() {
		while (!stopRequest_) {
			try {
				Socket clientSocket = serverSocket_.accept();
				EchoHandler task = new EchoHandler(clientSocket);
				exec_.execute(task);
			} catch (Exception e) {
				logger_.error("Cannot accept client connection.");
			}
		}
	}
	
	public void start() throws Exception {
		serverSocket_ = new ServerSocket(port_);
		mainThread_.start();

	}

	public void stop() throws Exception {
		stopRequest_ = true;
		SocketUtils.close(this.serverSocket_);
		mainThread_.interrupt();
		mainThread_.join();
		exec_.shutdown();

	}

	public static void main(String[] args) throws Exception {
		int nPort = 2008;
		if(args.length > 0 ) {
			nPort = NumberUtils.toInt(args[0]);
		}
		EchoServer svr = new EchoServer(nPort);
		svr.start();
		java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
		System.out.print("stop?");
		String as = in.readLine();
		System.out.println("stop..." + as);
		svr.stop();
	}
}

