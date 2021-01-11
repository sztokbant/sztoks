package br.net.du.myequity.controller.viewmodel.accountsnapshot;

import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;

import br.net.du.myequity.controller.util.MoneyFormatUtils;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;

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

    public static AccountSnapshotViewModelOutput of(
            final AccountSnapshot accountSnapshot, final boolean includeTotals) {
        final Account account = accountSnapshot.getAccount();
        final Snapshot snapshot = accountSnapshot.getSnapshot();
        final CurrencyUnit currencyUnit = accountSnapshot.getAccount().getCurrencyUnit();

        final String balance =
                MoneyFormatUtils.format(currencyUnit, toDecimal(accountSnapshot.getTotal()));
        final AccountSnapshotViewModelOutputBuilder builder =
                AccountSnapshotViewModelOutput.builder()
                        .accountId(account.getId())
                        .name(account.getName())
                        .balance(balance)
                        .currencyUnit(currencyUnit.getCode())
                        .currencyUnitSymbol(currencyUnit.getSymbol());

        if (includeTotals) {
            final String netWorth =
                    MoneyFormatUtils.format(
                            currencyUnit, toDecimal(snapshot.getNetWorth().get(currencyUnit)));
            final AccountType accountType = accountSnapshot.getAccount().getAccountType();
            final String totalForAccountType =
                    MoneyFormatUtils.format(
                            currencyUnit,
                            toDecimal(
                                    snapshot.getTotalForAccountType(accountType)
                                            .get(currencyUnit)));
            builder.netWorth(netWorth)
                    .accountType(accountType.name())
                    .totalForAccountType(totalForAccountType);
        }

        return builder.build();
    }

    public static AccountSnapshotViewModelOutput of(final AccountSnapshot accountSnapshot) {
        return of(accountSnapshot, false);
    }

    @Override
    public int compareTo(final AccountSnapshotViewModelOutput other) {
        return currencyUnit.equals(other.getCurrencyUnit())
                ? name.compareTo(other.getName())
                : currencyUnit.compareTo(other.getCurrencyUnit());
    }
}
