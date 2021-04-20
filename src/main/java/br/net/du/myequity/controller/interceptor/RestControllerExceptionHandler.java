package br.net.du.myequity.controller.interceptor;

import br.net.du.myequity.exception.SztoksException;
import br.net.du.myequity.exception.UserNotFoundException;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = RestController.class)
public class RestControllerExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleUserNotFoundException() {}

    @ExceptionHandler(SztoksException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public JsonErrorResponse handleSztoksException(final Exception e) {
        return JsonErrorResponse.builder().errorMessage(e.getMessage()).build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleException() {}

    @Builder
    @Data
    public static class JsonErrorResponse {
        private String errorMessage;
    }
}
