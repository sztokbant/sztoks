package br.net.du.sztoks.controller.viewmodel.validator;

import static br.net.du.sztoks.controller.viewmodel.validator.UserValidationCommons.rejectIfInvalidCurrentPassword;
import static br.net.du.sztoks.controller.viewmodel.validator.UserValidationCommons.rejectIfInvalidNewPassword;

import br.net.du.sztoks.controller.viewmodel.user.PasswordChangeInput;
import br.net.du.sztoks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PasswordChangeInputValidator implements Validator {
    private final UserService userService;

    public static final String EMAIL_CONFIRMATION_FIELD = "emailConfirmation";

    @Autowired
    public PasswordChangeInputValidator(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return PasswordChangeInput.class.equals(aClass);
    }

    @Override
    public void validate(final Object o, final Errors errors) {
        final PasswordChangeInput passwordChangeInput = (PasswordChangeInput) o;

        rejectIfInvalidCurrentPassword(
                errors,
                passwordChangeInput.getEmail(),
                passwordChangeInput.getCurrentPassword(),
                userService);
        rejectIfInvalidNewPassword(
                errors,
                passwordChangeInput.getPassword(),
                passwordChangeInput.getPasswordConfirm());
    }
}
