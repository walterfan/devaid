package com.github.walterfan.util.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class UDPSocket {
	private byte[] buffer_ = new byte[1024];
	private DatagramSocket receiveSocket_;
	private DatagramSocket sendSocket_;
	
	private String localHost_;
	private String remoteHost_;
	private int sendPort_;
	private int receivePort_;
	
	public UDPSocket() {
		
	}
	
	public UDPSocket(String localHost, String remoteHost, int sentPort, int receivePort) throws SocketException {
		this.localHost_ = localHost;
		this.remoteHost_ = remoteHost;
		this.sendPort_ = sentPort;
		this.receivePort_ = receivePort;
		
		this.receiveSocket_ = new DatagramSocket(new InetSocketAddress(localHost_, receivePort_));
		this.sendSocket_    = new DatagramSocket(new InetSocketAddress(remoteHost_, sendPort_));
	}
	
	public void send(byte[] data) throws IOException {
		DatagramPacket packet = new DatagramPacket(buffer_, buffer_.length, InetAddress.getByName(remoteHost_), sendPort_);
		packet.setData(data);
		
		sendSocket_.send(packet);
	}
	
	public void receive(byte[] data) throws IOException {
		DatagramPacket packet = new DatagramPacket(buffer_, buffer_.length);
		receiveSocket_.receive(packet);
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		os.write(packet.getData(), 0, packet.getLength());
		data = os.toByteArray();
		os.flush();
		os.close();
	}
	
	public void close() {
		SocketUtils.disconnect(receiveSocket_);
		SocketUtils.close(receiveSocket_);
		
		SocketUtils.disconnect(sendSocket_);
		SocketUtils.close(sendSocket_);
		
	}
}
