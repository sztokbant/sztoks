package br.net.du.myequity.controller.viewmodel.account;

import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

import br.net.du.myequity.controller.viewmodel.UpdateableTotals;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.SharedBillReceivableAccount;
import br.net.du.myequity.model.totals.AccountSubtypeDisplayGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
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

    // SharedBillReceivable only
    private final String billAmount;
    private final Integer numberOfPartners;
    private final Integer dueDay;

    // fields only used on updates
    private final String netWorth;
    private final String accountType;
    private final String totalForAccountType;

    private final String accountSubtype;
    private final String totalForAccountSubtype;
    private final InvestmentTotalsViewModelOutput investmentTotals;
    private final CreditCardTotalsViewModelOutput creditCardTotalsForCurrencyUnit;

    public AccountViewModelOutput(final AccountViewModelOutput other) {
        accountId = other.getAccountId();
        name = other.getName();

        balance = other.getBalance();
        currencyUnit = other.getCurrencyUnit();
        currencyUnitSymbol = other.getCurrencyUnitSymbol();

        billAmount = other.getBillAmount();
        numberOfPartners = other.getNumberOfPartners();
        dueDay = other.getDueDay();

        netWorth = other.getNetWorth();
        accountType = other.getAccountType();
        totalForAccountType = other.getTotalForAccountType();

        accountSubtype = other.getAccountSubtype();
        totalForAccountSubtype = other.getTotalForAccountSubtype();
        investmentTotals = other.getInvestmentTotals();
        creditCardTotalsForCurrencyUnit = other.getCreditCardTotalsForCurrencyUnit();
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

        builder.billAmount(
                account instanceof SharedBillReceivableAccount
                        ? format(
                                currencyUnit,
                                toDecimal(((SharedBillReceivableAccount) account).getBillAmount()))
                        : StringUtils.EMPTY);

        builder.numberOfPartners(
                account instanceof SharedBillReceivableAccount
                        ? ((SharedBillReceivableAccount) account).getNumberOfPartners()
                        : 0);

        builder.dueDay(
                account instanceof SharedBillReceivableAccount
                        ? ((SharedBillReceivableAccount) account).getDueDay()
                        : 0);

        if (includeTotals) {
            final UpdateableTotals updateableTotals = new UpdateableTotals(snapshot);
            final AccountSubtypeDisplayGroup accountSubtypeDisplayGroup =
                    AccountSubtypeDisplayGroup.forClass(account.getClass());

            builder.netWorth(updateableTotals.getNetWorth())
                    .accountType(account.getAccountType().name())
                    .totalForAccountType(updateableTotals.getTotalFor(account.getAccountType()))
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
