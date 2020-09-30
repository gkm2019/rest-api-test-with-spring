package com.kyeongmin.demorestapitest;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    //model Mapper는 공용 객체이므로 bean등록해서 사용 가능
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
