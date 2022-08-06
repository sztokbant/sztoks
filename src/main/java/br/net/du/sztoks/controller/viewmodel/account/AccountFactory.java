package br.net.du.sztoks.controller.viewmodel.account;

import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.account.FutureTithingCapable;
import br.net.du.sztoks.model.account.FutureTithingPolicy;
import java.lang.reflect.Constructor;
import org.joda.money.CurrencyUnit;

public class AccountFactory {
    static Account newInstance(
            final String subtypeName,
            final String name,
            final CurrencyUnit currencyUnit,
            final FutureTithingPolicy futureTithingPolicy) {
        final String packageName = Account.class.getPackage().getName();
        try {
            final Class<? extends Account> clazz =
                    Class.forName(String.format("%s.%s", packageName, subtypeName))
                            .asSubclass(Account.class);

            if (FutureTithingCapable.class.isAssignableFrom(clazz)) {
                final Constructor constructor =
                        clazz.getDeclaredConstructor(
                                String.class, CurrencyUnit.class, FutureTithingPolicy.class);
                return (Account) constructor.newInstance(name, currencyUnit, futureTithingPolicy);
            } else {
                final Constructor constructor =
                        clazz.getDeclaredConstructor(String.class, CurrencyUnit.class);
                return (Account) constructor.newInstance(name, currencyUnit);
            }
        } catch (final Exception e) {
            throw new RuntimeException("Account creation error", e);
        }
    }
}
