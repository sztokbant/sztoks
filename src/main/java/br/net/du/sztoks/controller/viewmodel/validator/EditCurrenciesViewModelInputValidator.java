package br.net.du.sztoks.controller.viewmodel.validator;

import static br.net.du.sztoks.controller.viewmodel.validator.SnapshotValidationCommons.getSnapshot;

import br.net.du.sztoks.controller.viewmodel.EditCurrenciesViewModelInput;
import br.net.du.sztoks.model.Snapshot;
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
            } catch (final Exception e) {
                errors.rejectValue(getFieldName(currency), "Invalid.currency");
            }

            try {
                final BigDecimal conversionRate = new BigDecimal(entry.getValue());
                if (conversionRate.compareTo(BigDecimal.ZERO) == 0) {
                    throw new IllegalArgumentException();
                }
            } catch (final Exception e) {
                errors.rejectValue(getFieldName(currency), "Invalid.conversionRate");
            }
        }
    }

    private String getFieldName(final String currency) {
        return String.format("currencyConversionRates[%s]", currency);
    }
}
