package com.github.walterfan.devaid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.webapp.WebAppContext;

public class WebApp extends HttpServlet {
	private static final Logger LOG = Log.getLogger(WebApp.class);
	private static final String JSP_WIKI_WAR = "/Users/yafan/Documents/workspace/walter/JSPWiki.war";
	private static final String JSP_WIKI_DIR = "/Users/yafan/Documents/workspace/walter/jspwiki";
	private static final String HOME_PAGE = "index.html";
	
	private static Server _server;
	private static List<Handler> _handles = new ArrayList<Handler>(5);

	public static void main(String[] args) throws Exception {

		_server = new Server(args.length == 0 ? 8080
				: Integer.parseInt(args[0]));

		addFileServer(args);
		addServlet(args);
		// addWebService(args);

		HandlerList handlers = new HandlerList();
		for (Handler aHandler : _handles)
			handlers.addHandler(aHandler);

		_server.setHandler(handlers);
		_server.start();
		_server.join();
	}

	public static void addWebService(String[] args) {
		WebAppContext webapp = new WebAppContext(JSP_WIKI_DIR, "/wiki");
		// webapp.setContextPath("/");
		// webapp.setWar(JSP_WIKI_WAR);
		// _server.setHandler(webapp);
		_handles.add(webapp);

	}

	public static void addFileServer(String[] args) throws Exception {

		ResourceHandler resource_handler = new ResourceHandler();

		resource_handler.setDirectoriesListed(true);
		resource_handler.setWelcomeFiles(new String[] { HOME_PAGE });
		resource_handler.setResourceBase(".");

		_handles.add(resource_handler);
		_handles.add(new DefaultHandler());

	}

	public static void addServlet(String[] args) throws Exception {

		ServletHandler handler = new ServletHandler();
		// _server.setHandler(handler);
		handler.addServletWithMapping(WebApp.class, "/cmd");
		_handles.add(handler);
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println("<h1>Command Executor</h1>");
	}

}