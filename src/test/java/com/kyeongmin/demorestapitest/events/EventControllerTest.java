package com.kyeongmin.demorestapitest.events;

import com.kyeongmin.demorestapitest.common.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTest extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Test
    @DisplayName("정상적으로 이벤트를 생성하는 Test")
    public void createEvent() throws Exception {
        EventDTO event = EventDTO.builder()
                .name("spring")
                .description("rest api dev with spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 9, 30, 23, 28))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 10, 1, 23, 28))
                .beginEventDateTime(LocalDateTime.of(2020, 10, 2, 23, 28))
                .endEventDateTime(LocalDateTime.of(2020, 10, 3, 23, 28))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("kangNam Station D2 startUP factory")
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print()) //응답이 어떻게 나왔는지 console로 확인 가능
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())//id가 존재하는지 확인 exists()
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                //.andDo(document("이 문서의 이름"))
                //snippet추가는 andDo document안에다 해야한다.
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing event"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                //content type 확인해보기
                                headerWithName(HttpHeaders.ACCEPT).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields( //요청필드
                                //요청으로 받는 것은?
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("response header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("free").description("it tells if this event is free event or not"),
                                fieldWithPath("offline").description("it tells if this event is offline event or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-event.href").description("link to update existing event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("입력 받을 수 없는 값이 전달될 경우 error발생하는 Test")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("spring")
                .description("rest api dev with spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 9, 30, 23, 28))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 10, 1, 23, 28))
                .beginEventDateTime(LocalDateTime.of(2020, 10, 2, 23, 28))
                .endEventDateTime(LocalDateTime.of(2020, 10, 3, 23, 28))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("kangNam Station D2 startUP factory")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print()) //응답이 어떻게 나왔는지 console로 확인 가능
                .andExpect(status().isBadRequest()) //bad request
        ;
    }

    @Test
    @DisplayName("입력값이 비어있는 경우 error 발생하는 Test")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDTO eventDTO = EventDTO.builder().name("test_test").build(); //아무값도 없이 보내보자(비어있는 값)
        this.mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(this.objectMapper.writeValueAsString(eventDTO)))
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("입력값이 잘못 된 경우 error 발생하는 Test")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDTO eventDTO = EventDTO.builder()
                .name("spring")
                .description("rest api dev with spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 10, 2, 23, 28))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 10, 1, 23, 28))
                .beginEventDateTime(LocalDateTime.of(2020, 10, 2, 23, 28))
                .endEventDateTime(LocalDateTime.of(2020, 10, 3, 23, 28))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("kangNam Station D2 startUP factory")
                .build();

        this.mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].field").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @DisplayName("30개의 이벤트를 10개씩, 두번째 페이지 조회하기")
    public void queryEvents() throws Exception {
        //Given
        //이벤트 30개..
        IntStream.range(0, 30).forEach(this::generateEvent);

        //When 
        //10개로 2번째 페이지 조회(get)
        this.mockMvc.perform(get("/api/events")
                .param("page", "1") //2번째 페이지
                .param("size", "10") //10개 묶음
                .param("sort", "name,DESC") //이름 역순으로 요청
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                //eventList [0]첫번째 요소에서 self link가 있는가?
                //하나의 세부 item 요소에 연결해주는 link가 생성된것이다.
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists()) //profile link 있는가? 확인
                .andDo(document("query-events"))
        //TODO link, page들에 대한 설명을담은 문서 추가로 생성해야함
        ;
    }

    @Test
    @DisplayName("기존의 이벤트 중 하나만 조회")
    public void getEvent() throws Exception {
        //Given
        Event event = this.generateEvent(100);

        //When & Then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"))
        //TODO event를 만든 user라면 update가능하게, 아니라면 불가능하게 : 유저 정보에 따라 구분지어야함 (모든 테스트에)
        ;
    }

    @Test
    @DisplayName("없는 이벤트를 조회했을 때 404 응답받기")
    public void getEvent404() throws Exception {
        //When & Then
        this.mockMvc.perform(get("/api/events/11883"))
                .andExpect(status().isNotFound()) // 존재 하지 않는 event조회했음
        ;
    }

    private Event generateEvent(int index) {
        Event event = Event.builder()
                .name("event" + index)
                .description("rest api dev with spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 9, 30, 23, 28))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 10, 1, 23, 28))
                .beginEventDateTime(LocalDateTime.of(2020, 10, 2, 23, 28))
                .endEventDateTime(LocalDateTime.of(2020, 10, 3, 23, 28))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("kangNam Station D2 startUP factory")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();

        return this.eventRepository.save(event);
    }

    @Test
    @DisplayName("이벤트 (정상) 수정")
    public void updateEvent() throws Exception {
        //Given
        Event event = this.generateEvent(200);
        //event객체에서 데이터를 담아서, mapper로 mapping
        EventDTO eventDTO = this.modelMapper.map(event, EventDTO.class);
        String eventName = "Update Event";
        eventDTO.setName(eventName);

        //When & Then
        //update request (put)
        //특정 {id}에 해당하는 event 수정할 것이다.
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                //데이터 보내기
                //타입은 json이다.
                .contentType(MediaType.APPLICATION_JSON)
                //event 객체 담아서 보낸다.
                .content(this.objectMapper.writeValueAsString(eventDTO))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                .andExpect(jsonPath("_links.self").exists())
        ;
    }

    //이벤트 입력값이 잘못 된 경우 2가지
    // 1. logic상 잘못된 경우, 2. 입력값 자체가 없는 경우, 3. 존재하지 않는 event 일 경우
    @Test
    @DisplayName("이벤트 (비정상) 수정실패 : 1. 입력값이 잘못 된경우 update 실패!")
    public void updateEvent400_Wrong() throws Exception {
        //Given
        Event event = this.generateEvent(200);
        EventDTO eventDTO = this.modelMapper.map(event, EventDTO.class);
        //최대치가 더 작을 경우 Wrong
        eventDTO.setBasePrice(20000);
        eventDTO.setMaxPrice(1000);

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDTO))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("이벤트 (비정상) 수정실패 : 2. 입력값이 비어있는 경우 update 실패!")
    public void updateEvent400_Empty() throws Exception {
        //Given
        Event event = this.generateEvent(200);
        EventDTO eventDTO = new EventDTO();

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDTO))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("이벤트 (비정상) 수정실패 : 3. 존재하지 않는 event일 경우 update 실패!")
    public void updateEvent400() throws Exception {
        //Given
        Event event = this.generateEvent(200);
        //event객체에서 데이터를 담아서, mapper로 mapping
        EventDTO eventDTO = this.modelMapper.map(event, EventDTO.class);

        //When & Then
        //update request (put)
        //특정 {id}에 해당하는 event 수정할 것이다.
        this.mockMvc.perform(put("/api/events/1231232")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDTO))
        )
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }
}