package com.github.walterfan.devaid.sql;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;



import com.github.walterfan.util.FileUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author walter
 *
 */
public class JdbcConfigFactory {
	private static String configFilename = "JDBCConfig.xml";
	
	private List<JdbcConfig> configList 
		= new ArrayList<JdbcConfig>(5);
	
	public void add(JdbcConfig jc) {
		configList.add(jc);
	}
	
	public JdbcConfig get(String name) {
		for(JdbcConfig cfg: configList) {
			if(cfg.getName().equals(name)) {
				return cfg;
			}
		}
		return null;
	}

	public static String getConfigFilename() {
		return configFilename;
	}

	public static void setConfigFilename(String configFilename) {
		JdbcConfigFactory.configFilename = configFilename;
	}

	public List<JdbcConfig> getConfigList() {
		return configList;
	}

	public void setConfigList(List<JdbcConfig> configList) {
		this.configList = configList;
	}

	public void serialize(OutputStream os) throws IOException {
		XStream xstream = createXStream();
		xstream.toXML(this, os);
	}
	
	public void serialize() throws IOException {
		XStream xstream = createXStream();
	       
        OutputStream fos = null;
        try {
        	fos = new FileOutputStream(configFilename);
        	xstream.toXML(this, fos);
        } finally {
        	IOUtils.closeQuietly(fos);
        }
	}
	
	public static JdbcConfigFactory deserialize(String xml) throws IOException {
		XStream xstream = createXStream();
		InputStream fis = null;
		try {
			fis = JdbcConfigFactory.class.getClassLoader().getResourceAsStream(configFilename);
			return (JdbcConfigFactory)xstream.fromXML(fis);
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}
	
	public static JdbcConfigFactory deserialize() throws IOException {
	        if(FileUtil.isFileExist(configFilename))
	            return deserialize(configFilename);
	        else 
	            return null;
	}
	
	private static XStream createXStream() {
		XStream xstream = new XStream(new DomDriver());
		xstream.useAttributeFor(JdbcConfig.class, "name");
		xstream.useAttributeFor(JdbcConfig.class, "type");

	    xstream.addImplicitCollection(JdbcConfigFactory.class, "configList");
		xstream.alias("JdbcConfigs", JdbcConfigFactory.class);
        xstream.alias("JdbcConfig", JdbcConfig.class);
        return xstream;
	}
	
	public static JdbcConfigFactory createJdbcConfigFactory() {
		JdbcConfigFactory factory =  new JdbcConfigFactory();		
        return factory;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		for(JdbcConfig cfg: configList) {
			sb.append(cfg + "\n");
		}
		return sb.toString();
	}
	
	public static void main(String[] args) throws IOException {
		JdbcConfigFactory factory =  JdbcConfigFactory.createJdbcConfigFactory();
		System.out.println(factory);
		factory.serialize();
	}

}