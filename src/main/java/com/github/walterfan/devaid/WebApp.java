package com.github.walterfan.devaid;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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
	private static final String JSP_WIKI_WAR = "/workspace/walter/JSPWiki.war";
	private static final String JSP_WIKI_DIR = "/workspace/walter/jspwiki";
	private static final String HOME_PAGE = "index.html";

	private static Server _server;
	private static List<Handler> _handles = new ArrayList<Handler>(5);

	public static void main(String[] args) throws Exception {

		int nPort = args.length == 0 ? 8080 : Integer.parseInt(args[0]);
		
		if( args.length > 2) {
			launchWebApp(nPort, args[1], args[2]);
			return;
		}

		_server = new Server(nPort);
		
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

	public static void launchWebApp(int port, String warPath, String tmpPath) throws Exception {
		// int port = Integer.parseInt(System.getProperty("port", "8080"));
		Server server = new Server(port);

		ProtectionDomain domain = WebApp.class.getProtectionDomain();
		URL location = domain.getCodeSource().getLocation();

		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setDescriptor(location.toExternalForm() + "/WEB-INF/web.xml");
		webapp.setServer(server);
		webapp.setWar(location.toExternalForm());

		// (Optional) Set the directory the war will extract to.
		// If not set, java.io.tmpdir will be used, which can cause problems
		// if the temp directory gets cleaned periodically.
		// Your build scripts should remove this directory between deployments
		webapp.setTempDirectory(new File(tmpPath));

		server.setHandler(webapp);
		server.start();
		server.join();
	}
}