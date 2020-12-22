package br.net.du.myequity.util;

import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

public class NetWorthUtil {

    public static Map<CurrencyUnit, BigDecimal> computeByCurrency(
            final Set<AccountSnapshot> accountBalances) {
        return accountBalances.stream()
                .map(
                        entry ->
                                Money.of(
                                        entry.getAccount().getCurrencyUnit(),
                                        entry.getAccount()
                                                        .getAccountType()
                                                        .equals(AccountType.ASSET)
                                                ? entry.getTotal()
                                                : entry.getTotal().negate(),
                                        RoundingMode.HALF_UP))
                .collect(
                        Collectors.groupingBy(
                                Money::getCurrencyUnit,
                                Collectors.reducing(
                                        BigDecimal.ZERO, Money::getAmount, BigDecimal::add)));
    }
}
