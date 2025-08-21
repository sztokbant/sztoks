package br.net.du.sztoks.controller.viewmodel.validator;

import static br.net.du.sztoks.controller.viewmodel.validator.UserValidationCommons.FIRST_NAME_FIELD;
import static br.net.du.sztoks.controller.viewmodel.validator.UserValidationCommons.LAST_NAME_FIELD;
import static br.net.du.sztoks.controller.viewmodel.validator.ValidationCommons.NOT_EMPTY_ERRORCODE;
import static org.springframework.validation.ValidationUtils.rejectIfEmptyOrWhitespace;

import br.net.du.sztoks.controller.viewmodel.user.NameChangeInput;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class NameChangeInputValidator implements Validator {
    public NameChangeInputValidator() {}

    @Override
    public boolean supports(Class<?> aClass) {
        return NameChangeInput.class.equals(aClass);
    }

    @Override
    public void validate(final Object o, final Errors errors) {
        rejectIfEmptyOrWhitespace(errors, FIRST_NAME_FIELD, NOT_EMPTY_ERRORCODE);
        rejectIfEmptyOrWhitespace(errors, LAST_NAME_FIELD, NOT_EMPTY_ERRORCODE);
    }
}
