package com.notification.search.service;

import com.notification.search.api.SearchQuery;
import com.notification.search.domain.MailDocument;
import com.notification.search.domain.dto.MailSearchHitDto;
import com.notification.search.util.ElasticsearchUtilities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSearchServiceImpl implements MailSearchService {

    private final Client client;
    private final ElasticsearchUtilities esUtils;
    private final String MAIL_INDEX = "mail";

    @Override
    public void save(MailDocument mailDocument) {
        String mailId = UUID.randomUUID().toString();
        try {
            client.prepareIndex()
                    .setIndex(MAIL_INDEX)
                    .setType(MAIL_INDEX)
                    .setId(mailId)
                    .setSource(esUtils.buildMailSource(mailDocument))
                    .execute();
        } catch (Exception e) {
            //todo put it on kafka with retry policy or send email to admin
            log.error("Failed to index MailDocument with id {}", mailId, e);
        }
    }

    @Override
    public List<MailSearchHitDto> search(SearchQuery searchQuery, Pageable pageable) {

        QueryBuilder dateQuery = QueryBuilders
                .rangeQuery("date")
                .from(searchQuery.getFromDate())
                .to(searchQuery.getToDate())
                .includeLower(false)
                .includeUpper(false);

        String text = searchQuery.getText();

        //todo implement sort
        SearchResponse response = client.prepareSearch(MAIL_INDEX)
                .setQuery(new TermQueryBuilder(TO_FIELD, text))
                .setQuery(new TermQueryBuilder(FROM_FIELD, text))
                .setQuery(new TermQueryBuilder(SUBJECT_FIELD, text))
                .setQuery(new TermQueryBuilder(RAW_TEXT_CONTENT_FIELD, text))
                .setQuery(dateQuery)
                .setFrom(Long.valueOf(pageable.getOffset()).intValue())
                .setSize(pageable.getPageSize())
//                .addSort()
                .execute()
                .actionGet();

        return esUtils.convertHitsToMailSearchHitDto(response);
    }
}
