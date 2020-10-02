package com.kyeongmin.demorestapitest.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyeongmin.demorestapitest.common.RestDocsConfiguration;
import com.kyeongmin.demorestapitest.commons.TestDescription;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class) //다른 bean 불러와서 사용
@ActiveProfiles("test")
public class EventControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 Test")
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
    @TestDescription("입력 받을 수 없는 값이 전달될 경우 error발생하는 Test")
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
    @TestDescription("입력값이 비어있는 경우 error 발생하는 Test")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDTO eventDTO = EventDTO.builder().name("test_test").build(); //아무값도 없이 보내보자(비어있는 값)
        this.mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(this.objectMapper.writeValueAsString(eventDTO)))
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("입력값이 잘못 된 경우 error 발생하는 Test")
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
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(this.objectMapper.writeValueAsString(eventDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
                .andExpect(jsonPath("$[0].rejectedValue").exists())
        ;
    }
}