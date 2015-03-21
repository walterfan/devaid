package com.github.walterfan.devaid;

import com.github.walterfan.devaid.webapi.LinksApi;
import com.github.walterfan.devaid.webapi.TasksApi;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class WebService extends Application {
	//private static Set services = new HashSet();
	private Set<Class<?>> classesSet = new HashSet<Class<?>>();
	
	public WebService() {
		// initialize restful services
		classesSet.add(WebApiResource.class);
                classesSet.add(LinksApi.class);
                classesSet.add(TasksApi.class);
	}
	
	public Set<Class<?>> getClasses() {
		return (classesSet);
	}
}