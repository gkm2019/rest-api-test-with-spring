package com.kyeongmin.demorestapitest.events;

import com.kyeongmin.demorestapitest.commons.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDTO eventDTO, Errors errors) {
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));
        }
        eventValidator.validate(eventDTO, errors);
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));
        }

        Event event = modelMapper.map(eventDTO, Event.class);
        event.update();
        Event newEvent = this.eventRepository.save(event);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdURI = selfLinkBuilder.toUri();

        //link를 추가해서 HATEOAS성질을 부여한다.
        EventResource eventResource = new EventResource(event);
        //이벤트 목록들을 담고있는 link
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        //이벤트 업데이트 정보를 담고있는 link
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        //프로필 추가
        eventResource.add(new Link("/docs/index.html").withRel("profile"));
        return ResponseEntity.created(createdURI).body(eventResource);
    }
}
