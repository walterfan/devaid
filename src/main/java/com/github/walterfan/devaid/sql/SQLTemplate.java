package com.github.walterfan.devaid.sql;


import java.io.FileInputStream;
import java.io.InputStream;
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

import com.github.walterfan.util.FileUtil;


public class SQLTemplate {
    
    private static Log logger = LogFactory.getLog(SQLTemplate.class);
    
   
    private Map<String, SQLCommands> sqlsMap = new HashMap<String, SQLCommands>();
    
    public void clear() {
        sqlsMap.clear();
    }

    public void loadFromXml(String cfgFile) throws Exception {

        InputStream is = FileUtil.readFromClassPath(cfgFile);

        if (is == null) {
            logger.error("cannot find " + cfgFile);
            throw new DocumentException("cannot find " + cfgFile);
        }
        loadFromStream(is);

    }

    public void loadFromResource(String cfgFile) throws Exception {
        //InputStream is  = new FileInputStream(cfgFile);
    	InputStream is  = FileUtil.readFromClassPath(cfgFile);
        if (is == null) {
            logger.error("cannot find " + cfgFile);
            throw new DocumentException("cannot find " + cfgFile);
        }
        loadFromStream(is);
    }  
    
    
    
    public void loadFromStream(InputStream is) throws Exception {
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(is);
            Element root = document.getRootElement();
            Iterator<Element> it = root.elementIterator();

            while (it.hasNext()) {
                Element cmdNode = it.next();

                String cmdType = cmdNode.attributeValue("type");
                SQLCommands cmds = sqlsMap.get(cmdType);
                if (cmds == null) {
                    cmds = new SQLCommands();
                    cmds.setType(cmdType);
                }

                Iterator it1 = cmdNode.elementIterator();

                while (it1.hasNext()) {
                    Element childNode = (Element) it1.next();
                    // String cmdName = childNode.attributeValue("name");
                    SQLCommand cmd = new SQLCommand();
                    cmd.setName(childNode.attributeValue("name"));
                    cmd.setSql(StringUtils.trim(childNode.getText()));
                    cmds.addSQLCommand(cmd);

                }
                sqlsMap.put(cmds.getType(), cmds);
            }
        } finally {
            IOUtils.closeQuietly(is);
        }

    }

    public Map<String, SQLCommands> getSqlMap() {
        return this.sqlsMap;
    }
    
    public SQLCommand getSQLCommand(String type, String name) {
        SQLCommands cmds =  this.sqlsMap.get(type);
        if(cmds!=null) {
            return cmds.getSQLCommand(name);
        }
        return null;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder("SQLTemplate:\n");
        for (Map.Entry<String, SQLCommands> entry : sqlsMap.entrySet()) {
            sb.append(entry.getKey() + ":\n" + entry.getValue() + "\n");
        }
        return sb.toString();
    }
    
    public static void main(String[] args) throws Exception {
        
        SQLTemplate template = new SQLTemplate();
        template.loadFromXml("./etc/SQLTemplate.xml");
        System.out.println(template);
    }

}
