package br.net.du.sztoks.controller.viewmodel;

import lombok.Data;

@Data
public class NewCurrencyViewModelInput {
    private String currencyUnit;
    private String conversionRate;
}
