package com.notification.search.api;

import com.notification.boundary.api.SearchController;
import com.notification.search.service.MailSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SearchControllerImpl implements SearchController {

    private final MailSearchService searchService;
    private final MailSearchResourceAssembler assembler;

    @Override
    public List<MailSearchResource> search(@PageableDefault Pageable pageable,
                                           @RequestParam String text,
                                           @RequestParam LocalDate from,
                                           @RequestParam LocalDate to,
                                           Errors errors) {
        List<MailSearchResource> resources = searchService.search(new SearchQuery(text, from, to), pageable)
                .stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());
        resources.forEach(resource ->
                resource.add(linkTo(methodOn(SearchControllerImpl.class).getOne(resource.getMailId())).withSelfRel()));

        return resources;
    }

    @Override
    public MailSearchResource getOne(UUID id) {
        return null;
    }
}
