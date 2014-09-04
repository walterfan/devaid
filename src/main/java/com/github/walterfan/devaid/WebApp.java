package com.github.walterfan.devaid;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.ProtectionDomain;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.webapp.WebAppContext;

import com.github.walterfan.devaid.http.WebCmdHandler;
import com.github.walterfan.devaid.http.WebHandler;

public class WebApp extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Log.getLogger(WebApp.class);
	private static final String JSP_WIKI_WAR = "/workspace/java/JSPWiki.war";
	private static final String JSP_WIKI_DIR = "/workspace/exam/JSPWiki";
	private static final String JSP_WIKI_PATH = "/wiki";
	private static final String JSP_WIKI_TMP = "/workspace/tmp";
	private static final String HOME_PAGE = "index.html";
	private static final String HOME_FOLDER = "/workspace/cpp/cwhat/site";
	
	private Server _server;
	private WebHandler webHandler;


	
	public WebHandler getWebHandler() {
		return webHandler;
	}



	public void setWebHandler(WebHandler webHandler) {
		this.webHandler = webHandler;
	}



	public void start(int nPort) throws Exception {
		_server = new Server(nPort);
		
		HandlerList handlers = new HandlerList();

		
		handlers.addHandler(createDynmicWebApp(JSP_WIKI_DIR, JSP_WIKI_TMP));
		handlers.addHandler(createStaticWebApp(HOME_FOLDER, HOME_PAGE));
		handlers.addHandler(createServletApp("/api"));
		handlers.addHandler(new DefaultHandler());
		_server.setHandler(handlers);
		_server.start();
		_server.join();
	}

	public Handler createStaticWebApp(String homeFolder, String homePage) throws Exception {

		ResourceHandler resource_handler = new ResourceHandler();

		resource_handler.setDirectoriesListed(true);
		resource_handler.setWelcomeFiles(new String[] { homePage });
		resource_handler.setResourceBase(homeFolder);

		return resource_handler;

	}

	public Handler createServletApp(String path) throws Exception {

		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(WebApp.class, path);

		return handler;
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPut(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	@Override
	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	@Override
	protected void doHead(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	@Override
	protected void doTrace(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	@Override
	protected void doOptions(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		logger.info("waltertestL " + webHandler);
		if(null != webHandler) {
			this.webHandler.handle(request, response);
			return;
		} else {
			this.webHandler = new WebCmdHandler();
			this.webHandler.handle(request, response);
			return;
		}
		
		/*
		
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println("<h1>Command Executor</h1>" + request.getServletPath());
		*/
	}
	
	public Handler createDynmicWebApp(String warPath, String tmpPath) throws Exception {
		
		ProtectionDomain domain = WebApp.class.getProtectionDomain();
		URL location = domain.getCodeSource().getLocation();

		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath(JSP_WIKI_PATH);
		webapp.setResourceBase(JSP_WIKI_DIR);
		webapp.setDescriptor(location.toExternalForm() + "/WEB-INF/web.xml");
		webapp.setServer(_server);
		webapp.setParentLoaderPriority(true);
		//webapp.setWar(location.toExternalForm());;

		// (Optional) Set the directory the war will extract to.
		// If not set, java.io.tmpdir will be used, which can cause problems
		// if the temp directory gets cleaned periodically.
		// Your build scripts should remove this directory between deployments
		webapp.setTempDirectory(new File(tmpPath));
		return webapp;
		
	}
	
	public static void main(String[] args) throws Exception {

		int nPort = args.length == 0 ? 1975 : Integer.parseInt(args[0]);
		WebApp webApp = new WebApp();
		WebHandler webHandler = new WebCmdHandler();
		webApp.setWebHandler(webHandler);
		
		webApp.start(nPort);

		
		
	}

}