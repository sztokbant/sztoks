package br.net.du.myequity.controller.viewmodel;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@NoArgsConstructor
public class EditCurrenciesViewModelInput {
    @Getter @Setter private Map<String, String> currencyConversionRates = new HashMap<>();

    public EditCurrenciesViewModelInput(
            @NonNull final Map<String, BigDecimal> currencyConversionRates) {
        currencyConversionRates.entrySet().stream()
                .forEach(
                        entry ->
                                this.currencyConversionRates.put(
                                        entry.getKey(), entry.getValue().toPlainString()));
    }
}
