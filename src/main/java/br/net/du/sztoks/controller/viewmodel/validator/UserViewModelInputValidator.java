package br.net.du.sztoks.controller.viewmodel.validator;

import static br.net.du.sztoks.controller.viewmodel.validator.SnapshotValidationCommons.rejectIfInvalidCurrencyUnit;
import static br.net.du.sztoks.controller.viewmodel.validator.SnapshotValidationCommons.rejectIfInvalidTithingPercentage;
import static br.net.du.sztoks.controller.viewmodel.validator.UserValidationCommons.FIRST_NAME_FIELD;
import static br.net.du.sztoks.controller.viewmodel.validator.UserValidationCommons.LAST_NAME_FIELD;
import static br.net.du.sztoks.controller.viewmodel.validator.UserValidationCommons.PASSWORD_CONFIRM_FIELD;
import static br.net.du.sztoks.controller.viewmodel.validator.UserValidationCommons.rejectIfInvalidNewPassword;
import static br.net.du.sztoks.controller.viewmodel.validator.ValidationCommons.NOT_EMPTY_ERRORCODE;
import static org.springframework.validation.ValidationUtils.rejectIfEmptyOrWhitespace;

import br.net.du.sztoks.controller.viewmodel.user.UserViewModelInput;
import br.net.du.sztoks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserViewModelInputValidator implements Validator {
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

        rejectIfEmptyOrWhitespace(errors, FIRST_NAME_FIELD, NOT_EMPTY_ERRORCODE);
        rejectIfEmptyOrWhitespace(errors, LAST_NAME_FIELD, NOT_EMPTY_ERRORCODE);
        rejectIfInvalidCurrencyUnit(userViewModelInput.getCurrencyUnit(), errors);
        rejectIfInvalidTithingPercentage(userViewModelInput.getTithingPercentage(), errors);
        rejectIfEmptyOrWhitespace(errors, PASSWORD_CONFIRM_FIELD, NOT_EMPTY_ERRORCODE);

        final String email = userViewModelInput.getEmail();
        UserValidationCommons.rejectIfInvalidOrExistingEmail(errors, email, userService);
        rejectIfInvalidNewPassword(
                errors, userViewModelInput.getPassword(), userViewModelInput.getPasswordConfirm());
    }
}
