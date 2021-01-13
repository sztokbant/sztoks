package br.net.du.myequity.util;

import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.transaction.Transaction;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

public class NetWorthUtils {

    public static Map<CurrencyUnit, BigDecimal> breakDownAccountSnapshotsByCurrency(
            final Set<AccountSnapshot> accountBalances) {
        return accountBalances.stream()
                .map(
                        entry -> {
                            final BigDecimal amount =
                                    entry.getAccount().getAccountType().equals(AccountType.ASSET)
                                            ? entry.getTotal()
                                            : entry.getTotal().negate();
                            return Money.of(
                                    entry.getAccount().getCurrencyUnit(),
                                    amount,
                                    RoundingMode.HALF_UP);
                        })
                .collect(getAmountByCurrencyCollector());
    }

    public static Map<CurrencyUnit, BigDecimal> breakDownTransactionsByCurrency(
            final Set<Transaction> transactions) {
        return transactions.stream()
                .map(
                        entry ->
                                Money.of(
                                        entry.getCurrencyUnit(),
                                        entry.getAmount(),
                                        RoundingMode.HALF_UP))
                .collect(getAmountByCurrencyCollector());
    }

    private static Collector<Money, ?, Map<CurrencyUnit, BigDecimal>>
            getAmountByCurrencyCollector() {
        return Collectors.groupingBy(
                Money::getCurrencyUnit,
                Collectors.reducing(BigDecimal.ZERO, Money::getAmount, BigDecimal::add));
    }
}
