package br.net.du.myequity.controller.viewmodel;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@NoArgsConstructor
public class EditCurrenciesViewModelInput {
    // Using TreeMap so currencies are sorted
    @Getter @Setter private Map<String, String> currencyConversionRates = new TreeMap<>();

    public EditCurrenciesViewModelInput(
            @NonNull final Map<String, BigDecimal> currencyConversionRates) {
        currencyConversionRates.entrySet().stream()
                .forEach(
                        entry ->
                                this.currencyConversionRates.put(
                                        entry.getKey(), entry.getValue().toPlainString()));
    }
}
