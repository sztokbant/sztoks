package br.net.du.myequity.controller.viewmodel.validator;

import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.AMOUNT_FIELD;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.CATEGORY_FIELD;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.CURRENCY_UNIT_FIELD;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.DATE_FIELD;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.DESCRIPTION_FIELD;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.IS_RECURRING_FIELD;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.IS_TAX_DEDUCTIBLE_FIELD;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.NOT_EMPTY_ERRORCODE;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.getSnapshot;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.rejectIfInvalidCurrencyUnit;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.rejectIfInvalidTithingPercentage;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.rejectIfUnsupportedCurrencyUnit;
import static org.springframework.validation.ValidationUtils.rejectIfEmptyOrWhitespace;

import br.net.du.myequity.controller.viewmodel.transaction.TransactionViewModelInput;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.transaction.InvestmentCategory;
import br.net.du.myequity.model.transaction.TransactionType;
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
        rejectIfInvalidIsRecurring(transactionViewModelInput, errors);

        final TransactionType transactionType =
                TransactionType.valueOf(transactionViewModelInput.getTypeName());
        if (transactionType.equals(TransactionType.INCOME)) {
            rejectIfInvalidTithingPercentage(
                    transactionViewModelInput.getTithingPercentage(), errors);
        } else if (transactionType.equals(TransactionType.INVESTMENT)) {
            rejectIfInvalidCategory(transactionViewModelInput, errors);
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

    private void rejectIfInvalidIsRecurring(
            final TransactionViewModelInput transactionViewModelInput, final Errors errors) {
        rejectIfEmptyOrWhitespace(errors, IS_RECURRING_FIELD, NOT_EMPTY_ERRORCODE);

        if (transactionViewModelInput.getIsRecurring() == null) {
            errors.rejectValue(IS_RECURRING_FIELD, "Invalid.value");
        }
    }

    private void rejectIfInvalidCategory(
            final TransactionViewModelInput transactionViewModelInput, final Errors errors) {
        rejectIfEmptyOrWhitespace(errors, CATEGORY_FIELD, NOT_EMPTY_ERRORCODE);

        if (StringUtils.isNotBlank(transactionViewModelInput.getCategory())) {
            try {
                InvestmentCategory.valueOf(transactionViewModelInput.getCategory());
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
