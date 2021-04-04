package br.net.du.myequity.controller;

import static br.net.du.myequity.controller.util.ControllerConstants.REDIRECT_TO_HOME;

import br.net.du.myequity.controller.viewmodel.UserViewModelInput;
import br.net.du.myequity.controller.viewmodel.validator.UserViewModelInputValidator;
import br.net.du.myequity.service.SecurityService;
import br.net.du.myequity.service.UserService;
import java.math.BigDecimal;
import org.joda.money.CurrencyUnit;
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

    @Autowired private UserViewModelInputValidator validator;

    @GetMapping(SIGNUP_MAPPING)
    public String signup(final Model model) {
        model.addAttribute(USER_FORM, new UserViewModelInput());

        return SIGNUP_TEMPLATE;
    }

    @PostMapping(SIGNUP_MAPPING)
    public String signup(
            @ModelAttribute(USER_FORM) final UserViewModelInput userViewModelInput,
            final BindingResult bindingResult) {
        validator.validate(userViewModelInput, bindingResult);

        if (bindingResult.hasErrors()) {
            return SIGNUP_TEMPLATE;
        }

        final String email = userViewModelInput.getEmail().trim();
        final String firstName = userViewModelInput.getFirstName().trim();
        final String lastName = userViewModelInput.getLastName().trim();
        final CurrencyUnit baseCurrencyUnit = CurrencyUnit.of(userViewModelInput.getCurrencyUnit());
        final BigDecimal defaultTithingPercentage =
                new BigDecimal(userViewModelInput.getTithingPercentage());
        final String password = userViewModelInput.getPassword();

        userService.signUp(
                email, firstName, lastName, baseCurrencyUnit, defaultTithingPercentage, password);
        securityService.autoLogin(email, password);

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
