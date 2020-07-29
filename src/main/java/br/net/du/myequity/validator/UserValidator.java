package br.net.du.myequity.validator;

import br.net.du.myequity.model.User;
import br.net.du.myequity.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

@Component
public class UserValidator implements Validator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^(.+)@(.+)$");

    private UserService userService;

    @Autowired
    public UserValidator(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(final Object o, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");

        final User user = (User) o;
        rejectIfInvalidOrExistingEmail(errors, user);
        rejectIfInvalidPassword(errors, user);
    }

    private void rejectIfInvalidOrExistingEmail(final Errors errors, final User user) {
        if (StringUtils.isEmpty(user.getEmail()) || !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            errors.rejectValue("email", "Invalid.userForm.email");
        } else if (userService.findByEmail(user.getEmail()) != null) {
            errors.rejectValue("email", "Duplicate.userForm.email");
        }
    }

    private void rejectIfInvalidPassword(final Errors errors, final User user) {
        if (StringUtils.isEmpty(user.getPassword()) || user.getPassword().length() < 8 || user.getPassword()
                                                                                              .length() > 32) {
            errors.rejectValue("password", "Size.userForm.password");
        } else if (!user.getPasswordConfirm().equals(user.getPassword())) {
            errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
        }
    }
}
