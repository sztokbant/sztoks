package br.net.du.myequity.controller.viewmodel;

import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

import br.net.du.myequity.controller.viewmodel.account.CreditCardTotalsViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.InvestmentTotalsViewModelOutput;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.account.CreditCardAccount;
import br.net.du.myequity.model.account.InvestmentAccount;
import br.net.du.myequity.model.totals.BalanceUpdateableSubtype;
import br.net.du.myequity.model.totals.SnapshotTotalsCalculator;
import lombok.Data;

@Data
public class UpdateableTotals {
    private final String netWorth;
    private final String accountType;
    private final String totalForAccountType;
    private final String balanceUpdateableSubtype;
    private final String totalForAccountSubtype;
    private final InvestmentTotalsViewModelOutput investmentTotals;
    private final CreditCardTotalsViewModelOutput creditCardTotalsForCurrencyUnit;

    public UpdateableTotals(final Snapshot snapshot, final Account account) {
        netWorth = format(snapshot.getBaseCurrencyUnit(), toDecimal(snapshot.getNetWorth()));

        final AccountType accountType = account.getAccountType();
        this.accountType = accountType.name();

        totalForAccountType =
                format(
                        snapshot.getBaseCurrencyUnit(),
                        toDecimal(snapshot.getTotalFor(accountType)));

        final SnapshotTotalsCalculator snapshotTotalsCalculator =
                new SnapshotTotalsCalculator(snapshot);

        BalanceUpdateableSubtype balanceUpdateableSubtype;
        try {
            balanceUpdateableSubtype = BalanceUpdateableSubtype.forClass(account.getClass());
        } catch (final IllegalArgumentException e) {
            balanceUpdateableSubtype = null;
        }

        this.balanceUpdateableSubtype =
                balanceUpdateableSubtype != null ? balanceUpdateableSubtype.name() : null;

        totalForAccountSubtype =
                balanceUpdateableSubtype != null
                        ? format(
                                snapshot.getBaseCurrencyUnit(),
                                toDecimal(
                                        snapshotTotalsCalculator
                                                .getTotalBalance(balanceUpdateableSubtype)
                                                .getTotalBalance()))
                        : null;

        investmentTotals =
                account instanceof InvestmentAccount
                        ? InvestmentTotalsViewModelOutput.of(
                                snapshotTotalsCalculator.getInvestmentsTotal())
                        : null;

        creditCardTotalsForCurrencyUnit =
                account instanceof CreditCardAccount
                        ? CreditCardTotalsViewModelOutput.of(
                                snapshotTotalsCalculator.getCreditCardsTotalForCurrency(
                                        account.getCurrencyUnit()))
                        : null;
    }
}
