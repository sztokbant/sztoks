package br.net.du.myequity.controller.viewmodel;

import br.net.du.myequity.model.snapshot.AccountSnapshot;
import java.lang.reflect.Constructor;
import java.time.LocalDate;
import org.joda.money.CurrencyUnit;

public class AccountFactory {
    static AccountSnapshot newInstance(
            final String typeName, final String name, final CurrencyUnit currencyUnit) {
        final String packageName = AccountSnapshot.class.getPackage().getName();
        try {
            final Class<? extends AccountSnapshot> clazz =
                    Class.forName(String.format("%s.%s", packageName, typeName))
                            .asSubclass(AccountSnapshot.class);
            final Constructor constructor =
                    clazz.getDeclaredConstructor(String.class, CurrencyUnit.class, LocalDate.class);
            return (AccountSnapshot) constructor.newInstance(name, currencyUnit);
        } catch (final Exception e) {
            throw new RuntimeException("Account creation error", e);
        }
    }
}
