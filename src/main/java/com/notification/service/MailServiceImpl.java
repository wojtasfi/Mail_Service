package com.notification.service;

import com.notification.domain.dto.MailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Locale;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public void sendMail(MailDto mailDto) throws MessagingException {
        Locale locale = LocaleContextHolder.getLocale();
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setFrom("sender@example.com");
        message.setTo("recipient@example.com");
        message.setSubject("This is the message subject");
        message.setText("This is the message body");

        final Context ctx = new Context(locale);
        ctx.setVariables(mailDto.getTemplateParams());

        final String htmlContent = this.templateEngine.process("html/email-inlineimage.html", ctx);

        this.mailSender.send(mimeMessage);
    }
}
