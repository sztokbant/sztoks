package br.net.du.sztoks.controller.util;

import com.google.common.collect.ImmutableMap;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import lombok.NonNull;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.money.format.MoneyAmountStyle;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;

public class MoneyFormatUtils {
    private static final MoneyFormatter BRL_FORMATTER =
            new MoneyFormatterBuilder()
                    .appendLiteral("R$ ")
                    .appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA)
                    .toFormatter();

    private static final MoneyFormatter BTC_FORMATTER =
            new MoneyFormatterBuilder()
                    .appendLiteral("₿ ")
                    .appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_NO_GROUPING)
                    .toFormatter();

    private static final MoneyFormatter CAD_FORMATTER =
            new MoneyFormatterBuilder()
                    .appendLiteral("C$ ")
                    .appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA)
                    .toFormatter();

    private static final MoneyFormatter EUR_FORMATTER =
            new MoneyFormatterBuilder()
                    .appendLiteral("€")
                    .appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA)
                    .toFormatter();

    private static final MoneyFormatter USD_FORMATTER =
            new MoneyFormatterBuilder()
                    .appendLiteral("$")
                    .appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA)
                    .toFormatter();

    private static final MoneyFormatter DEFAULT_FORMATTER =
            new MoneyFormatterBuilder()
                    .appendCurrencySymbolLocalized()
                    .appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA)
                    .toFormatter();

    private static final Map<CurrencyUnit, MoneyFormatter> MONEY_FORMATTERS =
            ImmutableMap.of(
                    CurrencyUnit.of("BRL"),
                    BRL_FORMATTER,
                    CurrencyUnit.of("BTC"),
                    BTC_FORMATTER,
                    CurrencyUnit.CAD,
                    CAD_FORMATTER,
                    CurrencyUnit.EUR,
                    EUR_FORMATTER,
                    CurrencyUnit.USD,
                    USD_FORMATTER);

    public static String format(
            @NonNull final CurrencyUnit currencyUnit, @NonNull final BigDecimal amount) {
        final MoneyFormatter moneyFormatter =
                MONEY_FORMATTERS.containsKey(currencyUnit)
                        ? MONEY_FORMATTERS.get(currencyUnit)
                        : DEFAULT_FORMATTER;

        return moneyFormatter.print(
                Money.of(
                        currencyUnit,
                        amount.setScale(currencyUnit.getDecimalPlaces(), RoundingMode.HALF_UP)));
    }
}
