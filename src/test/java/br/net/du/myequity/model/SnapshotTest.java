package br.net.du.myequity.model;

import static br.net.du.myequity.test.TestConstants.SIMPLE_ASSET_SNAPSHOT;
import static br.net.du.myequity.test.TestConstants.SIMPLE_LIABILITY_SNAPSHOT;
import static br.net.du.myequity.test.TestConstants.newRecurringDonation;
import static br.net.du.myequity.test.TestConstants.newRecurringIncome;
import static br.net.du.myequity.test.TestConstants.newRecurringInvestment;
import static br.net.du.myequity.test.TestConstants.newSingleDonation;
import static br.net.du.myequity.test.TestConstants.newSingleIncome;
import static br.net.du.myequity.test.TestConstants.newSingleInvestment;
import static br.net.du.myequity.test.TestConstants.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.SimpleAssetSnapshot;
import br.net.du.myequity.model.transaction.DonationTransaction;
import br.net.du.myequity.model.transaction.IncomeTransaction;
import br.net.du.myequity.model.transaction.InvestmentTransaction;
import br.net.du.myequity.model.transaction.Transaction;
import br.net.du.myequity.model.transaction.TransactionType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.Test;

class SnapshotTest {

    private static final long SNAPSHOT_INDEX = 1L;

