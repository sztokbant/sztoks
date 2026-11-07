package br.net.du.sztoks.model.totals;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.joda.money.CurrencyUnit;

@AllArgsConstructor
@Data
public class AdditionalCurrenciesTotal {

    private final CurrencyUnit currencyUnit;
    private final BigDecimal shares;
    private final BigDecimal amountInvested;

    /** Combined average purchase price in snapshot base: amountInvested / shares. */
    private final BigDecimal averagePurchasePrice;

    private final BigDecimal profitPercentage;
    private final BigDecimal balance;
}
