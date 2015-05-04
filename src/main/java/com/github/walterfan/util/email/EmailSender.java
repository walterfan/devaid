/**
 * 
 */
package com.github.walterfan.util.email;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;


/**
 * 
 * @author walter
 */
public class EmailSender {
    /**
     * DOCUMENT ME!
     */
    private static Log logger = LogFactory.getLog(EmailSender.class);
   
    private EmailConfig mailCfg;


    /**
     * Default Constructor.
     */
    public EmailSender() {
    }

    public EmailSender(EmailConfig mailCfg) {
        this.mailCfg = mailCfg;
    }

    
    /**
     * @return the mailCfg
     */
    public EmailConfig getMailCfg() {
        return mailCfg;
    }

    
    /**
     * @param mailCfg the mailCfg to set
     */
    public void setMailCfg(EmailConfig mailCfg) {
        this.mailCfg = mailCfg;
    }
 

    public void send(Email email) throws MessagingException {
        if(mailCfg == null) {
            throw new RuntimeException("Have not set mail config");
        }
        //if not set from email , use system default email address
        if(email.getFromAddress() == null) {
            email.setFromAddr(mailCfg.getFrom());
        }
        //toAddr, fromAddr, subject are required always
        if (email.getToAddress() == null
                        || email.getFromAddress() == null
                        || StringUtils.isBlank(email.getSubject())) {
            throw new RuntimeException("Have not set mail Recipients/Sender/Subject/Content: " + email);
        }
        
        Transport transport = null;
        try {
            Properties prop = mailCfg.getProp();
                        
            Session session = Session.getDefaultInstance(prop, null);
            MimeMessage message = new MimeMessage(session);
            transport = session.getTransport("smtp");

            //------ Set message properties ------//
            message.setSubject(email.getSubject());
            
            if (StringUtils.isNotBlank(email.getContentType())) {
                message.setContent(email.getContent(), email.getContentType());
            } else {
                message.setText(email.getContent());
            }
                       
            message.addRecipients(Message.RecipientType.TO, email.getToAddress());
            message.setFrom(email.getFromAddress());
            
            if (email.getCcAddress() != null && email.getCcAddress().length > 0) {
                message.addRecipients(Message.RecipientType.CC, email.getCcAddress());
            }

            if (email.getBccAddress() != null && email.getBccAddress().length > 0) {
                message.addRecipients(Message.RecipientType.BCC, email.getBccAddress());
            }

            if (email.getReplyAddress() != null) {
                message.setReplyTo(email.getReplyAddress());
            }
            
            
            Date date = new Date();
            message.setSentDate(date);

            if (mailCfg.getSmtpAuth()) {
                transport.connect(mailCfg.getSmtpHost(),
                                  mailCfg.getSmtpPort(),
                                  mailCfg.getSmtpUser(),
                                  mailCfg.getSmtpPassword());
            } else {
                transport.connect();
            }
            logger.debug("begin to send " + email);
            transport.sendMessage(message, message.getAllRecipients());
            logger.debug("complete sent " + email);

      } catch (MessagingException ex) {
            logger.error("send email error, ", ex);
            throw ex;
        } finally {
            if(transport != null) {
                transport.close();
            }
        }
    
    }
    
}
