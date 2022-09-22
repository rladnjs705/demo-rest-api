package com.example.demorestapi.events;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource2 extends EntityModel<Event> {
    public EventResource2(Event event, Iterable<Link> links) {
        super(event, links);

        //TODO 공통 처리 부분이므로 self링크는 여기서 추가
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }
}
