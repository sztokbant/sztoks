package br.net.du.myequity.controller.viewmodel.validator;

import static br.net.du.myequity.controller.viewmodel.validator.ValidationCommons.getSnapshot;

import br.net.du.myequity.controller.viewmodel.EditCurrenciesViewModelInput;
import br.net.du.myequity.model.Snapshot;
import java.math.BigDecimal;
import java.util.Map;
import lombok.NoArgsConstructor;
import org.joda.money.CurrencyUnit;
import org.joda.money.IllegalCurrencyException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;

@Component
@NoArgsConstructor
public class EditCurrenciesViewModelInputValidator implements SmartValidator {

    @Override
    public boolean supports(Class<?> aClass) {
        return EditCurrenciesViewModelInput.class.equals(aClass);
    }

    @Override
    public void validate(final Object o, final Errors errors) {
        validate(o, errors, new Object[0]);
    }

    @Override
    public void validate(final Object o, final Errors errors, final Object... validationHints) {
        final EditCurrenciesViewModelInput currenciesViewModelInput =
                (EditCurrenciesViewModelInput) o;
        final Snapshot snapshot = getSnapshot(validationHints);

        rejectIfInvalidEntry(
                currenciesViewModelInput.getCurrencyConversionRates(), snapshot, errors);
    }

    private void rejectIfInvalidEntry(
            final Map<String, String> currencyConversionRates,
            final Snapshot snapshot,
            final Errors errors) {
        for (final Map.Entry<String, String> entry : currencyConversionRates.entrySet()) {
            final String currency = entry.getKey();
            try {
                CurrencyUnit.of(currency);

                if (!snapshot.getCurrencyConversionRates().containsKey(currency)) {
                    throw new IllegalCurrencyException(currency);
                }

                final String conversionRate = entry.getValue();
                new BigDecimal(conversionRate);
            } catch (final IllegalCurrencyException | NumberFormatException e) {
                errors.rejectValue(getFieldName(currency), "Invalid.value");
            }
        }
    }

    private String getFieldName(final String currency) {
        return String.format("currencyConversionRates[%s]", currency);
    }
}
