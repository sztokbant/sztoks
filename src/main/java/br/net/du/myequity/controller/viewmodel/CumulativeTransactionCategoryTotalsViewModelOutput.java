package br.net.du.myequity.controller.viewmodel;

import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

import br.net.du.myequity.model.totals.CumulativeTransactionCategoryTotals;
import br.net.du.myequity.model.transaction.DonationTransaction;
import br.net.du.myequity.model.transaction.IncomeTransaction;
import br.net.du.myequity.model.transaction.InvestmentTransaction;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.joda.money.CurrencyUnit;

public class CumulativeTransactionCategoryTotalsViewModelOutput {

    @RequiredArgsConstructor
    @Getter
    static class CategoryAmountViewModelOutput
            implements Comparable<CategoryAmountViewModelOutput> {
        private final String category;
        private final String currency;
        private final String amount;

        @Override
        public int compareTo(
                @NonNull
                        final CumulativeTransactionCategoryTotalsViewModelOutput
                                        .CategoryAmountViewModelOutput
                                other) {
            if (category.equals(other.getCategory())) {
                return currency.compareTo(other.getCurrency());
            }
            return category.compareTo(other.getCategory());
        }
    }

    private final SortedSet<CategoryAmountViewModelOutput> incomeCategories;
    private final SortedSet<CategoryAmountViewModelOutput> investmentCategories;
    private final SortedSet<CategoryAmountViewModelOutput> donationCategories;

    public CumulativeTransactionCategoryTotalsViewModelOutput(
            @NonNull final List<CumulativeTransactionCategoryTotals> categoryTotals) {

        incomeCategories = new TreeSet<>();
        investmentCategories = new TreeSet<>();
        donationCategories = new TreeSet<>();

        categoryTotals.stream()
                .forEach(
                        totals -> {
                            if (IncomeTransaction.TRANSACTION_TYPE.equals(
                                    totals.getTransactionType())) {
                                incomeCategories.add(toCategoryAmountViewModelOutput(totals));
                            } else if (InvestmentTransaction.TRANSACTION_TYPE.equals(
                                    totals.getTransactionType())) {
                                investmentCategories.add(toCategoryAmountViewModelOutput(totals));
                            } else if (DonationTransaction.TRANSACTION_TYPE.equals(
                                    totals.getTransactionType())) {
                                donationCategories.add(toCategoryAmountViewModelOutput(totals));
                            } else {
                                throw new IllegalStateException(
                                        "Unknown transaction type: " + totals.getTransactionType());
                            }
                        });
    }

    private CategoryAmountViewModelOutput toCategoryAmountViewModelOutput(
            @NonNull final CumulativeTransactionCategoryTotals totals) {
        return new CategoryAmountViewModelOutput(
                totals.getCategory(),
                totals.getCurrency(),
                format(CurrencyUnit.of(totals.getCurrency()), totals.getAmount()));
    }
}
