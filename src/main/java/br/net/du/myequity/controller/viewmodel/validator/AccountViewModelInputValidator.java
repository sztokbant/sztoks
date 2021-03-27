package br.net.du.myequity.controller.viewmodel.validator;

import static br.net.du.myequity.controller.util.ControllerConstants.CURRENCY_UNIT;
import static br.net.du.myequity.controller.util.ControllerConstants.NAME;
import static br.net.du.myequity.controller.util.ControllerConstants.NOT_EMPTY;

import br.net.du.myequity.controller.viewmodel.AccountViewModelInput;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.service.AccountService;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.joda.money.CurrencyUnit;
import org.joda.money.IllegalCurrencyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;

@Component
public class AccountViewModelInputValidator implements SmartValidator {
    private final AccountService accountService;

    @Autowired
    public AccountViewModelInputValidator(final AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return AccountViewModelInput.class.equals(aClass);
    }

    @Override
    public void validate(final Object o, final Errors errors) {
        validate(o, errors, new Object[0]);
    }

    @Override
    public void validate(final Object o, final Errors errors, final Object... validationHints) {
        final AccountViewModelInput accountViewModelInput = (AccountViewModelInput) o;

        rejectIfInvalidAccountType(accountViewModelInput, errors);
        rejectIfInvalidCurrencyUnit(accountViewModelInput, errors);
        rejectIfNoUserOrExistingName(accountViewModelInput, errors, validationHints);
    }

    private void rejectIfInvalidAccountType(
            final AccountViewModelInput accountViewModelInput, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "typeName", NOT_EMPTY);
    }

    private void rejectIfInvalidCurrencyUnit(
            final AccountViewModelInput accountViewModelInput, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, CURRENCY_UNIT, NOT_EMPTY);

        if (StringUtils.isNotBlank(accountViewModelInput.getCurrencyUnit())) {
            try {
                CurrencyUnit.of(accountViewModelInput.getCurrencyUnit());
            } catch (final NullPointerException | IllegalCurrencyException e) {
                errors.rejectValue(CURRENCY_UNIT, "Invalid.currency");
            }
        }
    }

    private void rejectIfNoUserOrExistingName(
            final AccountViewModelInput accountViewModelInput,
            final Errors errors,
            final Object[] validationHints) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, NAME, NOT_EMPTY);

        if (StringUtils.isNotBlank(accountViewModelInput.getName())) {
            if ((validationHints == null)
                    || (validationHints.length != 1)
                    || !(validationHints[0] instanceof User)) {
                throw new UnsupportedOperationException();
            } else {
                final List<Account> accounts = accountService.findByUser((User) validationHints[0]);
                final boolean isDuplicateName =
                        accounts.stream()
                                .anyMatch(
                                        a ->
                                                a.getName()
                                                        .equals(
                                                                accountViewModelInput
                                                                        .getName()
                                                                        .trim()));
                if (isDuplicateName) {
                    errors.rejectValue(NAME, "Duplicate.accountForm.name");
                }
            }
        }
    }
}
