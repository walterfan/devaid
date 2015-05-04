package com.github.walterfan.util.email;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
public class EmailFactory {
    public static final String JOBALERT_EMAIL_NAME = "jobAlert";

    public static final String DOWNLOAD_FAILED_NAME = "downloadTraceFileFailed";
    
    public static final String RESTART_ALERT_EMAIL_NAME = "restartServerFailed";
    
    public static final String SERVER_DISCONNECTED_EMAIL_NAME = "serverDisconnected";
    
    private static Log logger = LogFactory.getLog(EmailFactory.class);
    
    private static String emailXmlFile = "WEB-INF/email-template.xml";
    
    private static EmailFactory myInstance = new EmailFactory();

    private Map<String, Email> emailTplMap = new HashMap<String, Email>();


    private EmailFactory() {
        try {
            initiate();
        } catch (Exception e) {
            logger.error("initiate error ", e);
        }
    }

    public void initiate() throws Exception {        
        initiate(emailXmlFile);
    }
    
    @SuppressWarnings("unchecked")
    public void initiate(String xmlFile) throws Exception {
        this.emailTplMap.clear();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(xmlFile);
        if (is == null) {
            logger.error("cannot find " + emailXmlFile);
            throw new DocumentException("cannot find " + emailXmlFile);
        }
        
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(is);
        Element root = document.getRootElement();
        Iterator<Element> it = root.elementIterator();
        while (it.hasNext()) {
            Element cmdNode = it.next();
            //System.out.println("===" + cmdNode.attributeValue("name")+"===");
            String emailName = cmdNode.attributeValue("name");
            Iterator<Element> it1 = cmdNode.elementIterator();
            Email email = new Email();
            while (it1.hasNext()) {
                Element childNode = (Element) it1.next();
                
                if ("subject".equals(childNode.getName())) {
                    email.setSubject(childNode.getText());
                } else if("content".equals(childNode.getName())) {
                    email.setContent(childNode.getText());
                }
            }
            emailTplMap.put(emailName, email);
        }
        
        //System.out.println(emailTplMap);
    }

    public static EmailFactory getInstance() {
        return myInstance;
    }
  
    public void putEmail(String mailName, Email email) {
        this.emailTplMap.put(mailName, email);
    }
    
    public Email getEmail(String mailName) {
        Email oldEmail = emailTplMap.get(mailName);
        if(oldEmail == null) {
            return null;
        }
        return (Email)oldEmail.clone();
    }
    
 }
