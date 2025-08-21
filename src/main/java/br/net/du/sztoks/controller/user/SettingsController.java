package br.net.du.sztoks.controller.user;

import static br.net.du.sztoks.controller.util.ControllerConstants.USER_AGENT_REQUEST_HEADER_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.sztoks.controller.util.ControllerUtils.getLoggedUser;
import static br.net.du.sztoks.controller.util.ControllerUtils.prepareTemplate;

import br.net.du.sztoks.controller.interceptor.WebController;
import br.net.du.sztoks.controller.viewmodel.user.EmailUpdateInput;
import br.net.du.sztoks.controller.viewmodel.user.UserViewModelOutput;
import br.net.du.sztoks.controller.viewmodel.validator.EmailUpdateInputValidator;
import br.net.du.sztoks.model.User;
import br.net.du.sztoks.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Slf4j
@WebController
public class SettingsController {
    private static final String FORM_MODEL_ATTRIBUTE = "emailUpdateInput";
    private static final String EMAIL_UPDATE_MAPPING = "/settings/email";
    private static final String EMAIL_UPDATE_TEMPLATE = "user/email_update";
    private static final String SETTINGS_MAPPING = "/settings";
    private static final String SETTINGS_TEMPLATE = "user/settings";

    @Autowired private UserService userService;

    @Autowired private EmailUpdateInputValidator validator;

    @GetMapping(SETTINGS_MAPPING)
    public String showSettingsPage(
            @RequestHeader(value = USER_AGENT_REQUEST_HEADER_KEY, required = false)
                    final String userAgent,
            final Model model) {
        final User user = getLoggedUser(model);
        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));

        return prepareTemplate(userAgent, model, SETTINGS_TEMPLATE);
    }

    @GetMapping(EMAIL_UPDATE_MAPPING)
    public String showEmailUpdateForm(
            @RequestHeader(value = USER_AGENT_REQUEST_HEADER_KEY, required = false)
                    final String userAgent,
            final Model model) {
        final User user = getLoggedUser(model);
        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));

        model.addAttribute(FORM_MODEL_ATTRIBUTE, new EmailUpdateInput());
        return prepareTemplate(userAgent, model, EMAIL_UPDATE_TEMPLATE);
    }

    @PostMapping(EMAIL_UPDATE_MAPPING)
    public String updateEmail(
            @RequestHeader(value = USER_AGENT_REQUEST_HEADER_KEY, required = false)
                    final String userAgent,
            final Model model,
            @ModelAttribute(FORM_MODEL_ATTRIBUTE) final EmailUpdateInput emailUpdateInput,
            final BindingResult bindingResult) {
        validator.validate(emailUpdateInput, bindingResult);

        final User user = getLoggedUser(model);
        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));

        if (bindingResult.hasErrors()) {
            model.addAttribute(FORM_MODEL_ATTRIBUTE, emailUpdateInput);
            return prepareTemplate(userAgent, model, EMAIL_UPDATE_TEMPLATE);
        }

        user.setEmail(emailUpdateInput.getEmail().trim());
        userService.save(user);

        model.addAttribute(
                "message", "You have successfully changed your e-mail, please log in again.");

        return prepareTemplate(userAgent, model, "login");
    }
}
