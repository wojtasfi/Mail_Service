package com.notification.search.service;

import com.google.common.io.Files;
import com.notification.NotificationServiceApplication;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;

import javax.mail.internet.MimeMessage;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NotificationServiceApplication.class)
public class TestWithElasticSearch {

    protected final String INDEX = "mail";
    @Autowired
    protected Client client;
    @SpyBean
    private JavaMailSender javaMailSender;
    @Value("${spring.data.elasticsearch.cluster-name}")
    private String clusterName;
    @Value("${spring.data.elasticsearch.cluster-nodes}")
    private String nodes;
    private EmbeddedElastic embeddedElastic;

    private Resource resource = new ClassPathResource("mail.json");
    @Before
    public void setUp() throws IOException {
        //todo implement in future, for now not connecting
//        embeddedElastic = EmbeddedElastic.builder()
//                .withElasticVersion("5.5.2")
//                .withSetting(PopularProperties.TRANSPORT_TCP_PORT, 9300)
//                .withSetting(PopularProperties.HTTP_PORT, 6777)
//                .withSetting(PopularProperties.CLUSTER_NAME, clusterName)
//                .build()
//                .start();

        byte[] mappings = Files.toByteArray(resource.getFile());

        client.admin().indices()
                .prepareCreate(INDEX)
                .setSource(mappings, XContentType.JSON)
                .execute();
        doNothing().when(javaMailSender).send(any(MimeMessage.class));
        when(javaMailSender.createMimeMessage()).thenCallRealMethod();
    }

    @After
    public void cleanUp() {
//        embeddedElastic.stop();
        client.admin().indices().prepareDelete(INDEX).execute();
    }
}
