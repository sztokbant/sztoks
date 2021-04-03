package br.net.du.myequity.controller.viewmodel.validator;

import static br.net.du.myequity.controller.util.ControllerConstants.CURRENCY_UNIT;
import static br.net.du.myequity.controller.util.ControllerConstants.NAME;
import static br.net.du.myequity.controller.util.ControllerConstants.NOT_EMPTY;
import static org.springframework.validation.ValidationUtils.rejectIfEmptyOrWhitespace;

import br.net.du.myequity.controller.viewmodel.AccountViewModelInput;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.TithingAccount;
import br.net.du.myequity.service.AccountService;
import java.util.List;
import org.joda.money.CurrencyUnit;
import org.joda.money.IllegalCurrencyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;

@Component
public class AccountViewModelInputValidator implements SmartValidator {
    public static final String SUBTYPE_NAME_KEY = "subtypeName";

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
        final Snapshot snapshot = getSnapshot(validationHints);

        rejectIfInvalidOrDuplicateNameAndSubType(accountViewModelInput, snapshot, errors);
        rejectIfInvalidCurrencyUnit(accountViewModelInput, errors);
    }

    private Snapshot getSnapshot(Object[] validationHints) {
        if ((validationHints == null)
                || (validationHints.length != 1)
                || !(validationHints[0] instanceof Snapshot)) {
            throw new UnsupportedOperationException();
        }

        return (Snapshot) validationHints[0];
    }

    private void rejectIfInvalidOrDuplicateNameAndSubType(
            AccountViewModelInput accountViewModelInput, Snapshot snapshot, Errors errors) {
        rejectIfEmptyOrWhitespace(errors, NAME, NOT_EMPTY);
        rejectIfEmptyOrWhitespace(errors, SUBTYPE_NAME_KEY, NOT_EMPTY);

        final String inputSubtypeName = accountViewModelInput.getSubtypeName();
        if (TithingAccount.class.getSimpleName().equals(inputSubtypeName)) {
            errors.rejectValue(SUBTYPE_NAME_KEY, "Invalid.value");
        }

        if (errors.hasFieldErrors(SUBTYPE_NAME_KEY) || errors.hasFieldErrors(NAME)) {
            return;
        }

        try {
            final String className = Account.class.getPackage().getName() + "." + inputSubtypeName;
            final Class<? extends Account> inputSubType =
                    Class.forName(className).asSubclass(Account.class);

            final String inputName = accountViewModelInput.getName().trim();

            rejectIfExistingNameAndSubType(inputName, inputSubType, snapshot, errors);
        } catch (final ClassNotFoundException e) {
            errors.rejectValue(SUBTYPE_NAME_KEY, "Invalid.value");
        }
    }

    private void rejectIfExistingNameAndSubType(
            final String inputName,
            final Class<? extends Account> inputSubType,
            final Snapshot snapshot,
            final Errors errors) {

        final List<Account> accounts = accountService.findBySnapshot(snapshot);

        final boolean isDuplicate =
                accounts.stream()
                        .anyMatch(
                                account ->
                                        (inputSubType.isInstance(account))
                                                && account.getName().equals(inputName));
        if (isDuplicate) {
            errors.rejectValue(NAME, "Duplicate.accountForm.name");
        }
    }

    private void rejectIfInvalidCurrencyUnit(
            final AccountViewModelInput accountViewModelInput, final Errors errors) {
        rejectIfEmptyOrWhitespace(errors, CURRENCY_UNIT, NOT_EMPTY);

        if (errors.hasFieldErrors(CURRENCY_UNIT)) {
            return;
        }

        try {
            CurrencyUnit.of(accountViewModelInput.getCurrencyUnit());
        } catch (final NullPointerException | IllegalCurrencyException e) {
            errors.rejectValue(CURRENCY_UNIT, "Invalid.currency");
        }
    }
}
