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

    public static Map<CurrencyUnit, BigDecimal> computeByCurrency(final Set<Account> accounts) {
        return accounts.stream()
                       .map(account -> account.getAccountType().equals(AccountType.ASSET) ?
                               account.getBalance() :
                               Money.of(account.getBalance().getCurrencyUnit(),
                                        account.getBalance().getAmount().negate()))
                       .collect(Collectors.groupingBy(Money::getCurrencyUnit,
                                                      Collectors.reducing(BigDecimal.ZERO,
                                                                          Money::getAmount,
                                                                          BigDecimal::add)));
    }

    public static Map<CurrencyUnit, BigDecimal> computeByCurrency(final Map<Account, BigDecimal> accounts) {
        return accounts.entrySet()
                       .stream()
                       .map(account -> account.getKey().getAccountType().equals(AccountType.ASSET) ?
                               Money.of(account.getKey().getBalance().getCurrencyUnit(), account.getValue()) :
                               Money.of(account.getKey().getBalance().getCurrencyUnit(), account.getValue().negate()))
                       .collect(Collectors.groupingBy(Money::getCurrencyUnit,
                                                      Collectors.reducing(BigDecimal.ZERO,
                                                                          Money::getAmount,
                                                                          BigDecimal::add)));
    }
}
