package br.net.du.sztoks.controller.viewmodel;

import br.net.du.sztoks.controller.viewmodel.account.CreditCardTotalsViewModelOutput;
import br.net.du.sztoks.controller.viewmodel.account.InvestmentTotalsViewModelOutput;
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

    private String accountSubtype;
    private String totalForAccountSubtype;

    private String futureTithingBalance;
    private String totalTithingBalance;
    private String totalLiability;

    private InvestmentTotalsViewModelOutput investmentTotals;
    private CreditCardTotalsViewModelOutput creditCardTotalsForCurrencyUnit;
}
