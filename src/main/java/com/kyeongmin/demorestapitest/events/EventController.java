package com.kyeongmin.demorestapitest.events;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    //modelMapper 사용해서 set(name)등등 하나씩 mapping해주는거 생략 가능함
    //MavenRepository 가서 modelMapper검색해서 최신버전 maven 의존성 추가
    private final ModelMapper modelMapper;

    //생성자가 1개만 있고, 그 안의 파라미터가 이미 bean에 등록되어있다면 @Autowired 생략해도됨
    public EventController(EventRepository eventRepository, ModelMapper modelMapper) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
    }

    //이미 위에서 requestMapping으로 api/evnets설정해줘서 안해줘도됨 methodOn 없어도됨
    //@PostMapping("/api/events")
    @PostMapping
    public ResponseEntity createEvent(@RequestBody EventDTO eventDTO) {

        Event event = modelMapper.map(eventDTO, Event.class);
        Event newEvent = this.eventRepository.save(event);
        URI createdURI = linkTo(EventController.class).slash(newEvent.getId()).toUri();
        return ResponseEntity.created(createdURI).body(event);
    }
}
