package com.notification.sender.service;

import com.notification.sender.api.FilePath;
import com.notification.sender.api.FileString;
import com.notification.sender.domain.dto.MailDto;
import com.notification.sender.util.FileUtilities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final FileUtilities fileUtilities;

    private final String htmlExtension = ".html";

    @Value("${default.template.locale}")
    private Locale defaultLocale;

    @Override
    @Transactional
    public void sendMail(MailDto mailDto) throws MessagingException {
        MimeMessageHelper message = createPresetMessage(mailDto);
        String locale = resolveLocale(mailDto);

        final Context ctx = new Context();
        ctx.setVariables(mailDto.getTemplateParams());

        final String htmlContent = this.templateEngine.process(mailDto.getTemplateType() + htmlExtension, ctx);
        message.setText(htmlContent, true);

        Map<String, File> filesMap = addAttachmentsIfApplicable(message, mailDto);

        this.mailSender.send(message.getMimeMessage());

        filesMap.forEach((fileName, file) -> {
            try {
                fileUtilities.deleteFile(file);
            } catch (IOException e) {
                log.error("Could not delete file {}:", fileName, e);
            }
        });
    }

    private String resolveLocale(MailDto mailDto) {
        if (mailDto.getLocale() == null) {
            return defaultLocale.toString();
        }
        return mailDto.getLocale().toString();
    }

    private MimeMessageHelper createPresetMessage(MailDto mailDto) throws MessagingException {

        boolean multipart = hasAttachments(mailDto);
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, multipart, "UTF-8");
        message.setFrom(mailDto.getFrom());
        message.setTo(mailDto.getTo());
        message.setSubject(mailDto.getSubject());

        return message;
    }

    private boolean hasAttachments(MailDto mailDto) {
        return !mailDto.getAttachmentsBase64Strings().isEmpty() || !mailDto.getAttachmentsPaths().isEmpty();
    }

    private Map<String, File> addAttachmentsIfApplicable(MimeMessageHelper message, MailDto mailDto) {
        Map<String, File> filesMap = new HashMap<>();

        mailDto.getAttachmentsPaths()
                .forEach(file -> addFile(filesMap, file));

        mailDto.getAttachmentsBase64Strings()
                .forEach(file -> addFile(filesMap, file));

        filesMap.forEach((fileName, file) -> {
            try {
                message.addAttachment(fileName, file);
            } catch (MessagingException e) {
                log.error("Could not add attachment {}:", fileName, e);
            }
        });
        return filesMap;
    }

    private void addFile(Map<String, File> filesMap, FilePath file) {
        try {
            filesMap.put(file.getFileName(), fileUtilities.retreiveFileFromUrl(file));
        } catch (IOException e) {
            throw new IllegalArgumentException(format("There was problem with retrieving file: %s", file.getFileName()), e);
        }
    }

    private void addFile(Map<String, File> filesMap, FileString file) {
        try {
            filesMap.put(file.getFileName(), fileUtilities.retreiveFileFromBase64String(file));
        } catch (IOException e) {
            throw new IllegalArgumentException(format("There was problem with retrieving file: %s", file.getFileName()), e);
        }
    }
}
