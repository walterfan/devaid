package com.github.walterfan.util.http;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author walter
 * 
 */
public final class HttpCommandConfig {

	private static Log logger = LogFactory.getLog(HttpCommandConfig.class);
	/**
	 * http xml path
	 */
	public static String HTTP_CMD_XML = "HttpAPI.xml";

	public static String HTTP_CMD_PROP = "HttpCommands.properties";

	private Map<String, HttpCommandGroup> httpCmdsMap = new HashMap<String, HttpCommandGroup>();

	public HttpCommandConfig() {

	}
	
	public int size() {
		return this.httpCmdsMap.size();
	}

	
	public HttpCommand getHttpCommand(String grpName, String cmdName) {
		if(httpCmdsMap.containsKey(grpName)) {
			return this.httpCmdsMap.get(grpName).get(cmdName);
		}
		return null;
	}
	
	public HttpCommandGroup getHttpCommandGroup(String grpName) {
			return this.httpCmdsMap.get(grpName);
	}

	private InputStream getInputStream(String filepath) throws FileNotFoundException {
		return new FileInputStream(filepath); 
	}
	
	private InputStream getInputStream(String filepath, ClassLoader loader) throws FileNotFoundException {
		return loader.getResourceAsStream(filepath); 
	}
	
	public void clear() {
		this.httpCmdsMap.clear();
	}

	public String getMainName(String item) { // gets filename without extension
		int dot = item.lastIndexOf('.');
		return item.substring(0, dot);
	}

	
	public void load(String cfgFile) throws Exception {
        load(getInputStream(cfgFile));
    }

    public void load(String cfgFile, ClassLoader loader) throws Exception {
        load(getInputStream(cfgFile, loader));
    }

    public void load(URL url) throws Exception {
        if(url != null) {
            load(url.openStream()); 
        }
    }
    
	private void load(InputStream is) throws Exception {
        if (is == null) {
            logger.error("cannot find " + HTTP_CMD_XML);
            throw new DocumentException("cannot find " + HTTP_CMD_XML);
        }
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(is);
            Element root = document.getRootElement();
            Iterator<Element> it = root.elementIterator();

            while (it.hasNext()) {
                Element grpNode = it.next();
                
                String grpName = grpNode.attributeValue("name");
                HttpCommandGroup grp = httpCmdsMap.get(grpName);
                if (grp == null) {
                	grp = new HttpCommandGroup();
                	grp.setName(grpName);
                	httpCmdsMap.put(grpName, grp);
                }

                Iterator it0 = grpNode.elementIterator();
                while (it0.hasNext()) {
                	Element cmdNode = (Element)it0.next();
                	
                	HttpCommand cmd = new HttpCommand();
                	cmd.setName(cmdNode.attributeValue("name"));
                	cmd.setMethod(cmdNode.attributeValue("method"));
                	grp.put(cmd.getName(), cmd);
                	
                	Iterator it1 = cmdNode.elementIterator();

                    while (it1.hasNext()) {
                        Element childNode = (Element) it1.next();
                        //System.out.println("debug--childNode path:" + childNode.getPath());
                        logger.debug("node:" + childNode.getName() + ", text=" + childNode.getText());
                        if ("url".equals(childNode.getName())) {
                            cmd.setUrl(StringUtils.trim(childNode.getText()));
                        } else if ("header".equals(childNode.getName())) {
                            cmd.setHeader(StringUtils.trim(childNode.getText()));
                        } else if ("request".equals(childNode.getName())) {
                            cmd.setRequest(StringUtils.trim(childNode.getText()));
                        } else if ("response".equals(childNode.getName())) {
                            cmd.setResponse(StringUtils.trim(childNode.getText()));
                        }
                    }
                	
                }

            }
        } finally {
            IOUtils.closeQuietly(is);
        }

    }

	public String toString() {
		StringBuilder sb = new StringBuilder("");
		for (Map.Entry<String, HttpCommandGroup> entry : httpCmdsMap.entrySet()) {
			sb.append(entry.getKey() + ":\n" + entry.getValue() + "\n");
		}
		return sb.toString();
	}

	public Map<String, HttpCommandGroup> getHttpCmdsMap() {
		return httpCmdsMap;
	}

	public void setHttpCmdsMap(Map<String, HttpCommandGroup> httpCmdsMap) {
		this.httpCmdsMap = httpCmdsMap;
	}

	public static void main(String[] args) throws Exception {
		HttpCommandConfig factory = new HttpCommandConfig();
		factory.load("./etc/" + HTTP_CMD_XML);
		System.out.println(factory);
		//factory.clear();
		//System.out.println("========================");
		//factory.load("./etc/" + HTTP_CMD_PROP);
		//System.out.println(factory);
	}

}
