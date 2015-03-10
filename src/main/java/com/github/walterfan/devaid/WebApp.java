package com.github.walterfan.devaid;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.webapp.WebAppContext;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import com.github.walterfan.devaid.http.WebCmdHandler;
import com.github.walterfan.devaid.http.WebHandler;

public class WebApp extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Log.getLogger(WebApp.class);
	//private static final String JSP_WIKI_WAR = "/workspace/java/JSPWiki.war";
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
		
		LoginService loginService = new HashLoginService("MyRealm",
                "src/main/resources/realm.properties");
        _server.addBean(loginService);
		
		HandlerList handlers = new HandlerList();

		Handler handler1 = createDynmicWebApp(JSP_WIKI_DIR, JSP_WIKI_TMP);
		Handler handler2 = createStaticWebApp(HOME_FOLDER, HOME_PAGE);
		Handler handler3 = createServletApp("/");
		Handler handler4 = new DefaultHandler();
		
		SecurityHandler sh1 = createSecurityHandler(loginService);
		sh1.setHandler(handler1);
		
		SecurityHandler sh2 = createSecurityHandler(loginService);
		sh2.setHandler(handler2);
		
		handlers.addHandler(sh1);
		handlers.addHandler(sh2);
		handlers.addHandler(handler3);
		handlers.addHandler(handler4);
		
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
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(path);
       
        ServletHolder servHolder = new ServletHolder(new HttpServletDispatcher());
        servHolder.setInitParameter("javax.ws.rs.Application", "com.github.walterfan.devaid.WebService");
        context.addServlet(servHolder, "/api/*");
        
        //ServletHandler handler = new ServletHandler();
		//handler.addServletWithMapping(WebApp.class, "/cmd");
			
		return context;
	}

	@Override
	protected void doGet(HttpServletRequest request,
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
		} 
			
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println("<h3>The handler have not be implemented</h3>" + request.getServletPath());
		
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
	
	
	public SecurityHandler createSecurityHandler(LoginService loginService) {
		
		
        ConstraintSecurityHandler security = new ConstraintSecurityHandler();


        // This constraint requires authentication and in addition that an
        // authenticated user be a member of a given set of roles for
        // authorization purposes.
        Constraint constraint = new Constraint();
        constraint.setName("auth");
        constraint.setAuthenticate(true);
        constraint.setRoles(new String[] { "user", "admin" });

        // Binds a url pattern with the previously created constraint. The roles
        // for this constraing mapping are mined from the Constraint itself
        // although methods exist to declare and bind roles separately as well.
        ConstraintMapping mapping = new ConstraintMapping();
        mapping.setPathSpec("/*");
        mapping.setConstraint(constraint);

        security.setConstraintMappings(Collections.singletonList(mapping));
        security.setAuthenticator(new BasicAuthenticator());
        security.setLoginService(loginService);

        // chain the hello handler into the security handler
        //security.setHandler(handler);

        return security;
	}
	
	public static void main(String[] args) throws Exception {

		int nPort = args.length == 0 ? 1975 : Integer.parseInt(args[0]);
		WebApp webApp = new WebApp();
		WebHandler webHandler = new WebCmdHandler();
		webApp.setWebHandler(webHandler);
		
		webApp.start(nPort);

		
		
	}

}