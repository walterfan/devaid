package com.github.walterfan.util.system;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.apache.commons.io.IOUtils;


/**
 * @author walter
 *
 */
public class JarUtils {
	public static String getValue(String jarfile, String tag) throws IOException {
		
		InputStream fi = null;
        JarInputStream ji =  null;        
        try {
            fi = Thread.currentThread().getContextClassLoader().getResourceAsStream(jarfile);
            if(null != fi) {
	            ji = new JarInputStream(fi);
	            Manifest manifest = ji.getManifest();
	            Attributes as = manifest.getMainAttributes();            
	            return as.getValue(tag);
            } else {
            	throw new IOException("Cannot find " + jarfile);
            }
        }  finally {
        	IOUtils.closeQuietly(ji);
        	IOUtils.closeQuietly(fi);        	                 
        }
	}
}
