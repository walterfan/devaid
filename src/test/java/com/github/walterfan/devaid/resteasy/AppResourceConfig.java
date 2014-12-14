package com.github.walterfan.devaid.resteasy;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.apache.log4j.Logger;


public class AppResourceConfig extends Application {

	private Set<Class<?>> classesSet = new HashSet<Class<?>>();

	private Logger log = Logger.getLogger(getClass());

	public AppResourceConfig() {
		classesSet.add(ApplicationCommandResource.class);
	}

	public Set<Class<?>> getClasses() {
		return (classesSet);
	}

}
