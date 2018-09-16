package com.notification.sender.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.notification.NotificationServiceApplication;
import com.notification.config.MailEventPublisher;
import com.notification.integration.msg.MailMessageListener;
import com.notification.sender.SendMailMessageBuilder;
import com.notification.sender.SendMailRequestBuilder;
import com.notification.sender.integration.api.SendMailRequest;
import com.notification.sender.integration.msg.SendMailMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.mail.internet.MimeMessage;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NotificationServiceApplication.class)
@AutoConfigureMockMvc
public class SenderIntegrationTest {

    @SpyBean
    private JavaMailSender javaMailSender;

    @SpyBean
    private MailService mailService;

    @SpyBean
    private MailMessageListener mailMessageListener;

    @SpyBean
    private MailEventPublisher publisher;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private final String template = "test_mail";
    private final Gson gson = new Gson();

    @Before
    public void setUp() {
        doNothing().when(javaMailSender).send(any(MimeMessage.class));
        doNothing().when(publisher).publishEvent(any());
    }

    @Test
    public void shouldSendSimpleTextMail() throws Exception {

        String to = "test@test.pl";
        String from = "testFrom@test.pl";
        String subject = "subject";

        SendMailRequest sendMailRequest = new SendMailRequestBuilder()
                .setTo(to)
                .setFrom(from)
                .setSubject(subject)
                .setTemplateType(template)
                .setTemplateParams(new HashMap<>())
                .build();

        mockMvc.perform(post("/mail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(sendMailRequest)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mailService, times(1)).sendMail(sendMailRequest.toDto());
    }

    @Test
    public void shouldSendSimpleTextMailWithMessageFromKafka() throws Exception {

        String to = "test@test.pl";
        String from = "testFrom@test.pl";
        String subject = "subject";

        SendMailMessage sendMailMessage = new SendMailMessageBuilder()
                .to(to)
                .from(from)
                .subject(subject)
                .templateType(template)
                .templateParams(new HashMap<>())
                .build();

        mailMessageListener.handleSendMailMessage(mapper.writeValueAsString(sendMailMessage));

        verify(mailService, times(1)).sendMail(sendMailMessage.toDto());
    }

    @Test
    public void shouldRejectNullSubject() throws Exception {

        String to = "test@test.pl";
        String from = "testFrom@test.pl";

        SendMailRequest sendMailRequest = new SendMailRequestBuilder()
                .setTo(to)
                .setFrom(from)
                .setSubject(null)
                .setTemplateType(template)
                .setTemplateParams(new HashMap<>())
                .build();

        MvcResult result = mockMvc.perform(post("/mail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(sendMailRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals(content, "Following fields have incorrect values: Field error in object 'sendMailRequest' " +
                "on field 'subject': rejected value [null]; codes [sendMailRequest.subject,subject,java.lang.String,]; " +
                "arguments []; default message [Subject must not be null]\n");
    }

    @Test
    public void shouldRejectWrongMailFormat() throws Exception {

        String to = "test.pl";
        String from = "testFrom@test.pl";
        String subject = "subject";

        SendMailRequest sendMailRequest = new SendMailRequestBuilder()
                .setTo(to)
                .setFrom(from)
                .setSubject(subject)
                .setTemplateType(template)
                .setTemplateParams(new HashMap<>())
                .build();

        MvcResult result = mockMvc.perform(post("/mail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(sendMailRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertEquals(content, "Following fields have incorrect values: Field error in object 'sendMailRequest' " +
                "on field 'to': rejected value [test.pl]; codes [sendMailRequest.to,to,java.lang.String,]; arguments []; " +
                "default message [Not valid email address: test.pl]\n");
    }

}
