package com.kyeongmin.demorestapitest.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest
public class EventControllerTest {
    //네이버 같은 경우는 응답을 구분한다.
    //api.json -> json으로 응답
    //api.xml -> xml으로 응답
    //accept header로 지정해서 데이터 형식 정하는게 더 좋은 방법이다.
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper; //object 형식을 json으로 바꾸어준다.

    //기존의 EventRepository를 만들어놓기만 하면, web전용의 repository만 만들어준다.
    //mockBean사용해서 eventRepository 만들어달라고 요청함
    //mock객체는 그냥 save()하면 null만 나온다.
    //save호출됐을때 어떻게 동작할지 mockito.when("조건").thenReturn("결과") 명시해주기
    @MockBean
    EventRepository eventRepository;

    @Test
    public void createEvnet() throws Exception {
        //perform안에 있는 것이 요청임
        //andExpect()안에 응답 담긴다.
        //status().isCreated() 응답 201의미한다.

        Event event = Event.builder()
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
                //heaer()값 확인하기
                //ver1 : .andExpect(header().exists("Location"))
                .andExpect(header().exists(HttpHeaders.LOCATION)) //ver2 이렇게 해도됨
                //ver1 : .andExpect(header().string("Content-Type", "application/hal+json"))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(100))) //id는 100이면 안됨
                .andExpect(jsonPath("free").value(Matchers.not(true)))
        ;
    }
}