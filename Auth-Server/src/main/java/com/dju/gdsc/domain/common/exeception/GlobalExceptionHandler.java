package com.dju.gdsc.domain.common.exeception;

import com.dju.gdsc.domain.common.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

   @ExceptionHandler(MethodArgumentNotValidException.class)
   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
       System.out.println("handleMethodArgumentNotValidException");
       return ErrorResponse.of(e.getBindingResult());
   }
}
