package com.github.walterfan.service;

import javax.management.Attribute;
import javax.management.AttributeList;

/**
 * @author walter
 *
 */
public interface IServerConfig {
	
	Attribute getAttribute(String name);
	
	void setAttribute(Attribute attribute);
	
	AttributeList getAttributes(String[] attributes); 
	
	AttributeList setAttributes(AttributeList attributes) ;

}
