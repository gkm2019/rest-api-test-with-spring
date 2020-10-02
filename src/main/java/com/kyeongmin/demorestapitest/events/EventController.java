package com.kyeongmin.demorestapitest.events;

import com.kyeongmin.demorestapitest.commons.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

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
            return badRequest(errors);
        }
        eventValidator.validate(eventDTO, errors);
        if(errors.hasErrors()){
            return badRequest(errors);
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
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createdURI).body(eventResource);
    }

    @GetMapping //get으로 조회한다... get으로 작성해놓고 post요청하면 405error발생함
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler){
        Page<Event> page = this.eventRepository.findAll(pageable);
        //page와 관련한 정보를 넘겨준다.
        //현재 페이지, 이전 페이지, 다음 페이지 등의 link 정보를 말한다.
        //e -> () 페이지 하나하나의 목록에 link를 생성해서 HATEOAS성질을 부여한다.
        var pagedResources = assembler.toModel(page, e -> new EventResource(e) );
        pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(pagedResources);
    }

    //overriding이 아니라 경로 뒤에 {id} 덧붙여진다.
    //조회
    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id){
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if(optionalEvent.isEmpty()){ //option event 비어있으면
            return ResponseEntity.notFound().build(); //nofound 전송 (404 에러 뜰것임)
        }
        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }

    //업데이트
    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                       @RequestBody EventDTO eventDTO, Errors errors){

        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if(optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        //수정 실패하는 경우들..(비어있거나, 이상한 로직의 값이거나, 존재하지 않는 값일경우..등)
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        //값을 제대로 넘겨줬음에도 error발생했다면? logic상의 error이다.
        this.eventValidator.validate(eventDTO, errors);
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        //문제없으면 update가능
        Event existingEvent = optionalEvent.get();
        //현재 event(existingEvent) 전부 EventDTO에 있는 값들이랑 맵핑 해준다.
        //existingEvent.setName(eventDTO.getName()) 이런식으로.. modelMapper가 다 해줌
        //map(어디에서, 어디로)
        //기존에있던 eventDTO에서 existingEvent로
        this.modelMapper.map(eventDTO, existingEvent);
        Event savedEvent = this.eventRepository.save(existingEvent);
        EventResource eventResource = new EventResource(savedEvent);
        eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }

    private ResponseEntity<ErrorsResource> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
