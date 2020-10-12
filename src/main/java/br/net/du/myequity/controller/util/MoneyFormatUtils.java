package br.net.du.myequity.controller.util;

import com.google.common.collect.ImmutableMap;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.money.format.MoneyAmountStyle;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;

import java.math.BigDecimal;
import java.util.Map;

public class MoneyFormatUtils {
    private static final MoneyFormatter BRL_FORMATTER = new MoneyFormatterBuilder().appendCurrencySymbolLocalized()
                                                                                   .appendLiteral(" ")
                                                                                   .appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA)
                                                                                   .toFormatter();

    private static final MoneyFormatter DEFAULT_FORMATTER = new MoneyFormatterBuilder().appendCurrencySymbolLocalized()
                                                                                       .appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA)
                                                                                       .toFormatter();
    private static final Map<CurrencyUnit, MoneyFormatter> MONEY_FORMATTERS =
            ImmutableMap.of(CurrencyUnit.of("BRL"), BRL_FORMATTER, CurrencyUnit.USD, DEFAULT_FORMATTER);

    public static String format(final CurrencyUnit currencyUnit, final BigDecimal amount) {
        final MoneyFormatter moneyFormatter = MONEY_FORMATTERS.containsKey(currencyUnit) ?
                MONEY_FORMATTERS.get(currencyUnit) :
                DEFAULT_FORMATTER;

        return moneyFormatter.print(Money.of(currencyUnit, amount));
    }
}
