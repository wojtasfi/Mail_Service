package com.notification.integration.msg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification.sender.domain.dto.MailDto;
import com.notification.sender.integration.msg.ResendErrorMessage;
import com.notification.sender.integration.msg.SendMailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailMessageSender {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    @Value("${mail.error.retries}")
    private int retries;
    public static final String resendTopic = "resend";

    public void putMessageOnResendTopic(MailDto mailDto) {
        putMessageOnResendTopic(ResendErrorMessage.from(mailDto, retries));
    }

    public void putMessageOnResendTopic(SendMailMessage message) {
        val resendMessage = ResendErrorMessage.from(message, retries);
        putMessageOnResendTopic(resendMessage);
    }

    public void putMessageOnResendTopic(ResendErrorMessage message) {
        try {
            kafkaTemplate.send(resendTopic, mapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            log.error("Could not write ResendErrorMessage as String", e);
        }
    }
}
