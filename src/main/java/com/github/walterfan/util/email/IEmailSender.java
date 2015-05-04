package com.github.walterfan.util.email;

import javax.mail.MessagingException;

public interface IEmailSender {
    void send(Email email) throws MessagingException; 
}
