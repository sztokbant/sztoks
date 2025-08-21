package br.net.du.sztoks.controller.viewmodel.validator;

import static br.net.du.sztoks.controller.viewmodel.validator.SnapshotValidationCommons.AMOUNT_FIELD;
import static br.net.du.sztoks.controller.viewmodel.validator.SnapshotValidationCommons.CATEGORY_FIELD;
import static br.net.du.sztoks.controller.viewmodel.validator.SnapshotValidationCommons.CURRENCY_UNIT_FIELD;
import static br.net.du.sztoks.controller.viewmodel.validator.SnapshotValidationCommons.DATE_FIELD;
import static br.net.du.sztoks.controller.viewmodel.validator.SnapshotValidationCommons.DESCRIPTION_FIELD;
import static br.net.du.sztoks.controller.viewmodel.validator.SnapshotValidationCommons.IS_TAX_DEDUCTIBLE_FIELD;
import static br.net.du.sztoks.controller.viewmodel.validator.SnapshotValidationCommons.RECURRENCE_POLICY_FIELD;
import static br.net.du.sztoks.controller.viewmodel.validator.SnapshotValidationCommons.getSnapshot;
import static br.net.du.sztoks.controller.viewmodel.validator.SnapshotValidationCommons.rejectIfInvalidCurrencyUnit;
import static br.net.du.sztoks.controller.viewmodel.validator.SnapshotValidationCommons.rejectIfInvalidTithingPercentage;
import static br.net.du.sztoks.controller.viewmodel.validator.SnapshotValidationCommons.rejectIfUnsupportedCurrencyUnit;
import static br.net.du.sztoks.controller.viewmodel.validator.ValidationCommons.NOT_EMPTY_ERRORCODE;
import static org.springframework.validation.ValidationUtils.rejectIfEmptyOrWhitespace;

import br.net.du.sztoks.controller.viewmodel.transaction.TransactionViewModelInput;
import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.transaction.DonationCategory;
import br.net.du.sztoks.model.transaction.IncomeCategory;
import br.net.du.sztoks.model.transaction.InvestmentCategory;
import br.net.du.sztoks.model.transaction.RecurrencePolicy;
import br.net.du.sztoks.model.transaction.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.joda.money.CurrencyUnit;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;

@Component
@NoArgsConstructor
public class TransactionViewModelInputValidator implements SmartValidator {

    @Override
    public boolean supports(Class<?> aClass) {
        return TransactionViewModelInput.class.equals(aClass);
    }

    @Override
    public void validate(final Object o, final Errors errors) {
        validate(o, errors, new Object[0]);
    }

    @Override
    public void validate(final Object o, final Errors errors, final Object... validationHints) {
        final TransactionViewModelInput transactionViewModelInput = (TransactionViewModelInput) o;
        final Snapshot snapshot = getSnapshot(validationHints);

        rejectIfInvalidDate(transactionViewModelInput, errors);
        rejectIfInvalidCurrencyUnit(transactionViewModelInput.getCurrencyUnit(), errors);
        if (!errors.hasFieldErrors(CURRENCY_UNIT_FIELD)) {
            rejectIfUnsupportedCurrencyUnit(
                    snapshot, CurrencyUnit.of(transactionViewModelInput.getCurrencyUnit()), errors);
        }

        rejectIfInvalidAmount(transactionViewModelInput, errors);
        rejectIfInvalidDescription(transactionViewModelInput, errors);
        rejectIfInvalidRecurrencePolicy(transactionViewModelInput, errors);

        final TransactionType transactionType =
                TransactionType.valueOf(transactionViewModelInput.getTypeName());

        rejectIfInvalidCategory(transactionType, transactionViewModelInput.getCategory(), errors);

        if (transactionType.equals(TransactionType.INCOME)) {
            rejectIfInvalidTithingPercentage(
                    transactionViewModelInput.getTithingPercentage(), errors);
        } else if (transactionType.equals(TransactionType.DONATION)) {
            rejectIfInvalidIsTaxDeductible(transactionViewModelInput, errors);
        }
    }

    private void rejectIfInvalidDate(
            final TransactionViewModelInput transactionViewModelInput, final Errors errors) {
        rejectIfEmptyOrWhitespace(errors, DATE_FIELD, NOT_EMPTY_ERRORCODE);

        if (StringUtils.isNotBlank(transactionViewModelInput.getDate())) {
            try {
                LocalDate.parse(transactionViewModelInput.getDate());
            } catch (final DateTimeParseException e) {
                errors.rejectValue(DATE_FIELD, "Invalid.date");
            }
        }
    }

    private void rejectIfInvalidAmount(
            final TransactionViewModelInput transactionViewModelInput, final Errors errors) {
        rejectIfEmptyOrWhitespace(errors, AMOUNT_FIELD, NOT_EMPTY_ERRORCODE);

        if (StringUtils.isNotBlank(transactionViewModelInput.getAmount())) {
            try {
                new BigDecimal(transactionViewModelInput.getAmount());
            } catch (final NumberFormatException e) {
                errors.rejectValue(AMOUNT_FIELD, "Invalid.amount");
            }
        }
    }

    private void rejectIfInvalidDescription(
            final TransactionViewModelInput transactionViewModelInput, final Errors errors) {
        rejectIfEmptyOrWhitespace(errors, DESCRIPTION_FIELD, NOT_EMPTY_ERRORCODE);
    }

    private void rejectIfInvalidRecurrencePolicy(
            final TransactionViewModelInput transactionViewModelInput, final Errors errors) {
        rejectIfEmptyOrWhitespace(errors, RECURRENCE_POLICY_FIELD, NOT_EMPTY_ERRORCODE);

        if (transactionViewModelInput.getRecurrencePolicy() == null) {
            try {
                RecurrencePolicy.valueOf(transactionViewModelInput.getRecurrencePolicy());
            } catch (final Exception e) {
                errors.rejectValue(RECURRENCE_POLICY_FIELD, "Invalid.value");
            }
        }
    }

    private void rejectIfInvalidCategory(
            final TransactionType transactionType, final String category, final Errors errors) {
        rejectIfEmptyOrWhitespace(errors, CATEGORY_FIELD, NOT_EMPTY_ERRORCODE);

        if (!errors.hasFieldErrors(CATEGORY_FIELD)) {
            try {
                if (transactionType.equals(TransactionType.INCOME)) {
                    IncomeCategory.valueOf(category);
                } else if (transactionType.equals(TransactionType.INVESTMENT)) {
                    InvestmentCategory.valueOf(category);
                } else if (transactionType.equals(TransactionType.DONATION)) {
                    DonationCategory.valueOf(category);
                }
            } catch (final Exception e) {
                errors.rejectValue(CATEGORY_FIELD, "Invalid.investmentCategory");
            }
        }
    }

    private void rejectIfInvalidIsTaxDeductible(
            final TransactionViewModelInput transactionViewModelInput, final Errors errors) {
        rejectIfEmptyOrWhitespace(errors, IS_TAX_DEDUCTIBLE_FIELD, NOT_EMPTY_ERRORCODE);

        if (transactionViewModelInput.getIsTaxDeductible() == null) {
            errors.rejectValue(IS_TAX_DEDUCTIBLE_FIELD, "Invalid.value");
        }
    }
}
