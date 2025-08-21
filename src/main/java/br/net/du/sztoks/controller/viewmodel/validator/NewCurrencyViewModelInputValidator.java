package br.net.du.sztoks.controller.viewmodel.validator;

import static br.net.du.sztoks.controller.viewmodel.validator.SnapshotValidationCommons.CONVERSION_RATE_FIELD;
import static br.net.du.sztoks.controller.viewmodel.validator.SnapshotValidationCommons.CURRENCY_UNIT_FIELD;
import static br.net.du.sztoks.controller.viewmodel.validator.SnapshotValidationCommons.getSnapshot;

import br.net.du.sztoks.controller.viewmodel.NewCurrencyViewModelInput;
import br.net.du.sztoks.model.Snapshot;
import java.math.BigDecimal;
import lombok.NoArgsConstructor;
import org.joda.money.CurrencyUnit;
import org.joda.money.IllegalCurrencyException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;

@Component
@NoArgsConstructor
public class NewCurrencyViewModelInputValidator implements SmartValidator {
    @Override
    public boolean supports(Class<?> aClass) {
        return NewCurrencyViewModelInput.class.equals(aClass);
    }

    @Override
    public void validate(final Object o, final Errors errors) {
        validate(o, errors, new Object[0]);
    }

    @Override
    public void validate(final Object o, final Errors errors, final Object... validationHints) {
        final NewCurrencyViewModelInput newCurrencyViewModelInput = (NewCurrencyViewModelInput) o;
        final Snapshot snapshot = getSnapshot(validationHints);

        rejectIfCurrencyAlreadyPresent(
                newCurrencyViewModelInput.getCurrencyUnit(), snapshot, errors);
        rejectIfBaseCurrencyPresent(newCurrencyViewModelInput.getCurrencyUnit(), snapshot, errors);
        rejectIfInvalidEntry(
                newCurrencyViewModelInput.getCurrencyUnit(),
                newCurrencyViewModelInput.getConversionRate(),
                errors);
    }

    private void rejectIfBaseCurrencyPresent(
            final String currency, final Snapshot snapshot, final Errors errors) {

        final String baseCurrency = snapshot.getBaseCurrencyUnit().toString();
        if (currency.equals(baseCurrency)) {
            errors.rejectValue(CURRENCY_UNIT_FIELD, "Invalid.currencyBaseCurrency");
        }
    }

    private void rejectIfCurrencyAlreadyPresent(
            final String currency, final Snapshot snapshot, final Errors errors) {
        if (snapshot.getCurrencyConversionRates().containsKey(currency)) {
            errors.rejectValue(CURRENCY_UNIT_FIELD, "Invalid.currencyPresent");
        }
    }

    private void rejectIfInvalidEntry(
            final String currency, final String conversionRate, final Errors errors) {
        try {
            CurrencyUnit.of(currency);
        } catch (final IllegalCurrencyException e) {
            errors.rejectValue(CURRENCY_UNIT_FIELD, "Invalid.currency");
        }

        try {
            final BigDecimal conversionRateBigDecimal = new BigDecimal(conversionRate);
            if (conversionRateBigDecimal.compareTo(BigDecimal.ZERO) == 0) {
                throw new IllegalArgumentException();
            }
        } catch (final Exception e) {
            errors.rejectValue(CONVERSION_RATE_FIELD, "Invalid.conversionRate");
        }
    }
}
