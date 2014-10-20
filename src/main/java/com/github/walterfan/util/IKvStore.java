package com.github.walterfan.util;

import java.io.Serializable;

public interface IKvStore {
	Serializable get(String key);
	
	void set(String key, Serializable data); 
	
	void remove(String key);
	
	void connect();
	
	void close();
}
