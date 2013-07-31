package com.github.walterfan.util.http;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class SimpleHttpHandler implements Runnable {
	private static final String CR = "\r\n";
	private static final String CONTENT_TYPE = "Content-Type:text/html";
	private static final String HTTP_STATUS = "HTTP/1.1 200 OK";
	private final SocketChannel socketChannel;
	private GetHandler getHandler_; 
	private Charset charset = Charset.forName("utf-8");

	public SimpleHttpHandler(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public void run() {
		handle(socketChannel);
	}

	public void setGetHandler(GetHandler getter) {
		this.getHandler_ = getter;
	}

	public void handle(SocketChannel socketChannel) {
		try {
			Socket socket = socketChannel.socket();
			System.out.println("accept from: " + socket.getInetAddress()
					+ ":" + socket.getPort());

			ByteBuffer buffer = ByteBuffer.allocate(1024);
			socketChannel.read(buffer);
			buffer.flip();
			String request = decode(buffer);
			System.out.print(request);

	
			StringBuffer sb = new StringBuffer(HTTP_STATUS + CR);
			sb.append(CONTENT_TYPE + CR + CR);
			socketChannel.write(encode(sb.toString()));
			
			
			handle(socketChannel, request);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (socketChannel != null)
					socketChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void handle(SocketChannel socketChannel, String request)
			throws IOException {
		String firstLineOfRequest = request.substring(0, request
				.indexOf("\r\n"));
		String str = getHandler_.get(firstLineOfRequest);
		ByteBuffer buf = ByteBuffer.wrap( str.getBytes() );

		while(buf.hasRemaining()) {
			socketChannel.write(buf);
		}
	}
	

	public String decode(ByteBuffer buffer) {
		CharBuffer charBuffer = charset.decode(buffer);
		return charBuffer.toString();
	}

	public ByteBuffer encode(String str) { 
		return charset.encode(str);
	}
}
