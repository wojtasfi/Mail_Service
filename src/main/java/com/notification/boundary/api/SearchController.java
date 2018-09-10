package com.notification.boundary.api;

import com.notification.search.api.MailSearchResource;
import io.swagger.annotations.Api;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Api(tags = "Search")
@RestController
@RequestMapping(SearchController.BASE_PATH)
public interface SearchController {

    String BASE_PATH = "search";

    @GetMapping
    List<MailSearchResource> search(@PageableDefault Pageable pageable,
                                    @RequestParam(required = false) String text,
                                    @RequestParam(required = false) LocalDate from,
                                    @RequestParam(required = false) LocalDate to);

    @GetMapping("/{id}")
    MailSearchResource getOne(@PathVariable UUID id);
}

