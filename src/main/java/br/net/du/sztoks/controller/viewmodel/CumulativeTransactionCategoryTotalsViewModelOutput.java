package br.net.du.sztoks.controller.viewmodel;

import static br.net.du.sztoks.controller.util.MoneyFormatUtils.format;

import br.net.du.sztoks.model.totals.CumulativeTransactionCategoryTotals;
import br.net.du.sztoks.model.transaction.DonationTransaction;
import br.net.du.sztoks.model.transaction.IncomeTransaction;
import br.net.du.sztoks.model.transaction.InvestmentTransaction;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.joda.money.CurrencyUnit;

@Getter
public class CumulativeTransactionCategoryTotalsViewModelOutput {

    private enum Period {
        YTD,
        TWELVE_MONTHS
    }

    @Getter
    @Setter
    public static class PeriodAmounts {
        private String ytd = "-";
        private String twelveMonths = "-";
    }

    private final SortedMap<String, SortedMap<String, PeriodAmounts>> incomeCategories;
    private final SortedMap<String, SortedMap<String, PeriodAmounts>> investmentCategories;
    private final SortedMap<String, SortedMap<String, PeriodAmounts>> donationCategories;

    public CumulativeTransactionCategoryTotalsViewModelOutput(
            @NonNull final List<CumulativeTransactionCategoryTotals> ytdCategoryTotals,
            @NonNull final List<CumulativeTransactionCategoryTotals> twelveMonthCategoryTotals) {

        incomeCategories = new TreeMap<>();
        investmentCategories = new TreeMap<>();
        donationCategories = new TreeMap<>();

        processTransactionCategories(ytdCategoryTotals, Period.YTD);
        processTransactionCategories(twelveMonthCategoryTotals, Period.TWELVE_MONTHS);
    }

    private void processTransactionCategories(
            @NonNull final List<CumulativeTransactionCategoryTotals> categoryTotals,
            @NonNull final Period period) {
        categoryTotals.stream()
                .forEach(
                        totals -> {
                            if (IncomeTransaction.TRANSACTION_TYPE.equals(
                                    totals.getTransactionType())) {
                                insert(incomeCategories, totals, period);
                            } else if (InvestmentTransaction.TRANSACTION_TYPE.equals(
                                    totals.getTransactionType())) {
                                insert(investmentCategories, totals, period);
                            } else if (DonationTransaction.TRANSACTION_TYPE.equals(
                                    totals.getTransactionType())) {
                                insert(donationCategories, totals, period);
                            } else {
                                throw new IllegalStateException(
                                        "Unknown transaction type: " + totals.getTransactionType());
                            }
                        });
    }

    private void insert(
            @NonNull final SortedMap<String, SortedMap<String, PeriodAmounts>> categories,
            @NonNull final CumulativeTransactionCategoryTotals totals,
            @NonNull final Period period) {

        if (!categories.containsKey(totals.getCategory())) {
            categories.put(totals.getCategory(), new TreeMap<>());
        }

        final SortedMap<String, PeriodAmounts> byCurrency = categories.get(totals.getCategory());
        if (!byCurrency.containsKey(totals.getCurrency())) {
            byCurrency.put(totals.getCurrency(), new PeriodAmounts());
        }

        final PeriodAmounts periodAmounts = byCurrency.get(totals.getCurrency());

        final String formattedAmount =
                format(CurrencyUnit.of(totals.getCurrency()), totals.getAmount());
        if (period.equals(Period.YTD)) {
            periodAmounts.setYtd(formattedAmount);
        } else if (period.equals(Period.TWELVE_MONTHS)) {
            periodAmounts.setTwelveMonths(formattedAmount);
        } else {
            throw new IllegalStateException("Unknown period: " + period.name());
        }
    }
}
