package br.net.du.myequity.controller;

import br.net.du.myequity.model.User;
import br.net.du.myequity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseController {
    @Autowired
    private UserService userService;

    protected User getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("Authentication is null");
        }

        final String email = authentication.getName();

        return userService.findByEmail(email);
    }
}
