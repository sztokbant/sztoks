package br.net.du.myequity.controller.viewmodel;

import br.net.du.myequity.model.account.Account;
import org.joda.money.CurrencyUnit;

import java.lang.reflect.Constructor;
import java.time.LocalDate;

public class AccountFactory {
    static Account newInstance(final String typeName,
                               final String name,
                               final CurrencyUnit currencyUnit,
                               final LocalDate date) {
        final String packageName = Account.class.getPackage().getName();
        try {
            final Class<? extends Account> clazz =
                    Class.forName(String.format("%s.%s", packageName, typeName)).asSubclass(Account.class);
            final Constructor constructor =
                    clazz.getDeclaredConstructor(String.class, CurrencyUnit.class, LocalDate.class);
            return (Account) constructor.newInstance(name, currencyUnit, date);
        } catch (final Exception e) {
            throw new RuntimeException("Account creation error", e);
        }
    }
}
