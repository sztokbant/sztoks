package br.net.du.myequity.model.totals;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.FutureTithingAccount;
import br.net.du.myequity.model.account.GiftCertificateAccount;
import br.net.du.myequity.model.account.PayableAccount;
import br.net.du.myequity.model.account.ReceivableAccount;
import br.net.du.myequity.model.account.SharedBillPayableAccount;
import br.net.du.myequity.model.account.SharedBillReceivableAccount;
import br.net.du.myequity.model.account.SimpleAssetAccount;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
import br.net.du.myequity.model.account.TithingAccount;
import java.util.HashSet;
import java.util.Set;

public enum AccountSubtypeDisplayGroup {
    SIMPLE_ASSET(new Class[] {SimpleAssetAccount.class}),
    GIFT_CERTIFICATE(new Class[] {GiftCertificateAccount.class}),
    RECEIVABLE(new Class[] {ReceivableAccount.class}),
    SHARED_BILL_RECEIVABLE(new Class[] {SharedBillReceivableAccount.class}),
    TITHING((new Class[] {TithingAccount.class, FutureTithingAccount.class})),
    SIMPLE_LIABILITY((new Class[] {SimpleLiabilityAccount.class})),
    PAYABLE(new Class[] {PayableAccount.class}),
    SHARED_BILL_PAYABLE(new Class[] {SharedBillPayableAccount.class});

    private final Set<Class> classes = new HashSet<>();

    AccountSubtypeDisplayGroup(final Class<? extends Account>[] classes) {
        for (final Class clazz : classes) {
            this.classes.add(clazz);
        }
    }

    public boolean accepts(final Class<? extends Account> clazz) {
        return classes.contains(clazz);
    }

    public static AccountSubtypeDisplayGroup forClass(final Class<? extends Account> clazz) {
        if (clazz.equals(SimpleAssetAccount.class)) {
            return SIMPLE_ASSET;
        }

        if (clazz.equals(GiftCertificateAccount.class)) {
            return GIFT_CERTIFICATE;
        }

        if (clazz.equals(ReceivableAccount.class)) {
            return RECEIVABLE;
        }

        if (clazz.equals(SharedBillReceivableAccount.class)) {
            return SHARED_BILL_RECEIVABLE;
        }

        if (clazz.equals(TithingAccount.class) || clazz.equals(FutureTithingAccount.class)) {
            return TITHING;
        }

        if (clazz.equals(SimpleLiabilityAccount.class)) {
            return SIMPLE_LIABILITY;
        }

        if (clazz.equals(PayableAccount.class)) {
            return PAYABLE;
        }

        if (clazz.equals(SharedBillPayableAccount.class)) {
            return SHARED_BILL_PAYABLE;
        }

        return null;
    }
}
