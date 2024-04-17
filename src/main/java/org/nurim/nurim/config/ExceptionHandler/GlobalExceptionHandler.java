package org.nurim.nurim.config.ExceptionHandler;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler { //추상 클래스 상속

    // NotFoundAccountException 발생시 에러 처리
    @ExceptionHandler(Exception.class)
    public String handleException(final Exception e){
        return e.getMessage();
    }
}