    private Map<CurrencyUnit, BigDecimal> EXPECTED_NET_WORTH =
            ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("7500.00"));

    @Test
    public void constructor() {
        // WHEN
        final IncomeTransaction recurringIncome = newRecurringIncome();
        final IncomeTransaction singleIncome = newSingleIncome();
        final DonationTransaction recurringDonation = newRecurringDonation();
        final DonationTransaction singleDonation = newSingleDonation();
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(SIMPLE_ASSET_SNAPSHOT, SIMPLE_LIABILITY_SNAPSHOT),
                        ImmutableList.of(
                                recurringIncome, singleIncome, recurringDonation, singleDonation));
        snapshot.setId(42L);

        // THEN
        assertEquals(SNAPSHOT_INDEX, snapshot.getIndex());
        assertEquals(now, snapshot.getName());

        assertNull(SIMPLE_ASSET_SNAPSHOT.getSnapshot());
        assertNull(SIMPLE_LIABILITY_SNAPSHOT.getSnapshot());

        assertEquals(2, snapshot.getAccountSnapshots().size());
        for (final AccountSnapshot accountSnapshot : snapshot.getAccountSnapshots()) {
            assertEquals(snapshot, accountSnapshot.getSnapshot());
        }

        assertEquals(EXPECTED_NET_WORTH, snapshot.getNetWorth());

        assertNull(recurringIncome.getSnapshot());
        assertNull(singleIncome.getSnapshot());
        assertNull(recurringDonation.getSnapshot());
        assertNull(singleDonation.getSnapshot());

        assertEquals(4, snapshot.getTransactions().size());
        for (final Transaction transaction : snapshot.getTransactions()) {
            assertEquals(snapshot, transaction.getSnapshot());
        }
    }

    @Test
    public void getNetWorth() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(SIMPLE_ASSET_SNAPSHOT, SIMPLE_LIABILITY_SNAPSHOT),
                        ImmutableList.of());

        // THEN
        assertEquals(EXPECTED_NET_WORTH, snapshot.getNetWorth());
    }

    @Test
    public void getTotalForAccountType_assets() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(SIMPLE_ASSET_SNAPSHOT, SIMPLE_LIABILITY_SNAPSHOT),
                        ImmutableList.of());

        // THEN
        assertEquals(
                ImmutableMap.of(
                        SIMPLE_ASSET_SNAPSHOT.getCurrencyUnit(), new BigDecimal("10000.00")),
                snapshot.getTotalForAccountType(AccountType.ASSET));
    }

    @Test
    public void getTotalForAccountType_liabilities() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(SIMPLE_ASSET_SNAPSHOT, SIMPLE_LIABILITY_SNAPSHOT),
                        ImmutableList.of());

        // THEN
        assertEquals(
                ImmutableMap.of(
                        SIMPLE_LIABILITY_SNAPSHOT.getCurrencyUnit(),
                        new BigDecimal("2500.00").negate()),
                snapshot.getTotalForAccountType(AccountType.LIABILITY));
    }

    @Test
    public void addAccountSnapshot_addNew() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(SNAPSHOT_INDEX, now, ImmutableSortedSet.of(), ImmutableList.of());

        // WHEN
        snapshot.addAccountSnapshot(SIMPLE_LIABILITY_SNAPSHOT);

        // THEN
        final SortedSet<AccountSnapshot> accountSnapshots = snapshot.getAccountSnapshots();
        assertEquals(1, accountSnapshots.size());
        assertEquals(SIMPLE_LIABILITY_SNAPSHOT, accountSnapshots.iterator().next());
        assertEquals(snapshot, SIMPLE_LIABILITY_SNAPSHOT.getSnapshot());

        assertEquals(
                ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-2500.00")),
                snapshot.getNetWorth());
    }

    @Test
    public void removeAccount_existing() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(SIMPLE_LIABILITY_SNAPSHOT, SIMPLE_ASSET_SNAPSHOT),
                        ImmutableList.of());

        // WHEN
        snapshot.removeAccountSnapshot(SIMPLE_LIABILITY_SNAPSHOT);

        // THEN
        assertEquals(1, snapshot.getAccountSnapshots().size());

        final SortedSet<AccountSnapshot> accountSnapshots = snapshot.getAccountSnapshots();
        assertTrue(SIMPLE_ASSET_SNAPSHOT.equalsIgnoreId(accountSnapshots.iterator().next()));

        assertEquals(
                ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("10000.00")),
                snapshot.getNetWorth());
    }

    @Test
    public void removeAccount_nonexisting() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(SIMPLE_ASSET_SNAPSHOT, SIMPLE_LIABILITY_SNAPSHOT),
                        ImmutableList.of());

        // WHEN
        final AccountSnapshot notInSnapshot =
                new SimpleAssetSnapshot("Another Account", CurrencyUnit.USD);
        snapshot.removeAccountSnapshot(notInSnapshot);

        // THEN
        assertEquals(2, snapshot.getAccountSnapshots().size());
        assertEquals(EXPECTED_NET_WORTH, snapshot.getNetWorth());
    }

    @Test
    public void getAccount_existing() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(SNAPSHOT_INDEX, now, ImmutableSortedSet.of(), ImmutableList.of());
        SIMPLE_ASSET_SNAPSHOT.setId(42L);
        snapshot.addAccountSnapshot(SIMPLE_ASSET_SNAPSHOT);

        // THEN
        assertTrue(snapshot.getAccountSnapshotById(42L).isPresent());
    }

    @Test
    public void getAccount_nonexisting() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(SNAPSHOT_INDEX, now, ImmutableSortedSet.of(), ImmutableList.of());
        SIMPLE_ASSET_SNAPSHOT.setId(42L);
        snapshot.addAccountSnapshot(SIMPLE_ASSET_SNAPSHOT);

        // THEN
        assertFalse(snapshot.getAccountSnapshotById(99L).isPresent());
    }

    @Test
    public void getAccounts_containersAreImmutable() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(SIMPLE_ASSET_SNAPSHOT, SIMPLE_LIABILITY_SNAPSHOT),
                        ImmutableList.of());

        final SortedSet<AccountSnapshot> accountSnapshots = snapshot.getAccountSnapshots();

        // THEN
        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    accountSnapshots.remove(SIMPLE_ASSET_SNAPSHOT);
                });

        final AccountSnapshot notInSnapshot =
                new SimpleAssetSnapshot(
                        "Another Account", CurrencyUnit.USD, new BigDecimal("50000"));
        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    accountSnapshots.add(notInSnapshot);
                });
    }

    @Test
    public void getAccountsByType() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(SIMPLE_ASSET_SNAPSHOT, SIMPLE_LIABILITY_SNAPSHOT),
                        ImmutableList.of());

        // WHEN
        final Map<AccountType, SortedSet<AccountSnapshot>> accountSnapshotsByType =
                snapshot.getAccountSnapshotsByType();

        // THEN
        assertEquals(2, accountSnapshotsByType.size());
        assertTrue(accountSnapshotsByType.containsKey(AccountType.ASSET));
        final SortedSet<AccountSnapshot> assetAccounts =
                accountSnapshotsByType.get(AccountType.ASSET);
        assertEquals(1, assetAccounts.size());
        assertTrue(SIMPLE_ASSET_SNAPSHOT.equalsIgnoreId(assetAccounts.iterator().next()));

        assertTrue(accountSnapshotsByType.containsKey(AccountType.LIABILITY));
        final SortedSet<AccountSnapshot> liabilityAccounts =
                accountSnapshotsByType.get(AccountType.LIABILITY);
        assertEquals(1, liabilityAccounts.size());
        assertTrue(SIMPLE_LIABILITY_SNAPSHOT.equalsIgnoreId(liabilityAccounts.iterator().next()));

        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    accountSnapshotsByType.remove(AccountType.LIABILITY);
                });

        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    accountSnapshotsByType
                            .get(AccountType.LIABILITY)
                            .remove(SIMPLE_LIABILITY_SNAPSHOT);
                });
    }

    @Test
    public void addTransaction_addRecurring() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(SNAPSHOT_INDEX, now, ImmutableSortedSet.of(), ImmutableList.of());
        final IncomeTransaction transaction = newRecurringIncome();

        // WHEN
        snapshot.addTransaction(transaction);

        // THEN
        final SortedSet<Transaction> transactions = snapshot.getTransactions();
        assertEquals(1, transactions.size());
        assertEquals(transaction, transactions.iterator().next());
        assertEquals(snapshot, transaction.getSnapshot());

        final List<Transaction> recurringTransactions = snapshot.getRecurringTransactions();
        assertEquals(1, recurringTransactions.size());
        assertEquals(transaction, recurringTransactions.get(0));
    }

    @Test
    public void addTransaction_addNonRecurring() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(SNAPSHOT_INDEX, now, ImmutableSortedSet.of(), ImmutableList.of());
        final IncomeTransaction transaction = newSingleIncome();

        // WHEN
        snapshot.addTransaction(transaction);

        // THEN
        final SortedSet<Transaction> transactions = snapshot.getTransactions();
        assertEquals(1, transactions.size());
        assertEquals(transaction, transactions.iterator().next());
        assertEquals(snapshot, transaction.getSnapshot());

        final List<Transaction> recurringTransactions = snapshot.getRecurringTransactions();
        assertTrue(recurringTransactions.isEmpty());
    }

    @Test
    public void removeTransaction_existing() {
        // GIVEN
        final IncomeTransaction recurringIncome = newRecurringIncome(42L);
        final Snapshot snapshot =
                new Snapshot(SNAPSHOT_INDEX, now, ImmutableSortedSet.of(), ImmutableList.of());

        snapshot.addTransaction(recurringIncome);
        snapshot.addTransaction(newSingleIncome());
        snapshot.addTransaction(newRecurringDonation());
        snapshot.addTransaction(newSingleDonation());

        // WHEN
        snapshot.removeTransaction(recurringIncome);

        // THEN
        assertEquals(3, snapshot.getTransactions().size());
        assertFalse(snapshot.getTransactions().contains(recurringIncome));
        assertNull(recurringIncome.getSnapshot());
    }

    @Test
    public void getTransactionsByType() {
        // GIVEN
        final IncomeTransaction recurringIncome = newRecurringIncome();
        final IncomeTransaction singleIncome = newSingleIncome();
        final InvestmentTransaction recurringInvestment = newRecurringInvestment();
        final InvestmentTransaction singleInvestment = newSingleInvestment();
        final DonationTransaction recurringDonation = newRecurringDonation();
        final DonationTransaction singleDonation = newSingleDonation();

        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(
                                recurringIncome,
                                singleIncome,
                                recurringDonation,
                                singleDonation,
                                recurringInvestment,
                                singleInvestment));

        // WHEN
        final Map<TransactionType, SortedSet<Transaction>> transactionsByType =
                snapshot.getTransactionsByType();

        // THEN
        assertEquals(3, transactionsByType.size());

        assertTrue(transactionsByType.containsKey(TransactionType.INCOME));
        final SortedSet<Transaction> incomes = transactionsByType.get(TransactionType.INCOME);
        assertEquals(2, incomes.size());
        final Iterator<Transaction> incomeIterator = incomes.iterator();
        assertTrue(singleIncome.equalsIgnoreId(incomeIterator.next()));
        assertTrue(recurringIncome.equalsIgnoreId(incomeIterator.next()));

        assertTrue(transactionsByType.containsKey(TransactionType.INVESTMENT));
        final SortedSet<Transaction> investments =
                transactionsByType.get(TransactionType.INVESTMENT);
        assertEquals(2, investments.size());
        final Iterator<Transaction> investmentIterator = investments.iterator();
        assertTrue(singleInvestment.equalsIgnoreId(investmentIterator.next()));
        assertTrue(recurringInvestment.equalsIgnoreId(investmentIterator.next()));

        assertTrue(transactionsByType.containsKey(TransactionType.DONATION));
        final SortedSet<Transaction> donations = transactionsByType.get(TransactionType.DONATION);
        assertEquals(2, donations.size());
        final Iterator<Transaction> donationIterator = donations.iterator();
        assertTrue(singleDonation.equalsIgnoreId(donationIterator.next()));
        assertTrue(recurringDonation.equalsIgnoreId(donationIterator.next()));

        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    transactionsByType.remove(TransactionType.INCOME);
                });

        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    transactionsByType.get(TransactionType.DONATION).remove(recurringDonation);
                });
    }

    @Test
    public void equals() {
        final Snapshot snapshot =
                new Snapshot(SNAPSHOT_INDEX, now, ImmutableSortedSet.of(), ImmutableList.of());

        // Itself
        assertTrue(snapshot.equals(snapshot));

        // Not instance of Snapshot
        assertFalse(snapshot.equals(null));
        assertFalse(snapshot.equals("Another type of object"));

        // Same Id null
        final Snapshot anotherSnapshot =
                new Snapshot(SNAPSHOT_INDEX, now, ImmutableSortedSet.of(), ImmutableList.of());
        snapshot.setId(null);
        anotherSnapshot.setId(null);
        assertFalse(snapshot.equals(anotherSnapshot));
        assertFalse(anotherSnapshot.equals(snapshot));

        // Same Id not null
        snapshot.setId(42L);
        anotherSnapshot.setId(42L);
        assertTrue(snapshot.equals(anotherSnapshot));
        assertTrue(anotherSnapshot.equals(snapshot));
    }
}
