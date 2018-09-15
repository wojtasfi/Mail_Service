package com.notification.search.service;

import com.google.gson.Gson;
import com.notification.NotificationServiceApplication;
import com.notification.search.MailDocumentBuilder;
import com.notification.search.domain.MailDocument;
import com.notification.sender.SendMailRequestBuilder;
import com.notification.sender.api.SendMailRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.notification.search.service.MailSearchServiceImpl.*;
import static java.util.Optional.ofNullable;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


//todo figure out how flush works, because when all test are run together than ES sometimes will not manage to index test data in time
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NotificationServiceApplication.class)
@AutoConfigureMockMvc
public class MailSearchServiceImplIntegrationTest extends TestWithElasticSearch {

    private final String contentTemplate = "test_mail_content";
    private final Gson gson = new Gson();
    @Autowired
    private MailSearchService mailSearchService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldIndexDocumentWithCorrectFields() throws InterruptedException {

        //given
        String to = "test@test.pl";
        String from = "testFrom@test.pl";
        String subject = "subject";
        String html = "<div>html</div>";
        String text = "html";

        MailDocument mailDocument = new MailDocumentBuilder()
                .setTo(to)
                .setFrom(from)
                .setSubject(subject)
                .setHtmlContent(html)
                .setRawTextContent(text)
                .createMailDocument();

        //when
        mailSearchService.save(mailDocument);

        //then
        Map<String, Object> mail = getOneMailFromEs();

        assertEquals(mailDocument.getTo(), mail.get(TO_FIELD).toString());
        assertEquals(mailDocument.getFrom(), mail.get(FROM_FIELD).toString());
        assertEquals(mailDocument.getSubject(), mail.get(SUBJECT_FIELD).toString());
        assertEquals(mailDocument.getHtmlContent(), mail.get(HTML_CONTENT_FIELD).toString());
        assertEquals(mailDocument.getRawTextContent(), mail.get(RAW_TEXT_CONTENT_FIELD).toString());

    }

    @Test
    public void shouldSendAndSaveMailToES() throws Exception {
        String to = "test@test.pl";
        String from = "testFrom@test.pl";
        String subject = "subject";

        SendMailRequest sendMailRequest = new SendMailRequestBuilder()
                .setTo(to)
                .setFrom(from)
                .setSubject(subject)
                .setTemplateType(contentTemplate)
                .setTemplateParams(new HashMap<>())
                .build();

        mockMvc.perform(post("/mail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(sendMailRequest)))
                .andDo(print())
                .andExpect(status().isOk());


        client.admin().indices().flush(new FlushRequest()).actionGet();
        Map<String, Object> mail = getOneMailFromEs();

        assertEquals(to, mail.get(TO_FIELD).toString());
        assertEquals(from, mail.get(FROM_FIELD).toString());
        assertEquals(subject, mail.get(SUBJECT_FIELD).toString());
        assertEquals("<!DOCTYPE html>" +
                "<body>" +
                "<p>Hello World</p>" +
                "</body>" +
                "</html>", mail.get(HTML_CONTENT_FIELD).toString()
        );
        assertEquals("Hello World", mail.get(RAW_TEXT_CONTENT_FIELD).toString());
    }


    @Test
    public void shouldReturnCorrectMailWithTextSearch() throws Exception {
        //given
        String to = "test@test.pl";
        String from = "testFrom@test.pl";
        String subject = "subject";
        String text1 = "html";
        String text2 = "content";
        String text3 = "content";

        MailDocument mailDocument1 = new MailDocumentBuilder()
                .setTo(to)
                .setFrom(from)
                .setSubject(subject)
                .setRawTextContent(text1)
                .createMailDocument();

        MailDocument mailDocument2 = new MailDocumentBuilder()
                .setTo(to)
                .setFrom(from)
                .setSubject(subject)
                .setRawTextContent(text2)
                .createMailDocument();

        MailDocument mailDocument3 = new MailDocumentBuilder()
                .setTo(to)
                .setFrom(from)
                .setSubject(subject)
                .setRawTextContent(text3)
                .createMailDocument();

        //when
        mailSearchService.save(mailDocument1, mailDocument2, mailDocument3);
        flush();
        //then
        mockMvc.perform(get("/search")
                .param("text", text1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].to", is(mailDocument1.getTo())))
                .andExpect(jsonPath("$[0].rawTextContent", is(mailDocument1.getRawTextContent())))
                .andExpect(jsonPath("$[0].from", is(mailDocument1.getFrom())))
                .andExpect(jsonPath("$[0].date", is(mailDocument1.getDate())));
    }

    @Test
    public void shouldReturnCorrectMailWithDateSearchForSameDayww() throws Exception {
        //given
        String to = "test@test.pl";
        String from = "testFrom@test.pl";
        String subject = "subject";
        String text = "html";
        String text2 = "html2";
        int year = 2018;
        int month = 12;
        int day = 24;

        String date1 = LocalDateTime.of(year, month, day + 1, 1, 15, 0).format(DATE_FORMAT);
        String date2 = LocalDateTime.of(year, month, day, 1, 20, 0).format(DATE_FORMAT);
        String date3 = LocalDateTime.of(year, month, day - 1, 1, 25, 0).format(DATE_FORMAT);

        MailDocument mailDocument1 = new MailDocumentBuilder()
                .setTo(to)
                .setFrom(from)
                .setSubject(subject)
                .setRawTextContent(text2)
                .setDate(date1)
                .createMailDocument();

        MailDocument mailDocument2 = new MailDocumentBuilder()
                .setTo(to)
                .setFrom(from)
                .setSubject(subject)
                .setRawTextContent(text)
                .setDate(date2)
                .createMailDocument();

        MailDocument mailDocument3 = new MailDocumentBuilder()
                .setTo(to)
                .setFrom(from)
                .setSubject(subject)
                .setRawTextContent(text)
                .setDate(date3)
                .createMailDocument();

        //when
        mailSearchService.save(mailDocument1, mailDocument2, mailDocument3);
        flush();
        //then
        String fromDate = LocalDateTime.of(year, month, day, 0, 0, 0).format(DATE_FORMAT);
        String toDate = LocalDateTime.of(year, month, day, 0, 0, 0).format(DATE_FORMAT);
        mockMvc.perform(get("/search")
                .param("from", fromDate)
                .param("to", toDate))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].to", is(mailDocument2.getTo())))
                .andExpect(jsonPath("$[0].rawTextContent", is(mailDocument2.getRawTextContent())))
                .andExpect(jsonPath("$[0].from", is(mailDocument2.getFrom())))
                .andExpect(jsonPath("$[0].date", is(mailDocument2.getDate())));
    }

    private void flush() throws InterruptedException {
        TimeUnit.SECONDS.sleep(5);
        client.admin().indices().prepareFlush(INDEX).execute().actionGet().getStatus();
    }

    private Map<String, Object> getOneMailFromEs() throws InterruptedException {
        flush();
        SearchResponse response = client.prepareSearch(INDEX)
                .setTypes(INDEX)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchAllQuery())
                .get();

        return ofNullable(response.getHits())
                .map(hits -> hits.getAt(0))
                .map(SearchHit::getSourceAsMap)
                .orElse(new HashMap<>());
    }
}
