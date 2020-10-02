package com.kyeongmin.demorestapitest.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {

    public void validate(EventDTO eventDTO, Errors errors) {
        if (eventDTO.getBasePrice() > eventDTO.getMaxPrice() && eventDTO.getMaxPrice() != 0) {
            errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong.");
            errors.rejectValue("maxPrice", "wrongValue", "MaxPrice is wrong.");
            errors.reject("wrongPrices", "Prices are wrong");//reject()하면 global 영역에 들어감
        }

        LocalDateTime endEventDateTime = eventDTO.getEndEventDateTime();
        if (endEventDateTime.isBefore(eventDTO.getBeginEnrollmentDateTime()) ||
                endEventDateTime.isBefore(eventDTO.getBeginEventDateTime()) ||
                endEventDateTime.isBefore(eventDTO.getCloseEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime is wrong.");
        }

        // TODO BeginEventDateTime
        LocalDateTime beginEventDateTime = eventDTO.getBeginEventDateTime();
        if (beginEventDateTime.isBefore(eventDTO.getBeginEnrollmentDateTime()) ||
                beginEventDateTime.isBefore(eventDTO.getCloseEnrollmentDateTime())) {
            errors.rejectValue("beginEventDateTime", "wrongValue", "BeginEventDateTime is Wrong");
        }

        // TODO ClosedEnrollmentDateTime
        LocalDateTime closeEnrollmentDateTime = eventDTO.getCloseEnrollmentDateTime();
        if (closeEnrollmentDateTime.isBefore(eventDTO.getBeginEnrollmentDateTime())) {
            errors.rejectValue("closeEnrollmentDateTime", "wrongValue", "CloseEnrollmentDateTime is Wrong");
        }
    }
}
