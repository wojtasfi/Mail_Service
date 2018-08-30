package com.notification.service;

import com.notification.domain.dto.MailDto;

import javax.mail.MessagingException;

public interface MailService {

    void sendMail(MailDto mailDto) throws MessagingException;
}
