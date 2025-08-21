package br.net.du.sztoks.controller.viewmodel.validator;

import static br.net.du.sztoks.controller.viewmodel.validator.UserValidationCommons.EMAIL_FIELD;
import static br.net.du.sztoks.controller.viewmodel.validator.UserValidationCommons.rejectIfInvalidCurrentPassword;

import br.net.du.sztoks.controller.viewmodel.user.EmailUpdateInput;
import br.net.du.sztoks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class EmailUpdateInputValidator implements Validator {
    private final UserService userService;

    public static final String EMAIL_CONFIRMATION_FIELD = "emailConfirmation";

    @Autowired
    public EmailUpdateInputValidator(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return EmailUpdateInput.class.equals(aClass);
    }

    @Override
    public void validate(final Object o, final Errors errors) {
        final EmailUpdateInput emailUpdateInput = (EmailUpdateInput) o;

        rejectIfNoop(errors, emailUpdateInput);

        final String email = emailUpdateInput.getEmail().trim();
        UserValidationCommons.rejectIfInvalidOrExistingEmail(errors, email, userService);

        rejectIfEmailsDontMatch(errors, emailUpdateInput);

        rejectIfInvalidCurrentPassword(
                errors,
                emailUpdateInput.getCurrentEmail(),
                emailUpdateInput.getCurrentPassword(),
                userService);
    }

    private void rejectIfNoop(final Errors errors, final EmailUpdateInput emailUpdateInput) {
        final String email = emailUpdateInput.getEmail().trim();
        final String currentEmail = emailUpdateInput.getCurrentEmail().trim();
        if (email.equals(currentEmail)) {
            errors.rejectValue(EMAIL_FIELD, "Invalid.userForm.emailNotChanged");
        }
    }

    private void rejectIfEmailsDontMatch(
            final Errors errors, final EmailUpdateInput emailUpdateInput) {
        final String email = emailUpdateInput.getEmail().trim();
        final String emailConfirmation = emailUpdateInput.getEmailConfirmation().trim();
        if (!emailConfirmation.equals(email)) {
            errors.rejectValue(
                    EMAIL_CONFIRMATION_FIELD, "Invalid.userForm.emailConfirmationMismatch");
        }
    }
}
