package br.net.du.myequity.controller.viewmodel;

import lombok.Data;

@Data
public class NewCurrencyViewModelInput {
    private String currencyUnit;
    private String conversionRate;
}
