package com.github.walterfan.devaid.resteasy;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {

		ServletHolder servletHolder = new ServletHolder(
				new HttpServletDispatcher());
		servletHolder
				.setInitParameter("javax.ws.rs.Application",
						"com.github.walterfan.devaid.resteasy.AppResourceConfig");

		ServletContextHandler servletCtxHandler = new ServletContextHandler();
		servletCtxHandler.addServlet(servletHolder, "/");
		
		

		Server server = new Server(8080);
		server.setHandler(servletCtxHandler);
		server.start();
		server.join();

	}
}
