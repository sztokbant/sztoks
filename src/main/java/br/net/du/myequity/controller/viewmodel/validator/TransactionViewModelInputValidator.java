package br.net.du.myequity.controller.viewmodel.validator;

import static br.net.du.myequity.controller.util.ControllerConstants.AMOUNT;
import static br.net.du.myequity.controller.util.ControllerConstants.CURRENCY_UNIT;
import static br.net.du.myequity.controller.util.ControllerConstants.DATE;
import static br.net.du.myequity.controller.util.ControllerConstants.DESCRIPTION;
import static br.net.du.myequity.controller.util.ControllerConstants.DONATION_RATIO;
import static br.net.du.myequity.controller.util.ControllerConstants.INVESTMENT_CATEGORY;
import static br.net.du.myequity.controller.util.ControllerConstants.IS_RECURRING;
import static br.net.du.myequity.controller.util.ControllerConstants.IS_TAX_DEDUCTIBLE;
import static br.net.du.myequity.controller.util.ControllerConstants.NOT_EMPTY;

import br.net.du.myequity.controller.viewmodel.TransactionViewModelInput;
import br.net.du.myequity.model.transaction.InvestmentCategory;
import br.net.du.myequity.model.transaction.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.joda.money.CurrencyUnit;
import org.joda.money.IllegalCurrencyException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;

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

        rejectIfInvalidDate(transactionViewModelInput, errors);
        rejectIfInvalidCurrencyUnit(transactionViewModelInput, errors);
        rejectIfInvalidAmount(transactionViewModelInput, errors);
        rejectIfInvalidDescription(transactionViewModelInput, errors);
        rejectIfInvalidIsRecurring(transactionViewModelInput, errors);

        final TransactionType transactionType =
                TransactionType.valueOf(transactionViewModelInput.getTypeName());
        if (transactionType.equals(TransactionType.INCOME)) {
            rejectIfInvalidDonationRatio(transactionViewModelInput, errors);
        } else if (transactionType.equals(TransactionType.INVESTMENT)) {
            rejectIfInvalidInvestmentCategory(transactionViewModelInput, errors);
        } else if (transactionType.equals(TransactionType.DONATION)) {
            rejectIfInvalidIsTaxDeductible(transactionViewModelInput, errors);
        }
    }

    private void rejectIfInvalidDate(
            final TransactionViewModelInput transactionViewModelInput, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, DATE, NOT_EMPTY);

        if (StringUtils.isNotBlank(transactionViewModelInput.getDate())) {
            try {
                LocalDate.parse(transactionViewModelInput.getDate());
            } catch (final DateTimeParseException e) {
                errors.rejectValue(DATE, "Invalid.date");
            }
        }
    }

    private void rejectIfInvalidCurrencyUnit(
            final TransactionViewModelInput transactionViewModelInput, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, CURRENCY_UNIT, NOT_EMPTY);

        if (StringUtils.isNotBlank(transactionViewModelInput.getCurrencyUnit())) {
            try {
                CurrencyUnit.of(transactionViewModelInput.getCurrencyUnit());
            } catch (final NullPointerException | IllegalCurrencyException e) {
                errors.rejectValue(CURRENCY_UNIT, "Invalid.currency");
            }
        }
    }

    private void rejectIfInvalidAmount(
            final TransactionViewModelInput transactionViewModelInput, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, AMOUNT, NOT_EMPTY);

        if (StringUtils.isNotBlank(transactionViewModelInput.getAmount())) {
            try {
                new BigDecimal(transactionViewModelInput.getAmount());
            } catch (final NumberFormatException e) {
                errors.rejectValue(AMOUNT, "Invalid.amount");
            }
        }
    }

    private void rejectIfInvalidDescription(
            final TransactionViewModelInput transactionViewModelInput, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, DESCRIPTION, NOT_EMPTY);
    }

    private void rejectIfInvalidIsRecurring(
            final TransactionViewModelInput transactionViewModelInput, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, IS_RECURRING, NOT_EMPTY);

        if (transactionViewModelInput.getIsRecurring() == null) {
            errors.rejectValue(IS_RECURRING, "Invalid.value");
        }
    }

    private void rejectIfInvalidDonationRatio(
            final TransactionViewModelInput transactionViewModelInput, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, DONATION_RATIO, NOT_EMPTY);

        if (StringUtils.isNotBlank(transactionViewModelInput.getDonationRatio())) {
            try {
                new BigDecimal(transactionViewModelInput.getDonationRatio());
            } catch (final NumberFormatException e) {
                errors.rejectValue(DONATION_RATIO, "Invalid.donationRatio");
            }
        }
    }

    private void rejectIfInvalidInvestmentCategory(
            final TransactionViewModelInput transactionViewModelInput, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, INVESTMENT_CATEGORY, NOT_EMPTY);

        if (StringUtils.isNotBlank(transactionViewModelInput.getInvestmentCategory())) {
            try {
                InvestmentCategory.valueOf(transactionViewModelInput.getInvestmentCategory());
            } catch (final Exception e) {
                errors.rejectValue(INVESTMENT_CATEGORY, "Invalid.investmentCategory");
            }
        }
    }

    private void rejectIfInvalidIsTaxDeductible(
            final TransactionViewModelInput transactionViewModelInput, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, IS_TAX_DEDUCTIBLE, NOT_EMPTY);

        if (transactionViewModelInput.getIsTaxDeductible() == null) {
            errors.rejectValue(IS_TAX_DEDUCTIBLE, "Invalid.value");
        }
    }
}
