package br.net.du.myequity.test;

import static br.net.du.myequity.test.TestConstants.FIRST_SNAPSHOT_NAME;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.CreditCardAccount;
import br.net.du.myequity.model.account.InvestmentAccount;
import br.net.du.myequity.model.account.PayableAccount;
import br.net.du.myequity.model.account.ReceivableAccount;
import br.net.du.myequity.model.account.SimpleAssetAccount;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
import br.net.du.myequity.model.account.TithingAccount;
import br.net.du.myequity.model.transaction.DonationTransaction;
import br.net.du.myequity.model.transaction.IncomeTransaction;
import br.net.du.myequity.model.transaction.InvestmentTransaction;
import br.net.du.myequity.model.transaction.Transaction;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

public class ModelTestUtils {

    public static final long SNAPSHOT_ID = 99L;

    public static User buildUser() {
        final String email = TestConstants.EMAIL;
        final String firstName = TestConstants.FIRST_NAME;
        final String lastName = TestConstants.LAST_NAME;
        final Long id = 42L;

        final User user = new User(email, firstName, lastName);
        user.setId(id);

        final Snapshot snapshot =
                new Snapshot(FIRST_SNAPSHOT_NAME, ImmutableSortedSet.of(), ImmutableList.of());
        snapshot.setId(SNAPSHOT_ID);

        user.addSnapshot(snapshot);

        return user;
    }

    public static boolean equalsIgnoreId(final SimpleAssetAccount a1, final SimpleAssetAccount a2) {
        return a1.getName().equals(a2.getName()) && a1.getAmount().compareTo(a2.getAmount()) == 0;
    }

    public static boolean equalsIgnoreId(final ReceivableAccount a1, final ReceivableAccount a2) {
        return a1.getName().equals(a2.getName()) && a1.getAmount().compareTo(a2.getAmount()) == 0;
    }

    public static boolean equalsIgnoreId(final InvestmentAccount a1, final InvestmentAccount a2) {
        return a1.getName().equals(a2.getName())
                && (a1.getShares().compareTo(a2.getShares()) == 0)
                && (a1.getAmountInvested().compareTo(a2.getAmountInvested()) == 0)
                && (a1.getCurrentShareValue().compareTo(a2.getCurrentShareValue()) == 0);
    }

    public static boolean equalsIgnoreId(final TithingAccount a1, final TithingAccount a2) {
        return a1.getName().equals(a2.getName());
    }

    public static boolean equalsIgnoreId(
            final SimpleLiabilityAccount a1, final SimpleLiabilityAccount a2) {
        return a1.getName().equals(a2.getName()) && a1.getAmount().compareTo(a2.getAmount()) == 0;
    }

    public static boolean equalsIgnoreId(final PayableAccount a1, final PayableAccount a2) {
        return a1.getName().equals(a2.getName()) && a1.getAmount().compareTo(a2.getAmount()) == 0;
    }

    public static boolean equalsIgnoreId(final CreditCardAccount a1, final CreditCardAccount a2) {
        return a1.getName().equals(a2.getName())
                && (a1.getTotalCredit().compareTo(a2.getTotalCredit()) == 0)
                && (a1.getAvailableCredit().compareTo(a2.getAvailableCredit()) == 0)
                && (a1.getStatement().compareTo(a2.getStatement()) == 0);
    }

    public static boolean equalsIgnoreId(final Transaction t1, final Transaction t2) {
        return t1.getDate().equals(t2.getDate()) && equalsIgnoreIdAndDate(t1, t2);
    }

    public static boolean equalsIgnoreIdAndDate(final Transaction t1, final Transaction t2) {
        return t1.getClass().equals(t2.getClass())
                && t1.getCurrency().equals(t2.getCurrency())
                && t1.getAmount().equals(t2.getAmount())
                && t1.getDescription().equals(t2.getDescription())
                && t1.isRecurring() == t2.isRecurring();
    }

    public static boolean equalsIgnoreIdAndDate(
            final IncomeTransaction t1, final IncomeTransaction t2) {
        return equalsIgnoreIdAndDate((Transaction) t1, (Transaction) t2)
                && t1.getDonationRatio().equals(t2.getDonationRatio());
    }

    public static boolean equalsIgnoreIdAndDate(
            final InvestmentTransaction t1, final InvestmentTransaction t2) {
        return equalsIgnoreIdAndDate((Transaction) t1, (Transaction) t2)
                && t1.getInvestmentCategory().equals(t2.getInvestmentCategory());
    }

    public static boolean equalsIgnoreIdAndDate(
            final DonationTransaction t1, final DonationTransaction t2) {
        return equalsIgnoreIdAndDate((Transaction) t1, (Transaction) t2)
                && t1.isTaxDeductible() == t2.isTaxDeductible();
    }
}
