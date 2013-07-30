/**
 *
 * Copyright 2004 Protique Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **/
package com.github.walterfan.util.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Some URI based helper methods.
 * Walter revised from package org.codehaus.activemq.uti
 * @version 1.0
 */
public final class URLHelper {

	private URLHelper() {
		
	}
    /**
     * Parsers the query string of the URI into a map of key-value pairs
     */
    public static Map<String, String> parseQuery(URI uri) {
        return parseQuery(uri.getQuery());
    }

    /**
     * Parsers the query string of the URI into a map of key-value pairs
     */
    public static Map<String, String> parseQuery(String query) {
        Map<String, String> answer = new HashMap<String, String>();
        if (query != null) {
            StringTokenizer iter = new StringTokenizer(query, "&");
            while (iter.hasMoreTokens()) {
                String pair = iter.nextToken();
                addKeyValuePair(answer, pair);
            }
        }
        return answer;
    }

    protected static void addKeyValuePair(Map<String, String> answer, String pair) {
        int idx = pair.indexOf('=');
        String name = null;
        String value = null;
        if (idx >= 0) {
            name = pair.substring(0, idx);
            if (++idx < pair.length()) {
                value = pair.substring(idx);
            }
        } else {
            name = pair;
        }
        answer.put(name, value);
    }
    
    public static String getPathAndQuery(String strUrl) {
    	try {
			URL url = new URL(strUrl);
			//System.out.println("url=" + url);
			String path = url.getPath();
			String query = url.getQuery();
			if(StringUtils.isNotBlank(query)) {
			    path = path + "?" + query;
			}
			if(path.startsWith("/")) {
				return path.substring(1);
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			
		}
		return "";
    }
    
	public static void URL2File(String strUrl) {
		URL2File(strUrl);
	}
	
	public static String URL2String(String strUrl) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			URL2Stream(strUrl, output);
		} catch (IOException e) {			
			e.printStackTrace();
			return "";
		}
		return output.toString();
	}
	
	public static void URL2Stream(String strUrl, OutputStream os) throws IOException {
		InputStream input = null;
		try {
			URL url = new URL(strUrl);	
			input = url.openStream();
			IOUtils.copy(input, os);			
		}  finally {
			IOUtils.closeQuietly(input);
		}
	}
	
	public static void URL2File(String strUrl, String filename) {
		OutputStream output = null;
		try {
			if(null == filename) {
				URL url = new URL(strUrl);
				filename = FilenameUtils.getName(url.getFile());
			}
			System.out.println("write " + filename);
			File file = new File(filename);			
			output = new FileOutputStream(file);
			URL2Stream(strUrl, output);
			
		} catch (MalformedURLException ex) {
			System.err.println(ex);
		} catch (FileNotFoundException ex) {
			System.err.println("Failed to open stream to URL: " + ex);
		} catch (IOException ex) {
			System.err.println("Error reading URL content: " + ex);
		} finally {
			IOUtils.closeQuietly(output);
		}
	}
	
    public static void main(String[] args) {
		String strUrl = "http://localhost";
		if (args.length < 1) {
			System.err.println("Usage: URLGet URL");
		} else {
			strUrl = args[0];
		}
		String str = URL2String(strUrl);
		System.out.println(str);		
	}
}
