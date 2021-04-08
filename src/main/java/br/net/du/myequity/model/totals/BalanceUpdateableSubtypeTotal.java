package br.net.du.myequity.model.totals;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.joda.money.CurrencyUnit;

@AllArgsConstructor
@Data
public class BalanceUpdateableSubtypeTotal {

    private final CurrencyUnit currencyUnit;
    private final BigDecimal totalBalance;
}
