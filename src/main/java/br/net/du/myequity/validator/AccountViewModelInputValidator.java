package br.net.du.myequity.validator;

import br.net.du.myequity.controller.viewmodel.AccountViewModelInput;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.persistence.AccountRepository;
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
    private final AccountRepository accountRepository;

    @Autowired
    public AccountViewModelInputValidator(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return AccountViewModelInput.class.equals(aClass);
    }

    @Override
    public void validate(final Object o, final Errors errors) {
        validate(o, errors, null);
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
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "typeName", "NotEmpty");
    }

    private void rejectIfInvalidCurrencyUnit(
            final AccountViewModelInput accountViewModelInput, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currencyUnit", "NotEmpty");

        if (StringUtils.isNotBlank(accountViewModelInput.getCurrencyUnit())) {
            try {
                CurrencyUnit.of(accountViewModelInput.getCurrencyUnit());
            } catch (final NullPointerException | IllegalCurrencyException e) {
                errors.rejectValue("currencyUnit", "Invalid.accountForm.currency");
            }
        }
    }

    private void rejectIfNoUserOrExistingName(
            final AccountViewModelInput accountViewModelInput,
            final Errors errors,
            final Object[] validationHints) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty");

        if (StringUtils.isNotBlank(accountViewModelInput.getName())) {
            if (validationHints == null
                    || validationHints.length != 1
                    || !(validationHints[0] instanceof User)) {
                throw new UnsupportedOperationException();
            } else {
                final List<Account> accounts =
                        accountRepository.findByUser((User) validationHints[0]);
                final boolean isDuplicateName =
                        accounts.stream()
                                        .filter(
                                                a ->
                                                        a.getName()
                                                                .equals(
                                                                        accountViewModelInput
                                                                                .getName()
                                                                                .trim()))
                                        .count()
                                > 0;
                if (isDuplicateName) {
                    errors.rejectValue("name", "Duplicate.accountForm.name");
                }
            }
        }
    }
}
