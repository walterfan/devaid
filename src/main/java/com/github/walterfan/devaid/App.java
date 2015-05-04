package com.github.walterfan.devaid;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.walterfan.server.ServiceWrapper;
import com.github.walterfan.util.ConfigLoader;


/**
 * Application
 *
 */
public class App extends ServiceWrapper {
	private static Log logger = LogFactory.getLog(App.class);
	private static final String cfgItem = "devaid.configfiles";
	private static String cfgFile = "devaid.properties";
	private static String strXmls = "ServiceContext.xml";
	


	public String getName() {
		return "devaid";
	}


	@Override
	public String[] getContextFiles() {
		String[] arrXmls = strXmls.split(",");
		return arrXmls;
	}
    
   
    public static void main( String[] args )
    {
    	ConfigLoader cfgLoader = ConfigLoader.getInstance();
    	try {
			cfgLoader.loadFromClassPath(cfgFile);
			strXmls = cfgLoader.get(cfgItem);
		} catch (IOException e) {
			logger.error("load config failed" ,e);
			System.exit(1);
		}
    	
    	App theApp = new App();
    	try {
    		theApp.init();
    		theApp.start();
    	} catch(Exception e) {
    		
    		logger.error("init or start servcie failed" ,e);
    		try {
				theApp.stop();
				theApp.clean();
			} catch (Exception e1) {
				logger.error("stop or clean service failed" ,e);
			}
    		
    		
    	}
    }

}


