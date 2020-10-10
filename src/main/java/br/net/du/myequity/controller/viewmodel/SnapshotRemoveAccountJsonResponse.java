package br.net.du.myequity.controller.viewmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SnapshotRemoveAccountJsonResponse {
    private Long accountId;
    private String currencyUnit;
    private String currencyUnitSymbol;
    private String netWorth;
    private String accountType;
    private String totalForAccountType;
    private CreditCardTotalsViewModelOutput creditCardTotalsForCurrencyUnit;
}
