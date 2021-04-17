package br.net.du.myequity.model.totals;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.FutureTithingAccount;
import br.net.du.myequity.model.account.PayableAccount;
import br.net.du.myequity.model.account.ReceivableAccount;
import br.net.du.myequity.model.account.SimpleAssetAccount;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
import br.net.du.myequity.model.account.TithingAccount;
import java.util.HashSet;
import java.util.Set;

public enum BalanceUpdateableSubtype {
    SIMPLE_ASSET(new Class[] {SimpleAssetAccount.class}),
    RECEIVABLE(new Class[] {ReceivableAccount.class}),
    SIMPLE_LIABILITY(
            (new Class[] {
                SimpleLiabilityAccount.class, TithingAccount.class, FutureTithingAccount.class
            })),
    PAYABLE(new Class[] {PayableAccount.class});

    private final Set<Class> classes = new HashSet<>();

    BalanceUpdateableSubtype(final Class<? extends Account>[] classes) {
        for (final Class clazz : classes) {
            this.classes.add(clazz);
        }
    }

    public boolean accepts(final Class<? extends Account> clazz) {
        return classes.contains(clazz);
    }

    public static BalanceUpdateableSubtype forClass(final Class<? extends Account> clazz) {
        if (clazz.equals(SimpleAssetAccount.class)) {
            return SIMPLE_ASSET;
        }

        if (clazz.equals(ReceivableAccount.class)) {
            return RECEIVABLE;
        }

        if (clazz.equals(SimpleLiabilityAccount.class)
                || clazz.equals(TithingAccount.class)
                || clazz.equals(FutureTithingAccount.class)) {
            return SIMPLE_LIABILITY;
        }

        if (clazz.equals(PayableAccount.class)) {
            return PAYABLE;
        }

        return null;
    }
}
