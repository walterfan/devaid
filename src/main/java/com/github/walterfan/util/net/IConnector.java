package com.github.walterfan.util.net;

import java.io.IOException;
import java.net.SocketAddress;

public interface IConnector {
	int connect(SocketAddress remoteAddr, SocketAddress localAddress, int timeout) throws IOException;
	
	int disconnect();
	
	
}
