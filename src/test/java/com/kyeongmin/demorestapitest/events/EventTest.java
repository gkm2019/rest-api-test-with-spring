package com.kyeongmin.demorestapitest.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EventTest {
    @Test
    public void builder() {
        Event event = Event.builder()
                .name("spring REST API")
                .description("rest api dev with spring")
                .build();
        assertThat(event).isNotNull();
    }

    //javabean 규칙 준수, default 생성자
    @Test
    public void javaBean(){
        //Given
        Event event = new Event();
        String name = "Event";
        String description = "spring";

        //When
        event.setName(name);
        event.setDescription(description);

        //Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }
}