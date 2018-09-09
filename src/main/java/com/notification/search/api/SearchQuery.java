package com.notification.search.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class SearchQuery {
    private String text;
    private LocalDate fromDate;
    private LocalDate toDate;
}
