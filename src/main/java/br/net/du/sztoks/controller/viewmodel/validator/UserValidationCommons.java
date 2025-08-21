package br.net.du.sztoks.controller.viewmodel.validator;

import br.net.du.sztoks.service.UserService;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;

public class UserValidationCommons {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^(.+)@(.+)$");

    public static final String EMAIL_FIELD = "email";
    public static final String FIRST_NAME_FIELD = "firstName";
    public static final String LAST_NAME_FIELD = "lastName";
    public static final String PASSWORD_CONFIRM_FIELD = "passwordConfirm";
    public static final String PASSWORD_FIELD = "password";

    public static void rejectIfInvalidOrExistingEmail(
            final Errors errors, final String email, final UserService userService) {
        if (StringUtils.isEmpty(email) || !EMAIL_PATTERN.matcher(email).matches()) {
            errors.rejectValue(EMAIL_FIELD, "Invalid.userForm.email");
        } else if (userService.findByEmail(email) != null) {
            errors.rejectValue(EMAIL_FIELD, "Duplicate.userForm.email");
        }
    }
}
