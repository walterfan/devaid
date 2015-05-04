package com.github.walterfan.util.email;

import java.util.Properties;

/**
 * 
 * @author walter
 *
 */
public class EmailConfig {
    /**
     * email server 
     */
    private String host;

    /**
     * default target email address 
     */
    private String to;
    /**
     * default from email address 
     */
    private String from;

    /**
     * send email partial?
     */
    private boolean smtpSendpartial = true;

    /**
     * send email timeout, unit is millsecond
     */
    private int smtpTimeout = 30000;
    
    /**
     * smtp used port
     */
    private int smtpPort = 25;

    /**
     * need smtp auth?
     */
    private boolean smtpAuth = false;

    /**
     * smtp host
     */
    private String smtpHost;

    /**
     * smtp user
     */
    private String smtpUser;

    /**
     * smtp password
     */
    private String smtpPassword;

    /**
     * site VIP
     */
    private String mailSiteVIP;
    
    /**
     * need debug?
     */
    private boolean debug = false;

    Properties prop = new Properties();
    

    public EmailConfig() {
        
    }

    
/*    public Properties loadProp(String filename) throws IOException {

        InputStream is = null;
        try {
            is = new FileInputStream(filename);
            prop.load(is);
            setHost(prop.getProperty("mail.host"));
            setFrom(prop.getProperty("mail.from"));
            setSmtpSendpartial("true".equalsIgnoreCase(prop.getProperty("mail.smtp.sendpartial")));
            setSmtpPort(NumberUtils.toInt(prop.getProperty("mail.smtp.port")));
            setSmtpAuth("true".equalsIgnoreCase(prop.getProperty("mail.smtp.auth")));
            setSmtpHost(prop.getProperty("mail.smtp.host"));
            setSmtpUser(prop.getProperty("mail.smtp.user"));
            setSmtpPassword(prop.getProperty("mail.smtp.password"));
            setDebug("true".equalsIgnoreCase(prop.getProperty("mail.debug")));

            return prop;
        } finally {
            IOUtils.closeQuietly(is);
        }
    }*/

    public synchronized Properties getProp() {
        
        if(prop.isEmpty()) {
            putValue(prop,"mail.transport.protocol", "smtp");
            putValue(prop,"mail.host", this.host);
            
            if (this.smtpAuth) {
                putValue(prop,"mail.smtp.port", this.smtpPort);
                putValue(prop,"mail.smtp.auth", this.smtpAuth);
                putValue(prop,"mail.smtp.host", this.smtpHost);
                putValue(prop,"mail.smtp.user", this.smtpUser);
                putValue(prop,"mail.smtp.password", this.smtpPassword);
                putValue(prop,"mail.smtp.timeout", this.smtpTimeout);
            }
            putValue(prop,"mail.smtp.sendpartial", this.smtpSendpartial);
            putValue(prop,"mail.debug", this.debug);
        }
        return this.prop;
    }

    private void putValue(Properties aProp, Object aKey, Object aValue) {
        if(aValue != null) {
            aProp.put(aKey, aValue);
        }
    }
       
    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }


    
    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }


    
    /**
     * @return the from
     */
    public String getFrom() {
        return from;
    }


    
    /**
     * @param from the from to set
     */
    public void setFrom(String from) {
        this.from = from;
    }


    
    /**
     * @return the smtpSendpartial
     */
    public boolean getSmtpSendpartial() {
        return smtpSendpartial;
    }


    
    /**
     * @param smtpSendpartial the smtpSendpartial to set
     */
    public void setSmtpSendpartial(boolean smtpSendpartial) {
        this.smtpSendpartial = smtpSendpartial;
    }


    
    /**
     * @return the smtpPort
     */
    public int getSmtpPort() {
        return smtpPort;
    }


    
    /**
     * @param smtpPort the smtpPort to set
     */
    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }


    
    /**
     * @return the smtpAuth
     */
    public boolean getSmtpAuth() {
        return smtpAuth;
    }


    
    /**
     * @param smtpAuth the smtpAuth to set
     */
    public void setSmtpAuth(boolean smtpAuth) {
        this.smtpAuth = smtpAuth;
    }


    
    /**
     * @return the smtpHost
     */
    public String getSmtpHost() {
        return smtpHost;
    }


    
    /**
     * @param smtpHost the smtpHost to set
     */
    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }


    
    /**
     * @return the smtpUser
     */
    public String getSmtpUser() {
        return smtpUser;
    }


    
    /**
     * @param smtpUser the smtpUser to set
     */
    public void setSmtpUser(String smtpUser) {
        this.smtpUser = smtpUser;
    }


    
    /**
     * @return the smtpPassword
     */
    public String getSmtpPassword() {
        return smtpPassword;
    }


    
    /**
     * @param smtpPassword the smtpPassword to set
     */
    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }


    
    /**
     * @return the mailSiteVIP
     */
    public String getMailSiteVIP() {
        return mailSiteVIP;
    }


    
    /**
     * @param mailSiteVIP the mailSiteVIP to set
     */
    public void setMailSiteVIP(String mailSiteVIP) {
        this.mailSiteVIP = mailSiteVIP;
    }


    
    /**
     * @return the mailDebug
     */
    public boolean getDebug() {
        return debug;
    }


    
    /**
     * @param mailDebug the mailDebug to set
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

   
    /**
     * @param prop the prop to set
     */
    public void setProp(Properties prop) {
        this.prop = prop;
    }


    
    /**
     * @return the smtpTimeout
     */
    public int getSmtpTimeout() {
        return smtpTimeout;
    }


    
    /**
     * @param smtpTimeout the smtpTimeout to set
     */
    public void setSmtpTimeout(int smtpTimeout) {
        this.smtpTimeout = smtpTimeout;
    }


    
    /**
     * @return the to
     */
    public String getTo() {
        return to;
    }


    
    /**
     * @param to the to to set
     */
    public void setTo(String to) {
        this.to = to;
    }
    
    
}