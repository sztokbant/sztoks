package br.net.du.myequity.model.totals;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.joda.money.CurrencyUnit;

@AllArgsConstructor
@Data
public class InvestmentsTotal {

    private final CurrencyUnit currencyUnit;
    private final BigDecimal amountInvested;
    private final BigDecimal profitPercentage;
    private final BigDecimal balance;
}
