package br.net.du.sztoks.controller.viewmodel.validator;

import br.net.du.sztoks.controller.viewmodel.user.DeleteUserInput;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class DeleteUserInputValidator implements Validator {
    private static final String DELETE_USER_CONFIRMATION = "PERMANENTLY DELETE";
    private static final String DELETE_USER_CONFIRMATION_FIELD = "confirmation";

    public DeleteUserInputValidator() {}

    @Override
    public boolean supports(Class<?> aClass) {
        return DeleteUserInput.class.equals(aClass);
    }

    @Override
    public void validate(final Object o, final Errors errors) {
        final DeleteUserInput deleteUserInput = (DeleteUserInput) o;

        rejectIfInvalidConfirmation(errors, deleteUserInput);
    }

    private void rejectIfInvalidConfirmation(
            final Errors errors, final DeleteUserInput deleteUserInput) {
        if (!deleteUserInput.getConfirmation().trim().equalsIgnoreCase(DELETE_USER_CONFIRMATION)) {
            errors.rejectValue(DELETE_USER_CONFIRMATION_FIELD, "Invalid.deleteUser.confirmation");
        }
    }
}
