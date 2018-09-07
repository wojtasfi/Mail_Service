package com.notification.sender.service;

import com.notification.sender.domain.dto.MailDto;

public interface TemplateService {
    String resolveEmailText(MailDto mailDto);

    String resolveTemplateFullName(MailDto mailDto);
}
