package com.github.walterfan.devaid.tool;

import com.github.walterfan.util.ConfigLoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by walter on 8/20/15.
 */
public class XmlScriptParser {

    private String srcPath;
    private String destPath;

    public XmlScriptParser(String srcPath, String destPath) {
        this.srcPath = srcPath;
        this.destPath = destPath;
    }

    public void readScripts() {
        File startDir = new File(srcPath);
        File[] filesAndDirs = startDir.listFiles();
        if(null == filesAndDirs) {
            return;
        }

        for (File file : filesAndDirs) {
            if(file.isDirectory()) {
                continue;
            }

            readScript(file.getAbsolutePath());
        }

    }

    public void readScript(String xml) {
        System.out.println("! outline for " + xml);

        String xmlRet = readXMLFile(xml, "function", 0);
        xmlRet = readXMLFile(xml, "events", 1);

        System.out.println(xmlRet);
    }

    @SuppressWarnings("rawtypes")
	public String readXMLFile(String scriptFileName, String elemType, int lev) {


        StringBuilder xmlSb = new StringBuilder();

        try {
            String text = FileUtils.readFileToString(new File(scriptFileName));
            int pos = text.indexOf("<");
            if(pos == -1) {
                return "";
            } else {
                text = text.substring(pos);
            }
            Document document = DocumentHelper.parseText(text);
            // String xml = document.asXML();
            Element root = document.getRootElement();
            Iterator iter = root.elementIterator(elemType);

            while (iter.hasNext()) {
                Element child = (Element) iter.next();
                xmlSb.append("* " + child.getName());
                xmlSb.append(" "  + attributes2Str(child));
                //&& "UpdateIVRCallLeg".equals(child.attributeValue("name"))
                if("function".equals(child.getName())
                        && "function".equals(elemType)) {
                    String xmlFileName = destPath + "/" + FilenameUtils.getBaseName(scriptFileName)
                            + "_function_" + child.attributeValue("name") + ".xml";
                    File xmlFile = new File(xmlFileName);
                    FileUtils.writeStringToFile(xmlFile, child.asXML());
                    System.out.println("write " + xmlFileName);
                }
                int depth = lev;
                xmlSb.append("\n");
                if(--depth >= 0) {
                    Iterator iterator = child.elementIterator();
                    while (iterator.hasNext()) {
                        Element aElement = (Element) iterator.next();
                        xmlSb.append("** " + aElement.getName());
                        xmlSb.append(" ");
                        xmlSb.append(attributes2Str(aElement));
                        if("events".equals(elemType)) {
                            //&& aElement.attributeValue("event").startsWith("sip.")
                            //&& !aElement.attributeValue("event").equals("sip.*")) {
                            String xmlFileName = destPath + "/" + FilenameUtils.getBaseName(scriptFileName)
                                    + "_event_" + aElement.attributeValue("event") + ".xml";
                            System.out.println("write " + xmlFileName);
                            xmlFileName = xmlFileName.replace("*", "other");
                            File xmlFile = new File(xmlFileName);
                            FileUtils.writeStringToFile(xmlFile, aElement.asXML());
                        }

                        xmlSb.append("\n");
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return xmlSb.toString();
    }
    @SuppressWarnings("unchecked")
	private String attributes2Str(Element aElement) {
        StringBuilder xmlSb = new StringBuilder();
        Iterator<Attribute> itAtt = aElement.attributeIterator();
        while(itAtt.hasNext()) {
            Attribute att = itAtt.next();
            xmlSb.append(att.getName());
            xmlSb.append("=");
            xmlSb.append(att.getText());

        }
        return xmlSb.toString();
    }


    public void walkthrough() {
        //ReadXMLFile(xml, elemType, lev);
    }
    /**
     * @param args
     * @throws IOException
     */
    @SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {

        String srcPath = "";
        String destPath = "";

        boolean bFlag = true;
        if(args.length == 2) {
            srcPath = args[0];
            destPath = args[1];
            bFlag = false;
        } else {
            ConfigLoader cfgLoader = ConfigLoader.getInstance();
            cfgLoader.loadFromClassPath("devaid.properties");
            srcPath = cfgLoader.getProperty("XML_SCRIPT_SRC_PATH");
            destPath = cfgLoader.getProperty("XML_SCRIPT_DEST_PATH");
            System.out.println("Convert XML from " + srcPath + " to " + destPath);
        }

        XmlScriptParser helper = new XmlScriptParser(srcPath, destPath);
        helper.readScripts();




    }

}
