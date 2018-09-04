package com.notification.sender.service;

import com.notification.sender.domain.dto.MailDto;

import javax.mail.MessagingException;

public interface MailService {

    void sendMail(MailDto mailDto) throws MessagingException;
}
