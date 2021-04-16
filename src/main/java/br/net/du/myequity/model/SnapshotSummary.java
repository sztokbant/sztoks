package br.net.du.myequity.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.joda.money.CurrencyUnit;

@RequiredArgsConstructor
@Data
public class SnapshotSummary {
    private final Long userId;

    private final Long id;
    private final Integer year;
    private final Integer month;

    private final String baseCurrency;
    private final BigDecimal assetsTotal;
    private final BigDecimal liabilitiesTotal;

    public CurrencyUnit getBaseCurrencyUnit() {
        return CurrencyUnit.of(baseCurrency);
    }

    public BigDecimal getNetWorth() {
        if (assetsTotal == null || liabilitiesTotal == null) {
            return BigDecimal.ZERO;
        }
        return assetsTotal.subtract(liabilitiesTotal).setScale(2, RoundingMode.HALF_UP);
    }
}
