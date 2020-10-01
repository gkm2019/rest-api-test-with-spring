package com.kyeongmin.demorestapitest.events;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource extends EntityModel<Event> {
    public EventResource(Event event, Link... links) {
        super(event, links);
        //self로 가는 link 추가하기
        //같은 의미임 그러나 type-safe하지 않음 mapping정보 바뀔때마다 적용 못함
        //add(new Link("http://localhost:8080/api/events/"+event.getId()));
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());

    }
}