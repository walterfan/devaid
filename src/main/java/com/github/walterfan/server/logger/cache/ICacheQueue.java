package com.github.walterfan.server.logger.cache;

import com.github.walterfan.server.IServer;

public interface ICacheQueue<T> extends IServer {
	T front();
	T popFront();
	boolean pushBack(T e);
	
	void start();
	void stop();

	int size();
	void setFromPath(String path);
	
	boolean isEmpty();
	boolean isFull() ;

}
