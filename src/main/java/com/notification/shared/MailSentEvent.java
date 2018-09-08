package com.notification.shared;

import com.notification.sender.domain.dto.MailDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class MailSentEvent extends ApplicationEvent {
    private String to;
    private String from;
    private String subject;
    private List<String> cc;
    private List<String> bcc;
    private String htmlContent;


    public MailSentEvent(Object source, MailDto mailDto, String htmlContent) {
        super(source);
        this.to = mailDto.getTo();
        this.from = mailDto.getFrom();
        this.subject = mailDto.getSubject();
        this.cc = mailDto.getCc();
        this.bcc = mailDto.getBcc();
        this.htmlContent = htmlContent;
    }
}
