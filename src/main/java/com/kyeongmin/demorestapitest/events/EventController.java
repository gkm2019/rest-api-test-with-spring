package com.kyeongmin.demorestapitest.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {
    private final EventRepository eventRepository;

    //생성자가 1개만 있고, 그 안의 파라미터가 이미 bean에 등록되어있다면 @Autowired 생략해도됨
    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    //이미 위에서 requestMapping으로 api/evnets설정해줘서 안해줘도됨 methodOn 없어도됨
    //@PostMapping("/api/events")
    @PostMapping
    public ResponseEntity createEvent(@RequestBody Event event) {
        Event newEvent = this.eventRepository.save(event);
        //createEvent()안에 evnet라는 파라미터를 추가해서 createEvent(null)을 넣어야함..
        //귀찮지? 그렇다면 위의 @RequestMapping(value=)설정하기
        URI createdUri = linkTo(EventController.class).slash("{id}").toUri();
        event.setId(10);
        return ResponseEntity.created(createdUri).body(event); //uri넣고, body에 event넣어서 빌드
    }
}
