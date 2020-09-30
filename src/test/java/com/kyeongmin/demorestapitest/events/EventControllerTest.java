package com.kyeongmin.demorestapitest.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void createEvnet() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print()) //응답이 어떻게 나왔는지 console로 확인 가능
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())//id가 존재하는지 확인 exists()
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(100))) //id는 100이면 안됨
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
        ;
    }

    @Test //잘못된 값이 전달 될 경우 Bad req처리해야함
    public void createEvnet_Bad_Request() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print()) //응답이 어떻게 나왔는지 console로 확인 가능
                .andExpect(status().isBadRequest()) //bad request

        ;
    }

    @Test //비어있는 값이 전달된 경우 bad req처리해야함
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDTO eventDTO = EventDTO.builder().build(); //아무값도 없이 보내보자(비어있는 값)
        this.mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDTO)))
                .andExpect(status().isBadRequest())
        ;
    }

    @Test //비어있는 값이 전달된 경우 bad req처리해야함
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
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDTO)))
                .andExpect(status().isBadRequest())
        ;
    }
}