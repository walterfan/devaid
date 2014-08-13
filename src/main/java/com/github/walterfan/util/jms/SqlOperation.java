package com.github.walterfan.util.jms;

import java.io.Serializable;

/**
 * 
 * @author walter
 *
 */
public class SqlOperation implements Serializable {

	private static final long serialVersionUID = 5554391521532960454L;
	
	private String className;
	private String methodName;
	private Object[] parameters;
	
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Object[] getParameters() {
		return parameters;
	}
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
	
	
}
