package br.net.du.sztoks.controller.user;

import static br.net.du.sztoks.controller.util.ControllerConstants.USER_AGENT_REQUEST_HEADER_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.sztoks.controller.util.ControllerUtils.getLoggedUser;
import static br.net.du.sztoks.controller.util.ControllerUtils.prepareTemplate;

import br.net.du.sztoks.controller.interceptor.WebController;
import br.net.du.sztoks.controller.viewmodel.user.EmailUpdateInput;
import br.net.du.sztoks.controller.viewmodel.user.NameChangeInput;
import br.net.du.sztoks.controller.viewmodel.user.UserViewModelOutput;
import br.net.du.sztoks.controller.viewmodel.validator.EmailUpdateInputValidator;
import br.net.du.sztoks.controller.viewmodel.validator.NameChangeInputValidator;
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
    private static final String EMAIL_UPDATE_FORM_MODEL_ATTRIBUTE = "emailUpdateInput";
    private static final String EMAIL_UPDATE_MAPPING = "/settings/email";
    private static final String EMAIL_UPDATE_TEMPLATE = "user/email_update";
    private static final String NAME_CHANGE_FORM_MODEL_ATTRIBUTE = "nameChangeInput";
    private static final String NAME_CHANGE_MAPPING = "/settings/name";
    private static final String NAME_CHANGE_TEMPLATE = "user/name_change";
    private static final String SETTINGS_MAPPING = "/settings";
    private static final String SETTINGS_TEMPLATE = "user/settings";

    @Autowired private UserService userService;

    @Autowired private EmailUpdateInputValidator emailUpdateInputValidator;

    @Autowired private NameChangeInputValidator nameChangeInputValidator;

    @GetMapping(SETTINGS_MAPPING)
    public String showSettingsPage(
            @RequestHeader(value = USER_AGENT_REQUEST_HEADER_KEY, required = false)
                    final String userAgent,
            final Model model) {
        final User user = getLoggedUser(model);
        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));

        return prepareTemplate(userAgent, model, SETTINGS_TEMPLATE);
    }

    @GetMapping(NAME_CHANGE_MAPPING)
    public String showNameChangeForm(
            @RequestHeader(value = USER_AGENT_REQUEST_HEADER_KEY, required = false)
                    final String userAgent,
            final Model model) {
        final User user = getLoggedUser(model);
        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));

        model.addAttribute(
                NAME_CHANGE_FORM_MODEL_ATTRIBUTE,
                NameChangeInput.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .build());
        return prepareTemplate(userAgent, model, NAME_CHANGE_TEMPLATE);
    }

    @GetMapping(EMAIL_UPDATE_MAPPING)
    public String showEmailUpdateForm(
            @RequestHeader(value = USER_AGENT_REQUEST_HEADER_KEY, required = false)
                    final String userAgent,
            final Model model) {
        final User user = getLoggedUser(model);
        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));

        model.addAttribute(EMAIL_UPDATE_FORM_MODEL_ATTRIBUTE, new EmailUpdateInput());
        return prepareTemplate(userAgent, model, EMAIL_UPDATE_TEMPLATE);
    }

    @PostMapping(NAME_CHANGE_MAPPING)
    public String changeName(
            @RequestHeader(value = USER_AGENT_REQUEST_HEADER_KEY, required = false)
                    final String userAgent,
            final Model model,
            @ModelAttribute(NAME_CHANGE_FORM_MODEL_ATTRIBUTE) final NameChangeInput nameChangeInput,
            final BindingResult bindingResult) {
        nameChangeInputValidator.validate(nameChangeInput, bindingResult);

        final User user = getLoggedUser(model);

        if (bindingResult.hasErrors()) {
            model.addAttribute(USER_KEY, UserViewModelOutput.of(user));

            model.addAttribute(NAME_CHANGE_FORM_MODEL_ATTRIBUTE, nameChangeInput);
            return prepareTemplate(userAgent, model, NAME_CHANGE_TEMPLATE);
        }

        final String newFirstName = nameChangeInput.getFirstName().trim();
        final String newLastName = nameChangeInput.getLastName().trim();

        if (!user.getFirstName().equals(newFirstName) || !user.getLastName().equals(newLastName)) {
            user.setFirstName(newFirstName);
            user.setLastName(newLastName);
            userService.save(user);

            model.addAttribute("message", "You have successfully changed your name.");
        }

        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));

        return prepareTemplate(userAgent, model, SETTINGS_TEMPLATE);
    }

    @PostMapping(EMAIL_UPDATE_MAPPING)
    public String updateEmail(
            @RequestHeader(value = USER_AGENT_REQUEST_HEADER_KEY, required = false)
                    final String userAgent,
            final Model model,
            @ModelAttribute(EMAIL_UPDATE_FORM_MODEL_ATTRIBUTE)
                    final EmailUpdateInput emailUpdateInput,
            final BindingResult bindingResult) {
        emailUpdateInputValidator.validate(emailUpdateInput, bindingResult);

        final User user = getLoggedUser(model);
        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));

        if (bindingResult.hasErrors()) {
            model.addAttribute(EMAIL_UPDATE_FORM_MODEL_ATTRIBUTE, emailUpdateInput);
            return prepareTemplate(userAgent, model, EMAIL_UPDATE_TEMPLATE);
        }

        user.setEmail(emailUpdateInput.getEmail().trim());
        userService.save(user);

        model.addAttribute(
                "message", "You have successfully updated your e-mail, please log in again.");

        return prepareTemplate(userAgent, model, "login");
    }
}
