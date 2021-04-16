package br.net.du.myequity.controller.viewmodel;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsPercentage;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

import br.net.du.myequity.model.totals.CumulativeTransactionTotals;
import lombok.Getter;
import lombok.NonNull;
import org.joda.money.CurrencyUnit;

@Getter
public class CumulativeTransactionTotalsViewModelOutput {
    private final String incomesTotal;
    private final String investmentsTotal;
    private final String donationsTotal;
    private final String taxDeductibleDonationsTotal;
    private final String investmentAvg;
    private final String donationAvg;

    public CumulativeTransactionTotalsViewModelOutput(
            @NonNull final CumulativeTransactionTotals cumulativeTransactionTotals) {
        final CurrencyUnit baseCurrencyUnit =
                CurrencyUnit.of(cumulativeTransactionTotals.getBaseCurrency());

        incomesTotal = format(baseCurrencyUnit, cumulativeTransactionTotals.getIncomesTotal());
        investmentsTotal =
                format(baseCurrencyUnit, cumulativeTransactionTotals.getInvestmentsTotal());
        donationsTotal = format(baseCurrencyUnit, cumulativeTransactionTotals.getDonationsTotal());
        taxDeductibleDonationsTotal =
                format(
                        baseCurrencyUnit,
                        cumulativeTransactionTotals.getTaxDeductibleDonationsTotal());
        investmentAvg = formatAsPercentage(cumulativeTransactionTotals.getInvestmentAvg());
        donationAvg = formatAsPercentage(cumulativeTransactionTotals.getDonationAvg());
    }
}
