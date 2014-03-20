package com.github.walterfan.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/**
 * @author walter
 * 
 */
public class XmlUtils {
	public static final String XML_HEADER = "<root  ver=\"1.0\"><response>"
			+ "<result>SUCCESS</result>" + "</response>" + "<return>";

	public static final String XML_FOOTER = "</return></root>";

	public static final String XML_ERROR = "<root ver=\"1.0\"><response>"
			+ "<result>FAILURE</result>" + "<reason>%s</reason>"
			+ "</response>" + "</root>";

	public static void appendXmlTag(StringBuilder sb, String tagName,
			Object value) {
		sb.append("<" + tagName + ">");
		sb.append(value);
		sb.append("</" + tagName + ">");
	}


	public static void traversal(File file) throws Exception {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(is);
			treeWalk(document);
		} finally {
			IOUtils.closeQuietly(is);
		}

	}

	public static void traversal(String xmlText) throws Exception {
		InputStream is = null;
		try {			
			Document document = DocumentHelper.parseText(xmlText);
			treeWalk(document);
		} finally {
			IOUtils.closeQuietly(is);
		}

	}
	
	public static void treeWalk(Document document) {
		treeWalk(document.getRootElement());
	}

	public static void treeWalk(Element element) {
		for (int i = 0, size = element.nodeCount(); i < size; i++) {
			org.dom4j.Node node = element.node(i);
			if (node instanceof Element) {
				System.out.println(node.getName() + " @ " + node.getPath());
				System.out.println("\tattrubutes: "
						+ ((Element) node).attributes());

				treeWalk((Element) node);
			} else {
				// do something....
			}
		}
	}

	public static void main(String[] args) throws Exception {
		
		try {
       	 String filename = "./doc/clientparam.txt";
       	 File file = new File(filename);
       	 byte[] bytes = FileUtil.readFromFile(file);
       	 traversal(new String(bytes));
       } catch (IOException e) {
           e.printStackTrace();
       }
                
	}
	
	public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
 
        return sb.toString();
    }

	
}
