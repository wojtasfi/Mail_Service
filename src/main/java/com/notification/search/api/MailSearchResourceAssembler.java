package com.notification.search.api;

import com.notification.boundary.api.SearchController;
import com.notification.search.domain.dto.MailSearchHitDto;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class MailSearchResourceAssembler extends ResourceAssemblerSupport<MailSearchHitDto, MailSearchResource> {

    public MailSearchResourceAssembler() {
        super(SearchController.class, MailSearchResource.class);
    }

    @Override
    public MailSearchResource toResource(MailSearchHitDto mailSearchHitDto) {
        return MailSearchResource.from(mailSearchHitDto);
    }
}
