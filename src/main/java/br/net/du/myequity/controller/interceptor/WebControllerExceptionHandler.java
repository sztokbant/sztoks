package br.net.du.myequity.controller.interceptor;

import static br.net.du.myequity.controller.util.ControllerConstants.REDIRECT_TO_HOME;
import static br.net.du.myequity.controller.util.ControllerConstants.REDIRECT_TO_LOGIN;

import br.net.du.myequity.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(annotations = WebController.class)
public class WebControllerExceptionHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(WebControllerExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException() {
        // TODO Error message
        return REDIRECT_TO_LOGIN;
    }

    @ExceptionHandler(Exception.class)
    public String handleException(final Exception exception) {
        LOGGER.error(exception.getMessage(), exception);
        return REDIRECT_TO_HOME;
    }
}
