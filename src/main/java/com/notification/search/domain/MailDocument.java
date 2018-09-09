package com.notification.search.domain;

import com.notification.shared.MailSentEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailDocument {
    private String to;
    private String from;
    private String subject;
    private List<String> cc;
    private List<String> bcc;
    private String rawTextContent;
    private String htmlContent;
    private LocalDate date;

    //todo add links to attachments

    public static MailDocument from(MailSentEvent event) {
        MailDocument document = new MailDocument();
        document.to = event.getTo();
        document.from = event.getFrom();
        document.subject = event.getSubject();
        document.cc = event.getCc();
        document.bcc = event.getBcc();
        document.htmlContent = event.getHtmlContent();
        document.rawTextContent = Jsoup.parse(event.getHtmlContent()).text();
        document.date = LocalDate.now();

        return document;
    }

}
