package br.net.du.myequity.controller;

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
    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;

    @GetMapping("/signup")
    public String signup(final Model model) {
        model.addAttribute("userForm", new User());

        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("userForm") final User userForm, final BindingResult bindingResult) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "signup";
        }

        userService.save(userForm);

        securityService.autoLogin(userForm.getEmail(), userForm.getPasswordConfirm());

        return "redirect:/";
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
