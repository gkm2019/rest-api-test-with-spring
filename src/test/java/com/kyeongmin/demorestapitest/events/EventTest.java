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

    @Test
    public void testFree(){
        //Given
        Event event = Event.builder()
                .basePrice(0)
                .maxPrice(0)
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isFree()).isTrue();

        //Given
        event = Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isFree()).isFalse(); //base값이 있으면 free이면 안됨..free=false이어야함

        //Given
        event = Event.builder()
                .basePrice(0)
                .maxPrice(100)
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isFree()).isFalse(); //max있으면 free면 안됨 (무료 아님)
    }

    @Test //온라인/오프라인 여부 test
    public void testOffline(){
        //Given
        Event event = Event.builder()
                .location("kangnam station naver D2 factory")
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isOffline()).isTrue(); //장소있으면 offline

        //Given
        event = Event.builder()
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isOffline()).isFalse(); //장소없으면 false

    }
}