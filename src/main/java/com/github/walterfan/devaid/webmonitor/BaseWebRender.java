package com.github.walterfan.devaid.webmonitor;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.ListTool;
import org.apache.velocity.tools.generic.NumberTool;


/** 
* Web Render base class 
* 
* @version 1.0 2 June 2008 
* @author Walter Fan Ya Min 
*/
public class BaseWebRender implements WebRender {
    protected Log logger = LogFactory.getLog(this.getClass());
    private String tplFile = null;
    private Template template = null;
    private VelocityEngine ve = null;
    //"./conf/velocity.properties"
    private String velocityConfig = "";
    private static final String NOT_APPLICABLE = "N/A";

/**
     * Default constructor.
     */
    public BaseWebRender() {
        super();
    }

    /**
     * Creates a new BaseWebRender object.
     *
     * @param tplFile DOCUMENT ME!
     */
    public BaseWebRender(String tplFile) {
        this.tplFile = tplFile;
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public synchronized void parseTplFile() throws Exception {
        ve = new VelocityEngine();
        if(velocityConfig.isEmpty())
        	ve.init();
        else
        	ve.init(velocityConfig);
        logger.debug("loadTplFile: " + tplFile);
        template = ve.getTemplate(tplFile);
    }

    /*
     * merge monitor info with template
     * @return web response content
     */
    /**
     * DOCUMENT ME!
     *
     * @param monitorInfo DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String render(MonitorInfo monitorInfo) {
        if (monitorInfo == null) {
            logger.error("MonitorInfo is null");

            return NOT_APPLICABLE;
        }

        if (template == null) {
            try {
                parseTplFile();
            } catch (Exception e) {
                logger.error("web render init error" + e.getMessage());

                return monitorInfo.toHtmlString();
            }
        }

        try {
            VelocityContext context = new VelocityContext();

            Map<String, Object> map = monitorInfo.getMapInfo();

            if (map == null) {
                logger.error("MonitorInfo's map is empty");

                return NOT_APPLICABLE;
            } else {
            	
            	context.put("map", map);
            }

            /*Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                context.put(entry.getKey(), entry.getValue());
            }*/

            if (logger.isDebugEnabled()) {
                logger.debug("map's size=" + map.size() + "--"
                             + monitorInfo.toString());
            }

            //Add DateTool to support date format conversion
            context.put("dateTool", new DateTool());
            // Add NumberTool
            context.put("numberTool", new NumberTool());
            // Add ListTool
            context.put("listTool", new ListTool());

            //Template tpl = ve.getTemplate(tplFile);
            /* now render the template into a Writer */
            if (template != null) {
                StringWriter writer = new StringWriter();
                template.merge(context, writer);

                return writer.toString();
            } else {
                return NOT_APPLICABLE;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());

            return e.getMessage();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTplFile() {
        return this.tplFile;
    }

    /**
     * DOCUMENT ME!
     *
     * @param filename DOCUMENT ME!
     */
    public void setTplFile(String filename) {
        this.tplFile = filename;
    }

	public String getVelocityConfig() {
		return velocityConfig;
	}

	public void setVelocityConfig(String velocityConfig) {
		this.velocityConfig = velocityConfig;
	}
    
    
}
