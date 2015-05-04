package com.github.walterfan.service;

import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.walterfan.server.IServer;
import com.github.walterfan.util.JarUtils;

/**
 * @author walter
 *
 */
public class Launcher implements ILauncher {
	private static Log logger = LogFactory.getLog(Launcher.class);
	
	private static final String VERSION = "0.1";
	
	private static final String USAGE = "[options]";

	private static final String HEADER = "Options:";

	private static final String FOOTER =

	"For more instructions, see our website at: https://github.com/walterfan/devaid";

	private String jarFile ;
	
	private static String appVersion;
	
	private IServer service;
	
	private BeanFactory beanFactory;
	
	public Launcher(String jarFile) {
		this.jarFile = jarFile;
	}

	/* (non-Javadoc)
	 * @see cn.fanyamin.server.ILauncher#init()
	 */
	public synchronized void init() throws Exception {
		if(null == service) {
			beanFactory = new ClassPathXmlApplicationContext(
		        new String[] {"service-context.xml"});
		
			service= (IServer)beanFactory.getBean("appServer");
		}
	}
	
	public void printUsage(Options options) {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp(USAGE, HEADER, options, FOOTER);
	}

	public String getVersion() {
		if(StringUtils.isNotBlank(appVersion)) {
			return appVersion;
		}		    
        try {                        
            appVersion = JarUtils.getValue(jarFile, "App-Version");
        } catch (IOException e) {            
            logger.error(e); 
            appVersion = VERSION;
        }
        return appVersion;
	}
	

	
	/* (non-Javadoc)
	 * @see cn.fanyamin.server.ILauncher#executeCmd(org.apache.commons.cli.Options, org.apache.commons.cli.CommandLine)
	 */
	public void executeCmd(Options options, String[] args) throws Exception {
		if(args.length == 0) {
			defaultCmd();
			return;
		}
		CommandLineParser parser = new BasicParser();
		CommandLine commandLine = parser.parse(options, args);
		if (commandLine.hasOption('h')) {
			printUsage(options);
			return;
		}
		if (commandLine.hasOption('v')) {
			System.out.println("Version: " + this.getVersion());
			return;
		}

		init();
		if(null == service) {
			throw new RuntimeException("Service have not created");
		}
		
		if (commandLine.hasOption("start")) {
			service.start();
		} else if (commandLine.hasOption("stop")) {
			service.stop();
		} else if (commandLine.hasOption("status")) {
			System.out.println("Status: " + service.isStarted());
		} else {
			otherCmd();
		}
	}
	
	/* (non-Javadoc)
	 * @see cn.fanyamin.server.ILauncher#defaultCmd()
	 */
	public void defaultCmd() throws Exception {
		init();
		service.start();
	}
	
	/* (non-Javadoc)
	 * @see cn.fanyamin.server.ILauncher#otherCmd()
	 */
	public void otherCmd() throws Exception {
		
	}

	public static void main(String[] args) {

		Options options = new Options();
		options.addOption("h", "help", false, "print this usage information");
		//options.addOption("o", "option", true,"set option");
		options.addOption("v", "version", false,"print version");
		options.addOption("start", false, "start the application");
		options.addOption("stop", false, "stop the application");
		options.addOption("status", false, "print the status information");
		options.addOption("reconfig", false, "reload the configuration");
		
		ILauncher launcher = new Launcher("jwhat.jar");
		try {			
			launcher.executeCmd(options, args);								
		} catch (Exception e) {
			launcher.printUsage(options);
			logger.error("launch error: ", e);
			System.exit(1);
		}

	}
}
