package com.github.walterfan.util.http;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;


/**
 * class HttpResult
 * 
 * @author Walter
 * @version 1.0 12/03/2008
 */
public class HttpResult {

    private static Log logger = LogFactory.getLog(HttpResult.class);

    private static final String BLANK = "";
    
    private static class FieldHolder {
    	static final Map<String, String> nsMap= createNSMap();
    }
    //private static HashMap<String, String> nsMap = createNSMap();
    static Map<String, String> getNsMap() {
    	return FieldHolder.nsMap;
    }
    protected String xmlText = null;

    protected Document document = null;

    protected boolean bSuccess = false;


    /**
     * Constructor
     * @param text String
     */
    public HttpResult(String text) {
        this.xmlText = text;
        parse();
    }
    //it should not be private method - walter fan on 6/11
    protected void parse() {
        if (document == null) {
            try {
                document = DocumentHelper.parseText(xmlText);
            } catch (Exception e) {
                logger.error("pare: ", e);
                bSuccess = false;
                return;
            }
            bSuccess = true;
        }
    }
    
    /*public static void addNameSpace(String item, String ns) {
        nsMap.put(item, ns);
    }*/

    
    
    private static Map<String, String> createNSMap() {
    	Map<String, String> nsMap = new HashMap<String, String>();
    	
            nsMap = new HashMap<String, String>();
            nsMap.put("xsd", "http://www.w3.org/2001/XMLSchema");
            nsMap.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            nsMap.put("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        
        return nsMap;
    }

    public boolean isSuccess() {
        return this.bSuccess;
    }


    /**
     * getNodeText 
     * @param path String
     * @return String
     */
    public String getNodeText(String path) {
       
        Node node = this.getNode(path);
        
        if (node != null) {
            return node.getText();
        } else {
            return BLANK;
        }
    }
    
   
    public Node getNode(String path) {
        if(document == null) {
            return null;
        }
        return document.selectSingleNode(path);
    }
    
    public String getTextWithNS(String path) {
        try {
            XPath xpath = document.createXPath(path);
            xpath.setNamespaceURIs(getNsMap());
            Node node = xpath.selectSingleNode(document);
            if(node == null) {
                return "";
            }
            return node.getText();
        } catch(Exception e) {
            logger.error("getTest error: ",e);
            return "";
        }

    }
    /** 
     * @param element Element
     */
    public String treeWalk(Element element) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0, size = element.nodeCount(); i < size; i++) {
            Node node = element.node(i);
            if (node instanceof Element) {
                treeWalk((Element) node);
            } else {
                sb.append(node.getPath() + ": " + node.getText());
            }
        }
        return sb.toString();
    }


    public Document getDocument() {
        return this.document;
    }
    
    /**
     * 
     * @return String
     */
    public String toString() {
        return treeWalk(this.document.getRootElement()) ;
    }
}
