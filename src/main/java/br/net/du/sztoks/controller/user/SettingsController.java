package br.net.du.sztoks.controller.user;

import static br.net.du.sztoks.controller.util.ControllerConstants.USER_AGENT_REQUEST_HEADER_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.sztoks.controller.util.ControllerUtils.getLoggedUser;
import static br.net.du.sztoks.controller.util.ControllerUtils.prepareTemplate;

import br.net.du.sztoks.controller.interceptor.WebController;
import br.net.du.sztoks.controller.viewmodel.user.EmailUpdateViewModelInput;
import br.net.du.sztoks.controller.viewmodel.user.UserViewModelOutput;
import br.net.du.sztoks.controller.viewmodel.validator.EmailUpdateViewModelInputValidator;
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
    private static final String FORM_MODEL_ATTRIBUTE = "emailUpdateViewModelInput";
    private static final String UPDATE_EMAIL_MAPPING = "/settings/email";
    private static final String UPDATE_EMAIL_TEMPLATE = "user/email_update";

    @Autowired private UserService userService;

    @Autowired private EmailUpdateViewModelInputValidator validator;

    @GetMapping(UPDATE_EMAIL_MAPPING)
    public String emailUpdateGet(
            @RequestHeader(value = USER_AGENT_REQUEST_HEADER_KEY, required = false)
                    final String userAgent,
            final Model model) {
        final User user = getLoggedUser(model);
        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));

        model.addAttribute(FORM_MODEL_ATTRIBUTE, new EmailUpdateViewModelInput());
        return prepareTemplate(userAgent, model, UPDATE_EMAIL_TEMPLATE);
    }

    @PostMapping(UPDATE_EMAIL_MAPPING)
    public String emailUpdatePost(
            @RequestHeader(value = USER_AGENT_REQUEST_HEADER_KEY, required = false)
                    final String userAgent,
            final Model model,
            @ModelAttribute(FORM_MODEL_ATTRIBUTE)
                    final EmailUpdateViewModelInput emailUpdateViewModelInput,
            final BindingResult bindingResult) {
        validator.validate(emailUpdateViewModelInput, bindingResult);

        final User user = getLoggedUser(model);
        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));

        if (bindingResult.hasErrors()) {
            model.addAttribute(FORM_MODEL_ATTRIBUTE, emailUpdateViewModelInput);
            return prepareTemplate(userAgent, model, UPDATE_EMAIL_TEMPLATE);
        }

        user.setEmail(emailUpdateViewModelInput.getEmail().trim());
        userService.save(user);

        model.addAttribute(
                "message", "You have successfully changed your e-mail, please log in again.");

        return prepareTemplate(userAgent, model, "login");
    }
}
