package br.net.du.myequity.model.totals;

import static br.net.du.myequity.model.util.ModelConstants.DIVISION_SCALE;
import static br.net.du.myequity.model.util.ModelConstants.ONE_HUNDRED;

import br.net.du.myequity.model.Snapshot;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import org.joda.money.CurrencyUnit;

@Getter
public class CumulativeTransactionTotalsImpl implements CumulativeTransactionTotals {
    private final String baseCurrency;
    private BigDecimal incomesTotal;
    private BigDecimal investmentsTotal;
    private BigDecimal donationsTotal;
    private BigDecimal taxDeductibleDonationsTotal;
    private BigDecimal investmentAvg;
    private BigDecimal donationAvg;

    public CumulativeTransactionTotalsImpl(
            @NonNull final Snapshot snapshot,
            @NonNull final List<CumulativeTransactionTotals> cumulativeTransactionTotalsList) {
        baseCurrency = snapshot.getBaseCurrencyUnit().getCode();

        incomesTotal = BigDecimal.ZERO;
        investmentsTotal = BigDecimal.ZERO;
        donationsTotal = BigDecimal.ZERO;
        taxDeductibleDonationsTotal = BigDecimal.ZERO;
        investmentAvg = BigDecimal.ZERO;
        donationAvg = BigDecimal.ZERO;

        cumulativeTransactionTotalsList.stream().forEach(t -> add(snapshot, t));
    }

    private void add(final Snapshot snapshot, final CumulativeTransactionTotals other) {
        if (!baseCurrency.equals(snapshot.getBaseCurrencyUnit().getCode())) {
            throw new IllegalArgumentException(
                    "Base currency mismatch: "
                            + baseCurrency
                            + ", "
                            + snapshot.getBaseCurrencyUnit().getCode());
        }

        final CurrencyUnit otherBaseCurrency = CurrencyUnit.of(other.getBaseCurrency());

        if (other.getIncomesTotal() != null) {
            incomesTotal =
                    incomesTotal.add(
                            snapshot.toBaseCurrency(otherBaseCurrency, other.getIncomesTotal()));
        }

        if (other.getInvestmentsTotal() != null) {
            investmentsTotal =
                    investmentsTotal.add(
                            snapshot.toBaseCurrency(
                                    otherBaseCurrency, other.getInvestmentsTotal()));
        }

        if (other.getDonationsTotal() != null) {
            donationsTotal =
                    donationsTotal.add(
                            snapshot.toBaseCurrency(otherBaseCurrency, other.getDonationsTotal()));
        }

        if (other.getTaxDeductibleDonationsTotal() != null) {
            taxDeductibleDonationsTotal =
                    taxDeductibleDonationsTotal.add(
                            snapshot.toBaseCurrency(
                                    otherBaseCurrency, other.getTaxDeductibleDonationsTotal()));
        }

        investmentAvg =
                incomesTotal.compareTo(BigDecimal.ZERO) == 0
                        ? BigDecimal.ZERO
                        : investmentsTotal
                                .multiply(ONE_HUNDRED)
                                .divide(incomesTotal, DIVISION_SCALE, RoundingMode.HALF_UP);

        donationAvg =
                incomesTotal.compareTo(BigDecimal.ZERO) == 0
                        ? BigDecimal.ZERO
                        : donationsTotal
                                .multiply(ONE_HUNDRED)
                                .divide(incomesTotal, DIVISION_SCALE, RoundingMode.HALF_UP);
    }
}
