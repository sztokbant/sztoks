package br.net.du.myequity.model.totals;

import java.math.BigDecimal;

public interface CumulativeTransactionCategoryTotals {
    String getTransactionType();

    String getCategory();

    String getCurrency();

    BigDecimal getAmount();
}
