package br.net.du.myequity.viewmodel;

public class SimpleAssetViewModelOutput extends AccountSnapshotViewModelOutput {
    public SimpleAssetViewModelOutput(final Long accountId,
                                      final String name,
                                      final String currencyUnit,
                                      final String currencyUnitSymbol,
                                      final String balance,
                                      final String netWorth,
                                      final String accountType,
                                      final String totalForAccountType) {
        super(accountId, name, balance, currencyUnit, currencyUnitSymbol, netWorth, accountType, totalForAccountType);
    }
}
