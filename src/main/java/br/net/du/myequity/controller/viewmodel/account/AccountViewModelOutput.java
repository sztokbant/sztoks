package br.net.du.myequity.controller.viewmodel.account;

import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

import br.net.du.myequity.controller.viewmodel.UpdateableTotals;
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
        final AccountViewModelOutputBuilder builder =
                AccountViewModelOutput.builder()
                        .accountId(account.getId())
                        .name(account.getName())
                        .balance(balance)
                        .currencyUnit(currencyUnit.getCode())
                        .currencyUnitSymbol(currencyUnit.getSymbol());

        if (includeTotals) {
            final UpdateableTotals updateableTotals = new UpdateableTotals(snapshot);
            final AccountSubtypeDisplayGroup accountSubtypeDisplayGroup =
                    AccountSubtypeDisplayGroup.forClass(account.getClass());

            final String totalForAccountType =
                    updateableTotals.getTotalFor(account.getAccountType());
            builder.netWorth(updateableTotals.getNetWorth())
                    .accountType(account.getAccountType().name())
                    .totalForAccountType(totalForAccountType)
                    .accountSubtype(
                            accountSubtypeDisplayGroup == null
                                    ? null
                                    : accountSubtypeDisplayGroup.name())
                    .totalForAccountSubtype(
                            accountSubtypeDisplayGroup == null
                                    ? null
                                    : updateableTotals.getTotalForAccountSubtype(
                                            accountSubtypeDisplayGroup))
                    .investmentTotals(updateableTotals.getInvestmentTotals())
                    .creditCardTotalsForCurrencyUnit(
                            updateableTotals.getCreditCardTotalsForCurrencyUnit(
                                    account.getCurrencyUnit()));

            if (account instanceof FutureTithingCapable) {
                final String totalLiability =
                        account.getAccountType().equals(AccountType.LIABILITY)
                                ? totalForAccountType
                                : updateableTotals.getTotalFor(AccountType.LIABILITY);
                builder.futureTithingBalance(updateableTotals.getFutureTithingBalance())
                        .totalTithingBalance(
                                updateableTotals.getTotalForAccountSubtype(
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
        return currencyUnit.equals(other.getCurrencyUnit())
                ? name.compareTo(other.getName())
                : currencyUnit.compareTo(other.getCurrencyUnit());
    }
}
