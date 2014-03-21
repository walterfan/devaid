package com.github.walterfan.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.io.IOUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;


class XmlElementHandler {
	StringBuilder _sb = new StringBuilder();
	
	public void handleElement(Element nodeElement) throws DecoderException {
		/*System.out.println("[--" + node.asXML() + "---]\n");		
		System.out.println(node.getName() + " @ " + node.getPath());
		System.out.println("\tattrubutes: "
				+ ((Element) node).attributes());*/
		_sb.append("<" + nodeElement.getName());
		List<Attribute> attributes = nodeElement.attributes();
		for(Attribute attribute: attributes) {
			_sb.append(" " + attribute.asXML());
		}
		_sb.append(">");
		if(nodeElement.isTextOnly()) {
			Attribute att = nodeElement.attribute("isBase64");
			if(att == null || att.getValue() == "0")
				_sb.append(nodeElement.getText());
			else {
				String decodeText = new String(EncodeUtils.decodeBase64(nodeElement.getText().getBytes()));
				_sb.append(decodeText);
			}
			_sb.append("</" + nodeElement.getName() + ">\n");
		}
		
		
	}
	
	public String toString() {
		return _sb.toString();
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
	
	public static void treeWalk(Document document, XmlElementHandler handler) {
		treeWalk(document.getRootElement(), handler);
	}

	public static void treeWalk(Element element, XmlElementHandler handler) {
		for (int i = 0, size = element.nodeCount(); i < size; i++) {
			org.dom4j.Node node = element.node(i);
			if (node instanceof Element) {
				try {
					handler.handleElement((Element)node);
				} catch (DecoderException e) {
					e.printStackTrace();
				}
				treeWalk((Element) node, handler);
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

	public static void main(String[] args) throws Exception {
		
		try {
       	 String filename = "S:\\Walter\\log\\clientparam_mc.txt";
       	 File file = new File(filename);
       	 byte[] bytes = FileUtil.readFromFile(file);
       	/* String xml = new String(EncodeUtils.decodeBase64(bytes));
       	 System.out.println(xml);
       	 System.out.println("------------------");
       	 XmlElementHandler handler = new XmlElementHandler();
       	 traversal(xml, handler);
       	 System.out.println(handler.toString());*/
       	System.out.println(decode(new String(bytes)));
       } catch (Exception e) {
           e.printStackTrace();
       }
                
	}	
}
