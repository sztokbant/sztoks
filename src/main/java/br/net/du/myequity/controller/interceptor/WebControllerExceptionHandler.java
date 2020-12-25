package br.net.du.myequity.controller.interceptor;

import static br.net.du.myequity.controller.util.ControllerConstants.REDIRECT_TO_HOME;
import static br.net.du.myequity.controller.util.ControllerConstants.REDIRECT_TO_LOGIN;

import br.net.du.myequity.exception.UserNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(annotations = WebController.class)
public class WebControllerExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException() {
        // TODO Error message
        return REDIRECT_TO_LOGIN;
    }

    @ExceptionHandler(Exception.class)
    public String handleException() {
        // TODO Error message
        return REDIRECT_TO_HOME;
    }
}
