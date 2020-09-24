package br.net.du.myequity.controller.model;

import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;

@AllArgsConstructor
@Builder
@Data
public class AccountSnapshotUpdateJsonResponse {
    private String balance;
    private String currencyUnit;
    private String currencyUnitSymbol;
    private String netWorth;
    private String accountType;
    private String totalForAccountType;

    public AccountSnapshotUpdateJsonResponse(final AccountSnapshotUpdateJsonResponse snapshotAccountUpdateJsonResponse) {
        this.balance = snapshotAccountUpdateJsonResponse.getBalance();
        this.currencyUnit = snapshotAccountUpdateJsonResponse.getCurrencyUnit();
        this.currencyUnitSymbol = snapshotAccountUpdateJsonResponse.getCurrencyUnitSymbol();
        this.netWorth = snapshotAccountUpdateJsonResponse.getNetWorth();
        this.accountType = snapshotAccountUpdateJsonResponse.getAccountType();
        this.totalForAccountType = snapshotAccountUpdateJsonResponse.getTotalForAccountType();
    }

    public static AccountSnapshotUpdateJsonResponse of(final AccountSnapshot accountSnapshot) {
        return getJsonResponseBuilderCommon(accountSnapshot).build();
    }

    private static AccountSnapshotUpdateJsonResponse.AccountSnapshotUpdateJsonResponseBuilder getJsonResponseBuilderCommon(
            final AccountSnapshot accountSnapshot) {
        final Snapshot snapshot = accountSnapshot.getSnapshot();
        final CurrencyUnit currencyUnit = accountSnapshot.getAccount().getCurrencyUnit();
        final AccountType accountType = accountSnapshot.getAccount().getAccountType();

        return AccountSnapshotUpdateJsonResponse.builder()
                                                .balance(formatAsDecimal(accountSnapshot.getTotal()))
                                                .currencyUnit(currencyUnit.getCode())
                                                .currencyUnitSymbol(currencyUnit.getSymbol())
                                                .netWorth(formatAsDecimal(snapshot.getNetWorth().get(currencyUnit)))
                                                .accountType(accountType.name())
                                                .totalForAccountType(formatAsDecimal(snapshot.getTotalForAccountType(
                                                        accountType).get(currencyUnit)));
    }
}
