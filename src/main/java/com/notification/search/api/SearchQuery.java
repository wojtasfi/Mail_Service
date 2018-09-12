package com.notification.search.api;

import lombok.Data;

@Data
public class SearchQuery {
    private String text;
    private String fromDate;
    private String toDate;

    public SearchQuery(String text, String fromDate, String toDate) {
        this.text = text;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
}
