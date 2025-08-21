package br.net.du.sztoks.controller.viewmodel.validator;

import static br.net.du.sztoks.controller.viewmodel.validator.UserValidationCommons.EMAIL_FIELD;
import static br.net.du.sztoks.controller.viewmodel.validator.UserValidationCommons.PASSWORD_FIELD;

import br.net.du.sztoks.controller.viewmodel.user.EmailUpdateViewModelInput;
import br.net.du.sztoks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class EmailUpdateViewModelInputValidator implements Validator {
    private final UserService userService;

    public static final String EMAIL_CONFIRMATION_FIELD = "emailConfirmation";

    @Autowired
    public EmailUpdateViewModelInputValidator(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return EmailUpdateViewModelInput.class.equals(aClass);
    }

    @Override
    public void validate(final Object o, final Errors errors) {
        final EmailUpdateViewModelInput emailUpdateViewModelInput = (EmailUpdateViewModelInput) o;

        rejectIfNoop(errors, emailUpdateViewModelInput);

        final String email = emailUpdateViewModelInput.getEmail().trim();
        UserValidationCommons.rejectIfInvalidOrExistingEmail(errors, email, userService);

        rejectIfEmailsDontMatch(errors, emailUpdateViewModelInput);

        rejectIfInvalidPassword(errors, emailUpdateViewModelInput);
    }

    private void rejectIfNoop(
            final Errors errors, final EmailUpdateViewModelInput emailUpdateViewModelInput) {
        final String email = emailUpdateViewModelInput.getEmail().trim();
        final String currentEmail = emailUpdateViewModelInput.getCurrentEmail().trim();
        if (email.equals(currentEmail)) {
            errors.rejectValue(EMAIL_FIELD, "Invalid.userForm.emailNotChanged");
        }
    }

    private void rejectIfEmailsDontMatch(
            final Errors errors, final EmailUpdateViewModelInput emailUpdateViewModelInput) {
        final String email = emailUpdateViewModelInput.getEmail().trim();
        final String emailConfirmation = emailUpdateViewModelInput.getEmailConfirmation().trim();
        if (!emailConfirmation.equals(email)) {
            errors.rejectValue(
                    EMAIL_CONFIRMATION_FIELD, "Invalid.userForm.emailConfirmationMismatch");
        }
    }

    private void rejectIfInvalidPassword(
            Errors errors, EmailUpdateViewModelInput emailUpdateViewModelInput) {
        if (!userService.validateLogin(
                emailUpdateViewModelInput.getCurrentEmail().trim(),
                emailUpdateViewModelInput.getPassword())) {
            errors.rejectValue(PASSWORD_FIELD, "Invalid.userForm.password");
        }
    }
}
