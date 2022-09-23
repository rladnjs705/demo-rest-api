package com.example.demorestapi.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource extends EntityModel<Event> {

    public static EntityModel<Event> modelOf(Event event) {
        EntityModel<Event> model = EntityModel.of(event);

        //TODO 공통 처리 부분이므로 self링크는 여기서 추가
        model.add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
        return model;
    }
}
