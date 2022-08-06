package br.net.du.sztoks.controller.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.Test;

class MoneyFormatUtilsTest {
    @Test
    public void format_usdBeyondMaxDecimalPlaces_roundsUp() {
        // GIVEN
        final CurrencyUnit usdCurrency = CurrencyUnit.USD;
        final String amount = "0.0450";

        // WHEN
        final String formatted = MoneyFormatUtils.format(usdCurrency, new BigDecimal(amount));

        // THEN
        assertEquals(usdCurrency.getSymbol() + "0.05", formatted);
    }

    @Test
    public void format_xxxBeyondMaxDecimalPlaces_roundsUp() {
        // GIVEN
        final CurrencyUnit xxxCurrency = CurrencyUnit.of("XXX");
        final String amount = "0.9999";

        // WHEN
        final String formatted = MoneyFormatUtils.format(xxxCurrency, new BigDecimal(amount));

        // THEN
        assertEquals(xxxCurrency.getSymbol() + "1", formatted);
    }
}
