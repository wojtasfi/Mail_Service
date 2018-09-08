package com.notification.sender.service;

import com.notification.sender.api.FilePath;
import com.notification.sender.api.FileString;
import com.notification.sender.domain.dto.MailDto;
import com.notification.sender.util.FileUtilities;
import com.notification.shared.MailSentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final TemplateService templateService;
    private final FileUtilities fileUtilities;
    private final ApplicationEventPublisher publisher;

    @Override
    @Transactional
    public void sendMail(MailDto mailDto) throws MessagingException {
        MimeMessageHelper message = createPresetMessage(mailDto);

        String htmlContent = templateService.resolveEmailText(mailDto);
        message.setText(htmlContent, true);
        Map<String, File> filesMap = addAttachmentsIfApplicable(message, mailDto);

        mailSender.send(message.getMimeMessage());

        publisher.publishEvent(new MailSentEvent(this, mailDto, htmlContent));
        cleanUpTheFiles(filesMap);
    }

    private void cleanUpTheFiles(Map<String, File> filesMap) {
        filesMap.forEach((fileName, file) -> {
            try {
                fileUtilities.deleteFile(file);
            } catch (IOException e) {
                //todo put this task to kafka with retry number and try to delete again
                //todo OR do not delete just generate links to be possible to preview attachments later
                log.error("Could not delete file {}:", fileName, e);
            }
        });
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
