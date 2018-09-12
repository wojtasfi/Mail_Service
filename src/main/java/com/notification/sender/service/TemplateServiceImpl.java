package com.notification.sender.service;

import com.notification.sender.domain.dto.MailDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {
    private final TemplateEngine templateEngine;

    private final String htmlExtension = ".html";

    @Value("${default.template.locale}")
    private String defaultLocale;

    @Override
    public String resolveEmailText(MailDto mailDto) {
        Context ctx = createContextWithParams(mailDto);
        String templateName = resolveTemplateFullName(mailDto);
        //todo load template from file or from DB to make it possible to add template with UI
        return templateEngine.process(templateName, ctx);
    }

    @Override
    public String resolveTemplateFullName(MailDto mailDto) {
        String localeString = resolveLocale(mailDto);
        return mailDto.getTemplateType() + "_" + localeString + htmlExtension;
    }

    private Context createContextWithParams(MailDto mailDto) {
        Context ctx = new Context();
        ctx.setVariables(mailDto.getTemplateParams());
        return ctx;
    }


    private String resolveLocale(MailDto mailDto) {
        if (mailDto.getLocale() == null) {
            return defaultLocale;
        }
        return mailDto.getLocale().toString();
    }
}
