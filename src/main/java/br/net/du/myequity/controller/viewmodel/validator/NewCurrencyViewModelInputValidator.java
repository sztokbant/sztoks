package br.net.du.myequity.controller.viewmodel.validator;

import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.CONVERSION_RATE_FIELD;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.CURRENCY_UNIT_FIELD;
import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.getSnapshot;

import br.net.du.myequity.controller.viewmodel.NewCurrencyViewModelInput;
import br.net.du.myequity.model.Snapshot;
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
            new BigDecimal(conversionRate);
        } catch (final NumberFormatException e) {
            errors.rejectValue(CONVERSION_RATE_FIELD, "Invalid.conversionRate");
        }
    }
}
