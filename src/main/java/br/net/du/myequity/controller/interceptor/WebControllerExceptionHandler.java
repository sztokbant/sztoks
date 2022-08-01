package br.net.du.myequity.controller.interceptor;

import static br.net.du.myequity.controller.util.ControllerConstants.REDIRECT_TO_HOME;
import static br.net.du.myequity.controller.util.ControllerConstants.REDIRECT_TO_LOGIN;

import br.net.du.myequity.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(annotations = WebController.class)
@Slf4j
public class WebControllerExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException() {
        // TODO Error message
        return REDIRECT_TO_LOGIN;
    }

    @ExceptionHandler(Exception.class)
    public String handleException(final Exception exception) {
        log.error(exception.getMessage(), exception);
        return REDIRECT_TO_HOME;
    }
}
