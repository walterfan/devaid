package com.github.walterfan.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.io.IOUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


class XmlElementHandler {
	public static Map<String, String> parameterMap = new HashMap<String, String>();
	StringBuilder _sb = new StringBuilder();
	
	public void handleElement(Element nodeElement) throws DecoderException {
		/*System.out.println("[--" + node.asXML() + "---]\n");		
		System.out.println(node.getName() + " @ " + node.getPath());
		System.out.println("\tattrubutes: "
				+ ((Element) node).attributes());*/
		
		appendBeginTag(nodeElement);
		
		if(nodeElement.isTextOnly()) {
			String key = nodeElement.getName();
			String val = "";
			Attribute att = nodeElement.attribute("isBase64");
			if(att == null || att.getValue() == "0") {
				val = nodeElement.getText();				
			}
			else {
				val = new String(EncodeUtils.decodeBase64(nodeElement.getText().getBytes()));				
			}
			parameterMap.put(key, val);
			_sb.append(val);
		} else {
			XmlUtils.treeWalk(nodeElement, this);
		}
		
		appendEndTag(nodeElement);
		_sb.append("\n");
		
	}
	
	public String toString() {
		return _sb.toString();
	}
	
	public void appendBeginTag(Element nodeElement) {		
		_sb.append("<" + nodeElement.getName());
		List<Attribute> attributes = nodeElement.attributes();
		for(Attribute attribute: attributes) {
			_sb.append(" " + attribute.asXML());
		}
		_sb.append(">");
	}
	
	
	public void appendEndTag(Element nodeElement) {		
		_sb.append("</" + nodeElement.getName() + ">");
	}
}

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


	public static void traversal(File file, XmlElementHandler handler) throws Exception {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(is);
			treeWalk(document, handler);
		} finally {
			IOUtils.closeQuietly(is);
		}

	}

	public static void traversal(String xmlText,XmlElementHandler handler) throws Exception {
		InputStream is = null;
		try {			
			Document document = DocumentHelper.parseText(xmlText);
			treeWalk(document, handler);
		} finally {
			IOUtils.closeQuietly(is);
		}

	}
	
	public static void treeWalk(Document document, XmlElementHandler handler) throws DecoderException {
		Element root = document.getRootElement();
		handler.appendBeginTag(root);
		treeWalk(root, handler);
		handler.appendEndTag(root);
	}

	public static void treeWalk(Element element, XmlElementHandler handler) throws DecoderException {
		int size = element.nodeCount();
		
		if(size == 0) {
			handler.handleElement(element);
			return;
		}
		
		for (int i = 0; i < size; i++) {
			org.dom4j.Node node = element.node(i);
			if (node instanceof Element) {
				handler.handleElement((Element)node);
				//treeWalk((Element) node, handler);
			} else {
				//do nothing
				//System.err.println("---see: " + node.asXML());
			}
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
	
	 public static String decode(String input) throws Exception {
		 String xml = new String(EncodeUtils.decodeBase64(input.getBytes()));
		 XmlElementHandler handler = new XmlElementHandler();
       	 traversal(xml, handler);
       	 return handler.toString();
	 }

     public static String encode(String input) throws Exception {
		 return "Not Support now";
	 }


}
