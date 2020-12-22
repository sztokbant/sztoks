package br.net.du.myequity.controller.viewmodel.accountsnapshot;

public class SimpleLiabilityViewModelOutput extends AccountSnapshotViewModelOutput {
    public SimpleLiabilityViewModelOutput(
            final Long accountId,
            final String name,
            final String currencyUnit,
            final String currencyUnitSymbol,
            final String balance,
            final String netWorth,
            final String accountType,
            final String totalForAccountType) {
        super(
                accountId,
                name,
                balance,
                currencyUnit,
                currencyUnitSymbol,
                netWorth,
                accountType,
                totalForAccountType);
    }
}
