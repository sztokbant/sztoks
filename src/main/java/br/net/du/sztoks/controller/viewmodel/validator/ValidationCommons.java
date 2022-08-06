package br.net.du.sztoks.controller.viewmodel.validator;

import static org.springframework.validation.ValidationUtils.rejectIfEmptyOrWhitespace;

import br.net.du.sztoks.model.Snapshot;
import java.math.BigDecimal;
import org.apache.commons.lang3.StringUtils;
import org.joda.money.CurrencyUnit;
import org.joda.money.IllegalCurrencyException;
import org.springframework.validation.Errors;

public class ValidationCommons {

    public static final String AMOUNT_FIELD = "amount";
    public static final String CONVERSION_RATE_FIELD = "conversionRate";
    public static final String CURRENCY_UNIT_FIELD = "currencyUnit";
    public static final String DATE_FIELD = "date";
    public static final String DESCRIPTION_FIELD = "description";
    public static final String EMAIL_FIELD = "email";
    public static final String FIRST_NAME_FIELD = "firstName";
    public static final String FUTURE_TITHING_POLICY_FIELD = "futureTithingPolicy";
    public static final String CATEGORY_FIELD = "category";
    public static final String IS_RESETTABLE_FIELD = "isResettable";
    public static final String IS_TAX_DEDUCTIBLE_FIELD = "isTaxDeductible";
    public static final String LAST_NAME_FIELD = "lastName";
    public static final String NAME_FIELD = "name";
    public static final String NOT_EMPTY_ERRORCODE = "NotEmpty";
    public static final String PASSWORD_CONFIRM_FIELD = "passwordConfirm";
    public static final String PASSWORD_FIELD = "password";
    public static final String RECURRENCE_POLICY_FIELD = "recurrencePolicy";
    public static final String SUBTYPE_NAME_FIELD = "subtypeName";
    public static final String TITHING_PERCENTAGE_FIELD = "tithingPercentage";

    public static Snapshot getSnapshot(final Object[] validationHints) {
        if ((validationHints == null)
                || (validationHints.length != 1)
                || !(validationHints[0] instanceof Snapshot)) {
            throw new UnsupportedOperationException();
        }

        return (Snapshot) validationHints[0];
    }

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

    public static void rejectIfUnsupportedCurrencyUnit(
            final Snapshot snapshot, final CurrencyUnit currencyUnit, final Errors errors) {
        if (!snapshot.supports(currencyUnit)) {
            errors.rejectValue(CURRENCY_UNIT_FIELD, "Unsupported.currency");
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
