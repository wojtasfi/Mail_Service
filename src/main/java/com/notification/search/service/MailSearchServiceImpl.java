package com.notification.search.service;

import com.notification.search.api.SearchQuery;
import com.notification.search.domain.MailDocument;
import com.notification.search.domain.dto.MailSearchHitDto;
import com.notification.search.util.ElasticsearchUtilities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;
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
        SearchRequestBuilder builder = client.prepareSearch(MAIL_INDEX);

        setTextSearch(builder, searchQuery);
        setDateSearch(builder, searchQuery);
//        setPagination(builder, pageable);

        SearchResponse response = builder
                .execute()
                .actionGet();

        return esUtils.convertHitsToMailSearchHitDto(response);
    }

    private void setPagination(SearchRequestBuilder builder, Pageable pageable) {
        builder.setFrom(Long.valueOf(pageable.getOffset()).intValue())
                .setSize(pageable.getPageSize());
        //todo implement sort
        //                .addSort()
    }

    private void setDateSearch(SearchRequestBuilder builder, SearchQuery searchQuery) {

        String fromDate = searchQuery.getFromDate();
        String toDate = searchQuery.getToDate();

        if (fromDate == null && toDate == null) {
            return;
        }

        RangeQueryBuilder dateQuery = QueryBuilders
                .rangeQuery("date");

        if (fromDate != null && toDate == null) {
            dateQuery
                    .from(fromDate)
                    .includeLower(false)
                    .includeUpper(true);
        }
        if (fromDate == null && toDate != null) {
            dateQuery
                    .to(toDate)
                    .includeLower(true)
                    .includeUpper(false);
        }
        if (fromDate != null && toDate != null) {
            dateQuery
                    .from(fromDate)
                    .to(toDate);
        }

        builder.setQuery(dateQuery);
    }

    private void setTextSearch(SearchRequestBuilder builder, SearchQuery searchQuery) {
        String text = searchQuery.getText();
        if (text == null) {
            return;
        }
        builder.setQuery(new SimpleQueryStringBuilder(text)
                .field(TO_FIELD)
                .field(FROM_FIELD)
                .field(SUBJECT_FIELD)
                .field(RAW_TEXT_CONTENT_FIELD));
    }
}
