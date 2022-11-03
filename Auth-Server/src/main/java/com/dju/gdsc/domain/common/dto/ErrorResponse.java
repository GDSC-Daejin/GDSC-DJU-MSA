package com.dju.gdsc.domain.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String message;
    private int status;
    private long timestamp;
    private List<FieldError> errors;
    public static ErrorResponse of(BindingResult bindingResult) {
        System.out.println("bindingResult = " + bindingResult);
        return new ErrorResponse("Invalid Input Value", 400, System.currentTimeMillis(), bindingResult.getFieldErrors());
    }
}