package br.net.du.myequity.controller.viewmodel.validator;

import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.EMAIL_FIELD;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.FIRST_NAME_FIELD;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.LAST_NAME_FIELD;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.NOT_EMPTY_ERRORCODE;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.PASSWORD_CONFIRM_FIELD;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.PASSWORD_FIELD;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.rejectIfInvalidCurrencyUnit;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.rejectIfInvalidTithingPercentage;
import static org.springframework.validation.ValidationUtils.rejectIfEmptyOrWhitespace;

import br.net.du.myequity.controller.viewmodel.UserViewModelInput;
import br.net.du.myequity.service.UserService;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
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
        final UserViewModelInput userViewModelInput = (UserViewModelInput) o;

        rejectIfEmptyOrWhitespace(errors, EMAIL_FIELD, NOT_EMPTY_ERRORCODE);
        rejectIfEmptyOrWhitespace(errors, FIRST_NAME_FIELD, NOT_EMPTY_ERRORCODE);
        rejectIfEmptyOrWhitespace(errors, LAST_NAME_FIELD, NOT_EMPTY_ERRORCODE);
        rejectIfInvalidCurrencyUnit(userViewModelInput.getCurrencyUnit(), errors);
        rejectIfInvalidTithingPercentage(userViewModelInput.getTithingPercentage(), errors);
        rejectIfEmptyOrWhitespace(errors, PASSWORD_FIELD, NOT_EMPTY_ERRORCODE);
        rejectIfEmptyOrWhitespace(errors, PASSWORD_CONFIRM_FIELD, NOT_EMPTY_ERRORCODE);

        rejectIfInvalidOrExistingEmail(errors, userViewModelInput);
        rejectIfInvalidPassword(errors, userViewModelInput);
    }

    private void rejectIfInvalidOrExistingEmail(
            final Errors errors, final UserViewModelInput userViewModelInput) {
        if (StringUtils.isEmpty(userViewModelInput.getEmail())
                || !EMAIL_PATTERN.matcher(userViewModelInput.getEmail()).matches()) {
            errors.rejectValue(EMAIL_FIELD, "Invalid.userForm.email");
        } else if (userService.findByEmail(userViewModelInput.getEmail()) != null) {
            errors.rejectValue(EMAIL_FIELD, "Duplicate.userForm.email");
        }
    }

    private void rejectIfInvalidPassword(
            final Errors errors, final UserViewModelInput userViewModelInput) {
        if (StringUtils.isEmpty(userViewModelInput.getPassword())
                || (userViewModelInput.getPassword().length() < 8)
                || (userViewModelInput.getPassword().length() > 32)) {
            errors.rejectValue(PASSWORD_FIELD, "Size.userForm.password");
        } else if (!userViewModelInput
                .getPasswordConfirm()
                .equals(userViewModelInput.getPassword())) {
            errors.rejectValue(PASSWORD_CONFIRM_FIELD, "Diff.userForm.passwordConfirm");
        }
    }
}
