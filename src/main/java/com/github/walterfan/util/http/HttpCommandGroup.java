package com.github.walterfan.util.http;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpCommandGroup extends HashMap<String, HttpCommand> 
implements Map<String, HttpCommand>, Iterable<Map.Entry<String, HttpCommand>> {

	private static final long serialVersionUID = 1L;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		for (Map.Entry<String, HttpCommand> entry : entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue() + "\n");
		}
		return sb.toString();
	}

	public Iterator<Map.Entry<String, HttpCommand>> iterator() {
		return this.entrySet().iterator();
	}
}
