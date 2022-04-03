package br.net.du.myequity.controller.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.Test;

class MoneyFormatUtilsTest {
    @Test
    public void format_usdBeyondMaxDecimalPlaces_roundsUp() {
        // GIVEN
        final String currencyCode = "USD";
        final String amount = "0.0450";

        // WHEN
        final String formatted =
                MoneyFormatUtils.format(CurrencyUnit.of(currencyCode), new BigDecimal(amount));

        // THEN
        assertEquals("$0.05", formatted);
    }

    @Test
    public void format_xxxBeyondMaxDecimalPlaces_roundsUp() {
        // GIVEN
        final String currencyCode = "XXX";
        final String amount = "0.9999";

        // WHEN
        final String formatted =
                MoneyFormatUtils.format(CurrencyUnit.of(currencyCode), new BigDecimal(amount));

        // THEN
        assertEquals("XXX1", formatted);
    }
}
