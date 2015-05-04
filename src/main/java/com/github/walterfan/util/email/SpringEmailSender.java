package com.github.walterfan.util.email;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

public class SpringEmailSender {

    private static final Log log = LogFactory.getLog(SpringEmailSender.class);
    private ExecutorService executor = Executors.newFixedThreadPool(5);
    private JavaMailSender mailSender;
    private MessageSource messageSource;

    public void sendEmail(final String subject, final String recipient,
            final String content, final String from) {

        try {
            MimeMessagePreparator preparator = new MimeMessagePreparator() {
                public void prepare(MimeMessage mimeMessage) throws Exception {
                    MimeMessageHelper message = new MimeMessageHelper(
                            mimeMessage, true, "UTF-8");
                    message.setFrom(from);
                    message.setTo(recipient);
                    message.setSubject(subject);
                    message.setText(content, true);
                }
            };

            executor.submit(new SendMail(preparator));
        } catch (Exception e) {
            log.error(e);
        }
    }

    class SendMail implements Runnable {
        MimeMessagePreparator preparator;

        SendMail(MimeMessagePreparator preparator) {
            this.preparator = preparator;
        }

        public void run() {
            log.debug("About to send email:");
            mailSender.send(preparator);
            log.debug("Email send.");
        }
    }

    public void sendEmail(final String subject, final String recipient,
            final String emailTemplate, final boolean flag, final String from) {
        if (flag) {
            sendEmail(subject, recipient, emailTemplate, from);
        }
    }

    public void sendEmail(final String subject, final String recipient,
            final String emailTemplate, final boolean flag) {
        if (flag) {
            sendEmail(subject, recipient, emailTemplate, "support@domain.com");
        }
    }

    public void sendEmail(final String subject, final String recipient,
            final String emailTemplate) {
        sendEmail(subject, recipient, emailTemplate, "support@domain.com");
    }

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

}
