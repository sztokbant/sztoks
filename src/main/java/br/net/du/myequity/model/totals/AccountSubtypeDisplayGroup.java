package br.net.du.myequity.model.totals;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.CreditCardAccount;
import br.net.du.myequity.model.account.FutureTithingAccount;
import br.net.du.myequity.model.account.GiftCertificateAccount;
import br.net.du.myequity.model.account.InvestmentAccount;
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
    SIMPLE_ASSET(new Class[] {SimpleAssetAccount.class}, true),
    GIFT_CERTIFICATE(new Class[] {GiftCertificateAccount.class}, true),
    RECEIVABLE(new Class[] {ReceivableAccount.class}, true),
    SHARED_BILL_RECEIVABLE(new Class[] {SharedBillReceivableAccount.class}, true),
    INVESTMENT(new Class[] {InvestmentAccount.class}, false),
    TITHING((new Class[] {TithingAccount.class, FutureTithingAccount.class}), true),
    SIMPLE_LIABILITY((new Class[] {SimpleLiabilityAccount.class}), true),
    PAYABLE(new Class[] {PayableAccount.class}, true),
    SHARED_BILL_PAYABLE(new Class[] {SharedBillPayableAccount.class}, true),
    CREDIT_CARD(new Class[] {CreditCardAccount.class}, false);

    private final Set<Class> classes = new HashSet<>();

    // For INVESTMENT and CREDIT_CARD accounts, totals are computed separately.
    private final boolean useDefaultTotals;

    AccountSubtypeDisplayGroup(
            final Class<? extends Account>[] classes, final boolean useDefaultTotals) {
        for (final Class clazz : classes) {
            this.classes.add(clazz);
        }
        this.useDefaultTotals = useDefaultTotals;
    }

    public boolean useDefaultTotals() {
        return useDefaultTotals;
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

        if (clazz.equals(InvestmentAccount.class)) {
            return INVESTMENT;
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

        if (clazz.equals(CreditCardAccount.class)) {
            return CREDIT_CARD;
        }

        throw new IllegalArgumentException(
                String.format("Unexpected class: %s", clazz.getCanonicalName()));
    }
}
