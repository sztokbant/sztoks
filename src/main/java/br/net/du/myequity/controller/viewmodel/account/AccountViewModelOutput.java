package br.net.du.myequity.controller.viewmodel.account;

import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.totals.BalanceUpdateableSubtype;
import br.net.du.myequity.model.totals.BalanceUpdateableSubtypeTotal;
import br.net.du.myequity.model.totals.SnapshotTotalsCalculator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;

@AllArgsConstructor
@Data
@Builder
public class AccountViewModelOutput implements Comparable<AccountViewModelOutput> {
    private final Long accountId;
    private final String name;

    private final String balance;
    private final String currencyUnit;
    private final String currencyUnitSymbol;

    // fields only used on updates
    private final String netWorth;
    private final String accountType;
    private final String totalForAccountType;

    private final String accountSubtype;
    private final String totalForAccountSubtype;

    public AccountViewModelOutput(final AccountViewModelOutput other) {
        accountId = other.getAccountId();
        name = other.getName();

        balance = other.getBalance();
        currencyUnit = other.getCurrencyUnit();
        currencyUnitSymbol = other.getCurrencyUnitSymbol();

        netWorth = other.getNetWorth();
        accountType = other.getAccountType();
        totalForAccountType = other.getTotalForAccountType();

        accountSubtype = other.getAccountSubtype();
        totalForAccountSubtype = other.getTotalForAccountSubtype();
    }

    public static AccountViewModelOutput of(final Account account, final boolean includeTotals) {
        final Snapshot snapshot = account.getSnapshot();
        final CurrencyUnit currencyUnit = account.getCurrencyUnit();

        final String balance = format(currencyUnit, toDecimal(account.getBalance()));
        final AccountViewModelOutputBuilder builder =
                AccountViewModelOutput.builder()
                        .accountId(account.getId())
                        .name(account.getName())
                        .balance(balance)
                        .currencyUnit(currencyUnit.getCode())
                        .currencyUnitSymbol(currencyUnit.getSymbol());

        if (includeTotals) {
            final String netWorth =
                    format(snapshot.getBaseCurrencyUnit(), toDecimal(snapshot.getNetWorth()));

            final AccountType accountType = account.getAccountType();
            final String totalForAccountType =
                    format(
                            snapshot.getBaseCurrencyUnit(),
                            toDecimal(snapshot.getTotalFor(accountType)));

            builder.netWorth(netWorth)
                    .accountType(accountType.name())
                    .totalForAccountType(totalForAccountType);

            BalanceUpdateableSubtype balanceUpdateableSubtype = null;
            String totalForAccountSubtype = null;
            try {
                final SnapshotTotalsCalculator snapshotTotalsCalculator =
                        new SnapshotTotalsCalculator(snapshot);

                balanceUpdateableSubtype = BalanceUpdateableSubtype.forClass(account.getClass());
                final BalanceUpdateableSubtypeTotal totalBalance =
                        snapshotTotalsCalculator.getTotalBalance(balanceUpdateableSubtype);

                totalForAccountSubtype =
                        format(
                                snapshot.getBaseCurrencyUnit(),
                                toDecimal(totalBalance.getTotalBalance()));
            } catch (final IllegalArgumentException e) {
                // Ignored
            }

            builder.accountSubtype(
                    balanceUpdateableSubtype != null ? balanceUpdateableSubtype.name() : null);
            builder.totalForAccountSubtype(totalForAccountSubtype);
        }

        return builder.build();
    }

    public static AccountViewModelOutput of(final Account account) {
        return of(account, false);
    }

    @Override
    public int compareTo(final AccountViewModelOutput other) {
        return currencyUnit.equals(other.getCurrencyUnit())
                ? name.compareTo(other.getName())
                : currencyUnit.compareTo(other.getCurrencyUnit());
    }
}
