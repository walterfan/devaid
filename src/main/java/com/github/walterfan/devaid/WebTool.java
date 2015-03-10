package com.github.walterfan.devaid;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
/*
 * Migrate the swing toolkit to web version
 * 
 * */
public class WebTool {
	public static void main(String[] args) throws IOException {

		  Server server = new Server(9091);
		  ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		  context.setContextPath("/");
		  ServletHolder apiHolder = new ServletHolder(new HttpServletDispatcher());
		  apiHolder.setInitParameter("javax.ws.rs.Application", "com.github.walterfan.devaid.WebService");
		  context.addServlet(apiHolder, "/api/*");
		  server.setHandler(context);
		  try {
		   server.start();
		   server.join();
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		 }
}
