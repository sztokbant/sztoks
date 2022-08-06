package br.net.du.sztoks.model.totals;

import java.math.BigDecimal;

public interface CumulativeTransactionCategoryTotals {
    String getTransactionType();

    String getCategory();

    String getCurrency();

    BigDecimal getAmount();
}
