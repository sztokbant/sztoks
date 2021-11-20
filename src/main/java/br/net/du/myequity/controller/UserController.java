package br.net.du.myequity.controller;

import static br.net.du.myequity.controller.util.ControllerConstants.CURRENCIES;
import static br.net.du.myequity.controller.util.ControllerConstants.DEFAULT_CURRENCY_UNIT;
import static br.net.du.myequity.controller.util.ControllerConstants.REDIRECT_TO_HOME;
import static br.net.du.myequity.controller.util.ControllerConstants.SELECTED_CURRENCY;
import static br.net.du.myequity.controller.util.ControllerUtils.prepareTemplate;

import br.net.du.myequity.controller.interceptor.WebController;
import br.net.du.myequity.controller.viewmodel.UserViewModelInput;
import br.net.du.myequity.controller.viewmodel.validator.UserViewModelInputValidator;
import br.net.du.myequity.service.SecurityService;
import br.net.du.myequity.service.UserService;
import java.math.BigDecimal;
import org.joda.money.CurrencyUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@WebController
public class UserController {
    private static final String SIGNUP_MAPPING = "/signup";
    private static final String SIGNUP_TEMPLATE = "signup";
    private static final String USER_FORM = "userForm";

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired private UserService userService;

    @Autowired private SecurityService securityService;

    @Autowired private UserViewModelInputValidator validator;

    @GetMapping(SIGNUP_MAPPING)
    public String signup(final Model model) {
        model.addAttribute(USER_FORM, new UserViewModelInput());

        model.addAttribute(CURRENCIES, CurrencyUnit.registeredCurrencies());
        model.addAttribute(SELECTED_CURRENCY, DEFAULT_CURRENCY_UNIT.getCode());

        return SIGNUP_TEMPLATE;
    }

    @PostMapping(SIGNUP_MAPPING)
    @Transactional
    public String signup(
            final Model model,
            @ModelAttribute(USER_FORM) final UserViewModelInput userViewModelInput,
            final BindingResult bindingResult) {
        validator.validate(userViewModelInput, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute(CURRENCIES, CurrencyUnit.registeredCurrencies());

            try {
                final CurrencyUnit selectedCurrency =
                        CurrencyUnit.of(userViewModelInput.getCurrencyUnit());
                model.addAttribute(SELECTED_CURRENCY, selectedCurrency.getCode());
            } catch (final Exception e) {
                model.addAttribute(SELECTED_CURRENCY, DEFAULT_CURRENCY_UNIT.getCode());
            }

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
    public String login(
            final Model model, final Device device, final String error, final String logout) {

        LOGGER.info("*** INSIDE login(): " + device.toString() + " ***");

        if (error != null) {
            model.addAttribute("error", "Invalid E-mail or Password.");
        }

        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }

        return prepareTemplate(model, device, "login");
    }
}
