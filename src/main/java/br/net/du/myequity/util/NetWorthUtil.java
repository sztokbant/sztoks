package br.net.du.myequity.util;

import br.net.du.myequity.model.Account;
import br.net.du.myequity.model.AccountType;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NetWorthUtil {

    public static Map<CurrencyUnit, BigDecimal> computeByCurrency(final Set<Map.Entry<Account, BigDecimal>> accountBalances) {
        return accountBalances.stream()
                              .map(entry -> Money.of(entry.getKey().getCurrencyUnit(),
                                                     entry.getKey().getAccountType().equals(AccountType.ASSET) ?
                                                             entry.getValue() :
                                                             entry.getValue().negate()))
                              .collect(Collectors.groupingBy(Money::getCurrencyUnit,
                                                             Collectors.reducing(BigDecimal.ZERO,
                                                                                 Money::getAmount,
                                                                                 BigDecimal::add)));
    }
}
