package com.github.walterfan.devaid.domain;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class Account extends BaseObject {
    private String userName;
    private String password;
    private String siteName;
    private String siteUrl;
    private String email;
    
    private transient int accountID;
    private transient int userID;
    
    private transient XStream xs = createXStream();
    
    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }
    
    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    
    
    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String toXml() {
        return xs.toXML(this);
        
    }
       
    private XStream createXStream() {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("Account", Account.class);
        xstream.useAttributeFor(Account.class, "siteName");
        return xs;
    }

    
   

    
    public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	/**
     * @return the userID
     */
    public int getUserID() {
        return userID;
    }

    
    /**
     * @param userID the userID to set
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    
    /**
     * @return the accountID
     */
    public int getAccountID() {
        return accountID;
    }

    
    /**
     * @param accountID the accountID to set
     */
    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }
    
}
