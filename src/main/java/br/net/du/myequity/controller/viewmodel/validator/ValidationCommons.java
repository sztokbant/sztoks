package br.net.du.myequity.controller.viewmodel.validator;

import static org.springframework.validation.ValidationUtils.rejectIfEmptyOrWhitespace;

import java.math.BigDecimal;
import org.apache.commons.lang3.StringUtils;
import org.joda.money.CurrencyUnit;
import org.joda.money.IllegalCurrencyException;
import org.springframework.validation.Errors;

public class ValidationCommons {

    public static final String AMOUNT_FIELD = "amount";
    public static final String CURRENCY_UNIT_FIELD = "currencyUnit";
    public static final String DATE_FIELD = "date";
    public static final String DESCRIPTION_FIELD = "description";
    public static final String EMAIL_FIELD = "email";
    public static final String FIRST_NAME_FIELD = "firstName";
    public static final String INVESTMENT_CATEGORY_FIELD = "investmentCategory";
    public static final String IS_RECURRING_FIELD = "isRecurring";
    public static final String IS_TAX_DEDUCTIBLE_FIELD = "isTaxDeductible";
    public static final String LAST_NAME_FIELD = "lastName";
    public static final String NAME_FIELD = "name";
    public static final String NOT_EMPTY_ERRORCODE = "NotEmpty";
    public static final String PASSWORD_CONFIRM_FIELD = "passwordConfirm";
    public static final String PASSWORD_FIELD = "password";
    public static final String SUBTYPE_NAME_FIELD = "subtypeName";
    public static final String TITHING_PERCENTAGE_FIELD = "tithingPercentage";

    public static void rejectIfInvalidCurrencyUnit(final String currencyUnit, final Errors errors) {
        rejectIfEmptyOrWhitespace(errors, CURRENCY_UNIT_FIELD, NOT_EMPTY_ERRORCODE);

        if (errors.hasFieldErrors(CURRENCY_UNIT_FIELD)) {
            return;
        }

        try {
            CurrencyUnit.of(currencyUnit);
        } catch (final NullPointerException | IllegalCurrencyException e) {
            errors.rejectValue(CURRENCY_UNIT_FIELD, "Invalid.currency");
        }
    }

    public static void rejectIfInvalidTithingPercentage(
            final String tithingPercentage, final Errors errors) {
        rejectIfEmptyOrWhitespace(errors, TITHING_PERCENTAGE_FIELD, NOT_EMPTY_ERRORCODE);

        if (StringUtils.isNotBlank(tithingPercentage)) {
            try {
                new BigDecimal(tithingPercentage);
            } catch (final NumberFormatException e) {
                errors.rejectValue(TITHING_PERCENTAGE_FIELD, "Invalid.tithingPercentage");
            }
        }
    }
}
