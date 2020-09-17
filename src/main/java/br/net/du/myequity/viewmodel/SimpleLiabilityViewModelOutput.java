package br.net.du.myequity.viewmodel;

import java.math.BigDecimal;

public class SimpleLiabilityViewModelOutput extends AccountViewModelOutputBase {
    public SimpleLiabilityViewModelOutput(final Long id,
                                          final String name,
                                          final boolean isClosed,
                                          final String currencyUnit,
                                          final String currencyUnitSymbol,
                                          final BigDecimal total) {
        super(id, name, isClosed, currencyUnit, currencyUnitSymbol, total);
    }
}
