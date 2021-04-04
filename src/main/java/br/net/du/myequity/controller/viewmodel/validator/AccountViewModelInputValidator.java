package br.net.du.myequity.controller.viewmodel.validator;

import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.CURRENCY_UNIT_FIELD;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.NAME_FIELD;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.NOT_EMPTY_ERRORCODE;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.SUBTYPE_NAME_FIELD;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.getSnapshot;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.rejectIfInvalidCurrencyUnit;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.rejectIfUnsupportedCurrencyUnit;
import static org.springframework.validation.ValidationUtils.rejectIfEmptyOrWhitespace;

import br.net.du.myequity.controller.viewmodel.AccountViewModelInput;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.TithingAccount;
import br.net.du.myequity.service.AccountService;
import java.util.List;
import org.joda.money.CurrencyUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;

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
        final Snapshot snapshot = getSnapshot(validationHints);

        rejectIfInvalidOrDuplicateNameAndSubType(accountViewModelInput, snapshot, errors);
        rejectIfInvalidCurrencyUnit(accountViewModelInput.getCurrencyUnit(), errors);
        if (!errors.hasFieldErrors(CURRENCY_UNIT_FIELD)) {
            rejectIfUnsupportedCurrencyUnit(
                    snapshot, CurrencyUnit.of(accountViewModelInput.getCurrencyUnit()), errors);
        }
    }

    private void rejectIfInvalidOrDuplicateNameAndSubType(
            AccountViewModelInput accountViewModelInput, Snapshot snapshot, Errors errors) {
        rejectIfEmptyOrWhitespace(errors, NAME_FIELD, NOT_EMPTY_ERRORCODE);
        rejectIfEmptyOrWhitespace(errors, SUBTYPE_NAME_FIELD, NOT_EMPTY_ERRORCODE);

        final String inputSubtypeName = accountViewModelInput.getSubtypeName();
        if (TithingAccount.class.getSimpleName().equals(inputSubtypeName)) {
            errors.rejectValue(SUBTYPE_NAME_FIELD, "Invalid.value");
        }

        if (errors.hasFieldErrors(SUBTYPE_NAME_FIELD) || errors.hasFieldErrors(NAME_FIELD)) {
            return;
        }

        try {
            final String className = Account.class.getPackage().getName() + "." + inputSubtypeName;
            final Class<? extends Account> inputSubType =
                    Class.forName(className).asSubclass(Account.class);

            final String inputName = accountViewModelInput.getName().trim();

            rejectIfExistingNameAndSubType(inputName, inputSubType, snapshot, errors);
        } catch (final ClassNotFoundException e) {
            errors.rejectValue(SUBTYPE_NAME_FIELD, "Invalid.value");
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
            errors.rejectValue(NAME_FIELD, "Duplicate.accountForm.name");
        }
    }
}
