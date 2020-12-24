package br.net.du.myequity.validator;

import br.net.du.myequity.controller.viewmodel.UserViewModelInput;
import br.net.du.myequity.service.UserService;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserViewModelInputValidator implements Validator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^(.+)@(.+)$");

    private final UserService userService;

    @Autowired
    public UserViewModelInputValidator(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UserViewModelInput.class.equals(aClass);
    }

    @Override
    public void validate(final Object o, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordConfirm", "NotEmpty");

        final UserViewModelInput userViewModelInput = (UserViewModelInput) o;
        rejectIfInvalidOrExistingEmail(errors, userViewModelInput);
        rejectIfInvalidPassword(errors, userViewModelInput);
    }

    private void rejectIfInvalidOrExistingEmail(
            final Errors errors, final UserViewModelInput userViewModelInput) {
        if (StringUtils.isEmpty(userViewModelInput.getEmail())
                || !EMAIL_PATTERN.matcher(userViewModelInput.getEmail()).matches()) {
            errors.rejectValue("email", "Invalid.userForm.email");
        } else if (userService.findByEmail(userViewModelInput.getEmail()) != null) {
            errors.rejectValue("email", "Duplicate.userForm.email");
        }
    }

    private void rejectIfInvalidPassword(
            final Errors errors, final UserViewModelInput userViewModelInput) {
        if (StringUtils.isEmpty(userViewModelInput.getPassword())
                || userViewModelInput.getPassword().length() < 8
                || userViewModelInput.getPassword().length() > 32) {
            errors.rejectValue("password", "Size.userForm.password");
        } else if (!userViewModelInput
                .getPasswordConfirm()
                .equals(userViewModelInput.getPassword())) {
            errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
        }
    }
}
