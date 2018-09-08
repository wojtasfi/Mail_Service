package com.notification.search.domain;

import com.notification.sender.SendMailRequestBuilder;
import com.notification.sender.domain.dto.MailDto;
import com.notification.shared.MailSentEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class MailDocumentTest {

    private final String template = "test_mail_content";

    @Test
    public void shouldCreateCorrectMailDocumentFromMailDto() {
        String to = "test@test.pl";
        String from = "testFrom@test.pl";
        String subject = "subject";
        String html = "<div><p>html</p></div>";
        String text = "html";

        MailDto mailDto = new SendMailRequestBuilder()
                .setTo(to)
                .setFrom(from)
                .setSubject(subject)
                .setTemplateType(template)
                .setTemplateParams(new HashMap<>())
                .setLocale(Locale.US)
                .build().toDto();
        MailSentEvent event = new MailSentEvent(this, mailDto, html);

        MailDocument document = MailDocument.from(event);
        assertEquals(document.getTo(), to);
        assertEquals(document.getFrom(), from);
        assertEquals(document.getSubject(), subject);
        assertEquals(document.getHtmlContent(), html);
        assertEquals(document.getRawTextContent(), text);
    }
}
