package br.net.du.myequity.controller.interceptor;

import br.net.du.myequity.model.User;
import br.net.du.myequity.service.UserService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = {Controller.class})
public class GlobalModelAttributes {
    public static final String LOGGED_USER = "loggedUser";

    @Autowired private UserService userService;

    @ModelAttribute
    public void loggedUser(final Model model) {
        final Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            final String email = authentication.getName();
            final User loggedUser = userService.findByEmail(email);
            if (loggedUser != null) {
                model.addAttribute(LOGGED_USER, Optional.of(loggedUser));
                return;
            }
        }

        model.addAttribute(LOGGED_USER, Optional.empty());
    }
}
