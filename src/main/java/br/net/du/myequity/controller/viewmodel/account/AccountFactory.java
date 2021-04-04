package br.net.du.myequity.controller.viewmodel.account;

import br.net.du.myequity.model.account.Account;
import java.lang.reflect.Constructor;
import org.joda.money.CurrencyUnit;

public class AccountFactory {
    static Account newInstance(
            final String subtypeName, final String name, final CurrencyUnit currencyUnit) {
        final String packageName = Account.class.getPackage().getName();
        try {
            final Class<? extends Account> clazz =
                    Class.forName(String.format("%s.%s", packageName, subtypeName))
                            .asSubclass(Account.class);
            final Constructor constructor =
                    clazz.getDeclaredConstructor(String.class, CurrencyUnit.class);
            return (Account) constructor.newInstance(name, currencyUnit);
        } catch (final Exception e) {
            throw new RuntimeException("Account creation error", e);
        }
    }
}
