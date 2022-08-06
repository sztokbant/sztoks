package br.net.du.sztoks.model.totals;

import java.math.BigDecimal;

public interface CumulativeTransactionTotals {
    String getBaseCurrency();

    BigDecimal getIncomesTotal();

    BigDecimal getInvestmentsTotal();

    BigDecimal getDonationsTotal();

    BigDecimal getTaxDeductibleDonationsTotal();

    BigDecimal getInvestmentAvg();

    BigDecimal getDonationAvg();
}
