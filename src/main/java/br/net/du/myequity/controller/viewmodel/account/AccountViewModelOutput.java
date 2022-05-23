package br.net.du.myequity.controller.viewmodel.account;

import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

import br.net.du.myequity.controller.viewmodel.UpdatableTotals;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.account.FutureTithingCapable;
import br.net.du.myequity.model.totals.AccountSubtypeDisplayGroup;
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
    private final Integer currencyIndex;

    // fields only used on updates
    private final String netWorth;
    private final String accountType;
    private final String totalForAccountType;

    private final String accountSubtype;
    private final String totalForAccountSubtype;
    private final InvestmentTotalsViewModelOutput investmentTotals;
    private final CreditCardTotalsViewModelOutput creditCardTotalsForCurrencyUnit;

    private final String futureTithingBalance;
    private final String totalTithingBalance;
    private final String totalLiability;

    public AccountViewModelOutput(final AccountViewModelOutput other) {
        accountId = other.getAccountId();
        name = other.getName();

        balance = other.getBalance();
        currencyUnit = other.getCurrencyUnit();
        currencyUnitSymbol = other.getCurrencyUnitSymbol();
        currencyIndex = other.getCurrencyIndex();

        netWorth = other.getNetWorth();
        accountType = other.getAccountType();
        totalForAccountType = other.getTotalForAccountType();

        accountSubtype = other.getAccountSubtype();
        totalForAccountSubtype = other.getTotalForAccountSubtype();
        investmentTotals = other.getInvestmentTotals();
        creditCardTotalsForCurrencyUnit = other.getCreditCardTotalsForCurrencyUnit();

        futureTithingBalance = other.getFutureTithingBalance();
        totalTithingBalance = other.getTotalTithingBalance();
        totalLiability = other.getTotalLiability();
    }

    public static AccountViewModelOutput of(final Account account, final boolean includeTotals) {
        final Snapshot snapshot = account.getSnapshot();
        final CurrencyUnit currencyUnit = account.getCurrencyUnit();

        final String balance = format(currencyUnit, toDecimal(account.getBalance()));
        final AccountSubtypeDisplayGroup accountSubtypeDisplayGroup =
                account.getAccountSubtypeDisplayGroup();
        final AccountViewModelOutputBuilder builder =
                AccountViewModelOutput.builder()
                        .accountType(account.getAccountType().name())
                        .accountSubtype(accountSubtypeDisplayGroup.name())
                        .accountId(account.getId())
                        .name(account.getName())
                        .balance(balance)
                        .currencyUnit(currencyUnit.getCode())
                        .currencyUnitSymbol(currencyUnit.getSymbol())
                        .currencyIndex(
                                snapshot.getCurrenciesInUseBaseFirst()
                                        .indexOf(currencyUnit.getCode()));

        if (includeTotals) {
            final UpdatableTotals updatableTotals = new UpdatableTotals(snapshot);

            final String totalForAccountType =
                    updatableTotals.getTotalFor(account.getAccountType());
            builder.netWorth(updatableTotals.getNetWorth())
                    .totalForAccountType(totalForAccountType);

            // For INVESTMENT and CREDIT_CARD accounts totals are computed differently
            if (accountSubtypeDisplayGroup.equals(AccountSubtypeDisplayGroup.INVESTMENT)) {
                builder.investmentTotals(updatableTotals.getInvestmentTotals());
            } else if (accountSubtypeDisplayGroup.equals(AccountSubtypeDisplayGroup.CREDIT_CARD)) {
                builder.creditCardTotalsForCurrencyUnit(
                        updatableTotals.getCreditCardTotalsForCurrencyUnit(
                                account.getCurrencyUnit()));
            } else {
                builder.totalForAccountSubtype(
                        updatableTotals.getTotalForAccountSubtypeDisplayGroup(
                                accountSubtypeDisplayGroup));
            }

            if (account instanceof FutureTithingCapable) {
                final String totalLiability =
                        account.getAccountType().equals(AccountType.LIABILITY)
                                ? totalForAccountType
                                : updatableTotals.getTotalFor(AccountType.LIABILITY);
                builder.futureTithingBalance(updatableTotals.getFutureTithingBalance())
                        .totalTithingBalance(
                                updatableTotals.getTotalForAccountSubtypeDisplayGroup(
                                        AccountSubtypeDisplayGroup.TITHING))
                        .totalLiability(totalLiability);
            }
        }

        return builder.build();
    }

    public static AccountViewModelOutput of(final Account account) {
        return of(account, false);
    }

    @Override
    public int compareTo(final AccountViewModelOutput other) {
        if (accountType.equals(other.getAccountType())) {
            if (accountSubtype.equals(other.getAccountSubtype())) {
                if (currencyIndex.equals(other.getCurrencyIndex())) {
                    return name.compareToIgnoreCase(other.getName());
                }
                return currencyIndex.compareTo(other.getCurrencyIndex());
            }
            return accountSubtype.compareTo(other.getAccountSubtype());
        }
        return accountType.compareTo(other.getAccountType());
    }
}
