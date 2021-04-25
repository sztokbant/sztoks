package br.net.du.myequity.controller.viewmodel;

import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

import br.net.du.myequity.controller.viewmodel.account.CreditCardTotalsViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.InvestmentTotalsViewModelOutput;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.totals.AccountSubtypeDisplayGroup;
import br.net.du.myequity.model.totals.SnapshotTotalsCalculator;
import br.net.du.myequity.model.transaction.TransactionType;
import lombok.Getter;
import org.joda.money.CurrencyUnit;

public class UpdateableTotals {
    private final Snapshot snapshot;
    private final SnapshotTotalsCalculator snapshotTotalsCalculator;

    @Getter private final String netWorth;

    public UpdateableTotals(final Snapshot snapshot) {
        this.snapshot = snapshot;
        snapshotTotalsCalculator = new SnapshotTotalsCalculator(snapshot);
        netWorth = format(snapshot.getBaseCurrencyUnit(), toDecimal(snapshot.getNetWorth()));
    }

    public String getTotalFor(final AccountType accountType) {
        return format(snapshot.getBaseCurrencyUnit(), toDecimal(snapshot.getTotalFor(accountType)));
    }

    public String getTotalForAccountSubtype(
            final AccountSubtypeDisplayGroup accountSubtypeDisplayGroup) {
        return format(
                snapshot.getBaseCurrencyUnit(),
                toDecimal(
                        snapshotTotalsCalculator
                                .getTotalBalance(accountSubtypeDisplayGroup)
                                .getTotalBalance()));
    }

    public InvestmentTotalsViewModelOutput getInvestmentTotals() {
        return InvestmentTotalsViewModelOutput.of(snapshotTotalsCalculator.getInvestmentsTotal());
    }

    public CreditCardTotalsViewModelOutput getCreditCardTotalsForCurrencyUnit(
            final CurrencyUnit currencyUnit) {
        return CreditCardTotalsViewModelOutput.of(
                snapshotTotalsCalculator.getCreditCardsTotalForCurrency(currencyUnit));
    }

    public String getTotalFor(final TransactionType transactionType) {
        return format(
                snapshot.getBaseCurrencyUnit(), toDecimal(snapshot.getTotalFor(transactionType)));
    }

    public String getTaxDeductibleDonationsTotal() {
        return format(
                snapshot.getBaseCurrencyUnit(),
                toDecimal(snapshot.getTaxDeductibleDonationsTotal()));
    }

    public String getTithingBalance() {
        return format(snapshot.getBaseCurrencyUnit(), toDecimal(snapshot.getTithingBalance()));
    }
}
