package com.notification.sender;

import com.google.gson.Gson;
import com.notification.NotificationServiceApplication;
import com.notification.sender.api.SendMailRequest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NotificationServiceApplication.class)
@AutoConfigureMockMvc
@WebAppConfiguration
public class MailServiceTest {

    private final String template = "test_mail";
    private final Gson gson = new Gson();
    @SpyBean
    private JavaMailSender javaMailSender;
    @SpyBean
    private TemplateEngine templateEngine;
    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

    }

    @Test
    @Ignore
    public void shouldResolveProperTemplateName() throws Exception {

        //given
        String to = "test@test.pl";
        String from = "testFrom@test.pl";
        String subject = "subject";
        Locale locale = Locale.US;

        SendMailRequest request = new SendMailRequestBuilder()
                .setTo(to)
                .setFrom(from)
                .setSubject(subject)
                .setTemplateType(template)
                .setTemplateParams(new HashMap<>())
                .setLocale(locale)
                .build();

        //when
        mockMvc.perform(post("/mail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(request)))
                .andDo(print())
                .andExpect(status().isOk());

        //then
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(templateEngine).process(argument.capture(), any(Context.class));
        assertEquals(template + "_" + locale.toString() + ".html", argument.getValue());
    }

}
