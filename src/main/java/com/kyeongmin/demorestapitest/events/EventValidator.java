package com.kyeongmin.demorestapitest.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {

    public void validate(EventDTO eventDTO, Errors errors){
        if(eventDTO.getBasePrice() > eventDTO.getMaxPrice() && eventDTO.getMaxPrice() !=0){
            errors.rejectValue("basePrice","wrongValue","BasePrice is wrong.");
            errors.rejectValue("maxPrice","wrongValue","MaxPrice is wrong.");
            errors.reject("wrongPrices","Prices are wrong");//reject()하면 global 영역에 들어감
        }

        LocalDateTime endEventDateTime = eventDTO.getEndEventDateTime();
        if(endEventDateTime.isBefore(eventDTO.getBeginEventDateTime()) ||
        endEventDateTime.isBefore(eventDTO.getCloseEnrollmentDateTime()) ||
                endEventDateTime.isBefore(eventDTO.getBeginEnrollmentDateTime())){
            errors.rejectValue("endEventDateTime","wrongValue","endEventDateTime is wrong.");
        }

        //TODO beginEventDateTime
        //TODO CloseEnrollmentDateTime
    }
}
