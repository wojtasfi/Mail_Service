package com.notification.sender;

import com.google.gson.Gson;
import com.notification.sender.domain.dto.MailDto;
import com.notification.sender.service.TemplateService;
import com.notification.sender.service.TemplateServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;

import java.util.HashMap;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
public class TemplateServiceTest {

    private final String template = "test_mail";
    private final Gson gson = new Gson();
    @Mock
    private TemplateEngine templateEngine;

    @Test
    public void shouldResolveProperTemplateName() {

        TemplateService templateService = new TemplateServiceImpl(templateEngine);
        //given
        String to = "test@test.pl";
        String from = "testFrom@test.pl";
        String subject = "subject";
        Locale locale = Locale.US;

        MailDto mailDto = new SendMailRequestBuilder()
                .setTo(to)
                .setFrom(from)
                .setSubject(subject)
                .setTemplateType(template)
                .setTemplateParams(new HashMap<>())
                .setLocale(locale)
                .build().toDto();

        //when
        String templateName = templateService.resolveTemplateFullName(mailDto);
        assertEquals(template + "_" + locale.toString() + ".html", templateName);
    }

}
