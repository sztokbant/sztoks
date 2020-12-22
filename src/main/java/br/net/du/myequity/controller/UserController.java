package br.net.du.myequity.controller;

import static br.net.du.myequity.controller.util.ControllerConstants.REDIRECT_TO_HOME;

import br.net.du.myequity.model.User;
import br.net.du.myequity.service.SecurityService;
import br.net.du.myequity.service.UserService;
import br.net.du.myequity.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    private static final String SIGNUP_MAPPING = "/signup";
    private static final String SIGNUP_TEMPLATE = "signup";
    private static final String USER_FORM = "userForm";

    @Autowired private UserService userService;

    @Autowired private SecurityService securityService;

    @Autowired private UserValidator userValidator;

    @GetMapping(SIGNUP_MAPPING)
    public String signup(final Model model) {
        model.addAttribute(USER_FORM, new User());

        return SIGNUP_TEMPLATE;
    }

    @PostMapping(SIGNUP_MAPPING)
    public String signup(
            @ModelAttribute(USER_FORM) final User userForm, final BindingResult bindingResult) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return SIGNUP_TEMPLATE;
        }

        userService.save(userForm);
        securityService.autoLogin(userForm.getEmail(), userForm.getPasswordConfirm());

        return REDIRECT_TO_HOME;
    }

    @GetMapping("/login")
    public String login(final Model model, final String error, final String logout) {
        if (error != null) {
            model.addAttribute("error", "Invalid E-mail or Password.");
        }

        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }

        return "login";
    }
}
