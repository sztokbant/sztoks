package br.net.du.sztoks.model.totals;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.joda.money.CurrencyUnit;

@AllArgsConstructor
@Data
public class BalanceUpdatableSubtypeTotal {

    private final CurrencyUnit currencyUnit;
    private final BigDecimal totalBalance;
}
