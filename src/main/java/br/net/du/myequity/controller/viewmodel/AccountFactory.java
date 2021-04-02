package br.net.du.myequity.controller.viewmodel;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.TithingAccount;
import java.lang.reflect.Constructor;
import org.joda.money.CurrencyUnit;

public class AccountFactory {
    static Account newInstance(
            final String typeName, final String name, final CurrencyUnit currencyUnit) {
        if (TithingAccount.class.getSimpleName().equals(typeName)) {
            throw new RuntimeException(typeName + " not permitted");
        }

        final String packageName = Account.class.getPackage().getName();
        try {
            final Class<? extends Account> clazz =
                    Class.forName(String.format("%s.%s", packageName, typeName))
                            .asSubclass(Account.class);
            final Constructor constructor =
                    clazz.getDeclaredConstructor(String.class, CurrencyUnit.class);
            return (Account) constructor.newInstance(name, currencyUnit);
        } catch (final Exception e) {
            throw new RuntimeException("Account creation error", e);
        }
    }
}
