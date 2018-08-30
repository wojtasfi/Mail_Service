package com.notification.service;

import com.notification.domain.dto.MailDto;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    public void sendMail(MailDto mailDto) throws MessagingException {
        Locale locale = LocaleContextHolder.getLocale();
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setFrom(mailDto.getFrom());
        message.setTo(mailDto.getTo());
        message.setSubject(mailDto.getSubject());

        final Context ctx = new Context(locale);
        ctx.setVariables(mailDto.getTemplateParams());

        final String htmlContent = this.templateEngine.process("email-basic.html", ctx);
        message.setText(htmlContent, true);

        this.mailSender.send(mimeMessage);
    }
}
