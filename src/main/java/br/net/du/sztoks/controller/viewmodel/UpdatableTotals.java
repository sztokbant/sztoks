package br.net.du.sztoks.controller.viewmodel;

import static br.net.du.sztoks.controller.util.ControllerUtils.formatAsPercentage;
import static br.net.du.sztoks.controller.util.ControllerUtils.toDecimal;
import static br.net.du.sztoks.controller.util.MoneyFormatUtils.format;
import static br.net.du.sztoks.model.util.ModelConstants.DIVISION_SCALE;
import static br.net.du.sztoks.model.util.ModelConstants.ONE_HUNDRED;

import br.net.du.sztoks.controller.viewmodel.account.CreditCardTotalsViewModelOutput;
import br.net.du.sztoks.controller.viewmodel.account.InvestmentTotalsViewModelOutput;
import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.account.AccountType;
import br.net.du.sztoks.model.totals.AccountSubtypeDisplayGroup;
import br.net.du.sztoks.model.totals.SnapshotTotalsCalculator;
import br.net.du.sztoks.model.transaction.TransactionType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Getter;
import org.joda.money.CurrencyUnit;

public class UpdatableTotals {
    private final Snapshot snapshot;
    private final SnapshotTotalsCalculator snapshotTotalsCalculator;

    @Getter private final String netWorth;
    @Getter private final String netWorthDiff;
    @Getter private final String netWorthDiffPercentage;
    @Getter private final boolean isNetWorthIncreased;

    public UpdatableTotals(final Snapshot snapshot) {
        this.snapshot = snapshot;
        snapshotTotalsCalculator = new SnapshotTotalsCalculator(snapshot);
        netWorth = format(snapshot.getBaseCurrencyUnit(), toDecimal(snapshot.getNetWorth()));

        final BigDecimal netWorthDiffValue = computeNetWorthDiff(snapshot);
        isNetWorthIncreased = netWorthDiffValue.compareTo(BigDecimal.ZERO) >= 0;
        netWorthDiff = format(snapshot.getBaseCurrencyUnit(), netWorthDiffValue);
        netWorthDiffPercentage =
                formatAsPercentage(computeNetWorthDiffPercentage(netWorthDiffValue));
    }

    private BigDecimal computeNetWorthDiff(final Snapshot snapshot) {
        final Snapshot previousSnapshot = snapshot.getPrevious();
        if (previousSnapshot != null) {
            if (this.snapshot
                    .getBaseCurrencyUnit()
                    .equals(previousSnapshot.getBaseCurrencyUnit())) {
                return snapshot.getNetWorth().subtract(previousSnapshot.getNetWorth());
            } else {
                // TODO: convert currency
            }
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal computeNetWorthDiffPercentage(final BigDecimal netWorthDiffValue) {
        final Snapshot previousSnapshot = snapshot.getPrevious();
        if (previousSnapshot != null) {
            if (this.snapshot
                    .getBaseCurrencyUnit()
                    .equals(previousSnapshot.getBaseCurrencyUnit())) {
                return netWorthDiffValue
                        .divide(
                                snapshot.getPrevious().getNetWorth(),
                                DIVISION_SCALE,
                                RoundingMode.HALF_UP)
                        .multiply(ONE_HUNDRED);
            } else {
                // TODO: convert currency
            }
        }
        return BigDecimal.ZERO;
    }

    public String getTotalFor(final AccountType accountType) {
        return format(snapshot.getBaseCurrencyUnit(), toDecimal(snapshot.getTotalFor(accountType)));
    }

    public String getTotalForAccountSubtypeDisplayGroup(
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

    public String getFutureTithingBalance() {
        return format(
                snapshot.getBaseCurrencyUnit(), toDecimal(snapshot.getFutureTithingBalance()));
    }
}
