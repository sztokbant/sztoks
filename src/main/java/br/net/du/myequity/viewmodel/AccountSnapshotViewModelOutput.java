package br.net.du.myequity.viewmodel;

import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;

@AllArgsConstructor
@Data
@Builder
public class AccountSnapshotViewModelOutput implements Comparable<AccountSnapshotViewModelOutput> {
    private final Long accountId;
    private final String name;

    private final String balance;
    private final String currencyUnit;
    private final String currencyUnitSymbol;

    // fields only used on updates
    private final String netWorth;
    private final String accountType;
    private final String totalForAccountType;

    public AccountSnapshotViewModelOutput(final AccountSnapshotViewModelOutput other) {
        accountId = other.getAccountId();
        name = other.getName();

        balance = other.getBalance();
        currencyUnit = other.getCurrencyUnit();
        currencyUnitSymbol = other.getCurrencyUnitSymbol();

        netWorth = other.getNetWorth();
        accountType = other.getAccountType();
        totalForAccountType = other.getTotalForAccountType();
    }

    public static AccountSnapshotViewModelOutput of(final AccountSnapshot accountSnapshot) {
        final Account account = accountSnapshot.getAccount();
        final Snapshot snapshot = accountSnapshot.getSnapshot();
        final CurrencyUnit currencyUnit = accountSnapshot.getAccount().getCurrencyUnit();

        // TODO These should probably be null unless it's an update operation
        final String netWorth = formatAsDecimal(snapshot.getNetWorth().get(currencyUnit));
        final AccountType accountType = accountSnapshot.getAccount().getAccountType();
        final String totalForAccountType =
                formatAsDecimal(snapshot.getTotalForAccountType(accountType).get(currencyUnit));

        return AccountSnapshotViewModelOutput.builder()
                                             .accountId(account.getId())
                                             .name(account.getName())

                                             .balance(formatAsDecimal(accountSnapshot.getTotal()))
                                             .currencyUnit(currencyUnit.getCode())
                                             .currencyUnitSymbol(currencyUnit.getSymbol())

                                             .netWorth(netWorth)
                                             .accountType(accountType.name())
                                             .totalForAccountType(totalForAccountType)

                                             .build();
    }

    @Override
    public int compareTo(final AccountSnapshotViewModelOutput other) {
        return this.currencyUnit.equals(other.getCurrencyUnit()) ?
                this.name.compareTo(other.name) :
                this.currencyUnit.compareTo(other.getCurrencyUnit());
    }
}
