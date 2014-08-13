package com.github.walterfan.util.net;

import java.net.Socket;


public interface SocketHandler extends Runnable {
	public void handle(Socket aSocket);
}
