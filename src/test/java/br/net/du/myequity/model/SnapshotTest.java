package br.net.du.myequity.model;

import static br.net.du.myequity.test.TestConstants.BEGGAR_DONATION;
import static br.net.du.myequity.test.TestConstants.CHARITY_DONATION;
import static br.net.du.myequity.test.TestConstants.SALARY_INCOME;
import static br.net.du.myequity.test.TestConstants.SIDE_GIG_INCOME;
import static br.net.du.myequity.test.TestConstants.SIMPLE_ASSET_ACCOUNT;
import static br.net.du.myequity.test.TestConstants.SIMPLE_ASSET_SNAPSHOT;
import static br.net.du.myequity.test.TestConstants.SIMPLE_LIABILITY_ACCOUNT;
import static br.net.du.myequity.test.TestConstants.SIMPLE_LIABILITY_SNAPSHOT;
import static br.net.du.myequity.test.TestConstants.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.account.SimpleAssetAccount;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.SimpleAssetSnapshot;
import br.net.du.myequity.model.transaction.Donation;
import br.net.du.myequity.model.transaction.Income;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
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
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(SIMPLE_ASSET_SNAPSHOT, SIMPLE_LIABILITY_SNAPSHOT),
                        ImmutableSortedSet.of(SALARY_INCOME, SIDE_GIG_INCOME),
                        ImmutableSortedSet.of(CHARITY_DONATION, BEGGAR_DONATION));
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

        assertNull(SALARY_INCOME.getSnapshot());
        assertNull(SIDE_GIG_INCOME.getSnapshot());

        assertEquals(2, snapshot.getIncomes().size());
        for (final Income income : snapshot.getIncomes()) {
            assertEquals(snapshot, income.getSnapshot());
        }

        assertNull(CHARITY_DONATION.getSnapshot());
        assertNull(BEGGAR_DONATION.getSnapshot());

        assertEquals(2, snapshot.getDonations().size());
        for (final Donation donation : snapshot.getDonations()) {
            assertEquals(snapshot, donation.getSnapshot());
        }

        assertEquals(EXPECTED_NET_WORTH, snapshot.getNetWorth());
    }

    @Test
    public void getNetWorth() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(SIMPLE_ASSET_SNAPSHOT, SIMPLE_LIABILITY_SNAPSHOT),
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of());

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
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of());

        // THEN
        assertEquals(
                ImmutableMap.of(SIMPLE_ASSET_ACCOUNT.getCurrencyUnit(), new BigDecimal("10000.00")),
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
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of());

        // THEN
        assertEquals(
                ImmutableMap.of(
                        SIMPLE_LIABILITY_ACCOUNT.getCurrencyUnit(),
                        new BigDecimal("2500.00").negate()),
                snapshot.getTotalForAccountType(AccountType.LIABILITY));
    }

    @Test
    public void addAccountSnapshot_addNew() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of());

        // WHEN
        snapshot.addAccountSnapshot(SIMPLE_LIABILITY_SNAPSHOT);

        // THEN
        final SortedSet<AccountSnapshot> accountSnapshots = snapshot.getAccountSnapshots();
        assertEquals(1, accountSnapshots.size());
        assertEquals(SIMPLE_LIABILITY_SNAPSHOT, accountSnapshots.iterator().next());
        assertEquals(snapshot, SIMPLE_LIABILITY_SNAPSHOT.getSnapshot());

        assertEquals(
                new BigDecimal("2500.00"),
                snapshot.getAccountSnapshotFor(SIMPLE_LIABILITY_ACCOUNT).get().getTotal());
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
                        ImmutableSortedSet.of(SIMPLE_ASSET_SNAPSHOT, SIMPLE_LIABILITY_SNAPSHOT),
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of());

        // WHEN
        snapshot.removeAccountSnapshotFor(SIMPLE_LIABILITY_ACCOUNT);

        // THEN
        assertEquals(1, snapshot.getAccountSnapshots().size());
        assertFalse(snapshot.getAccountSnapshotFor(SIMPLE_LIABILITY_ACCOUNT).isPresent());
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
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of());

        // WHEN
        final Account notInSnapshot = new SimpleAssetAccount("Another Account", CurrencyUnit.USD);
        snapshot.removeAccountSnapshotFor(notInSnapshot);

        // THEN
        assertEquals(2, snapshot.getAccountSnapshots().size());
        assertEquals(
                new BigDecimal("2500.00"),
                snapshot.getAccountSnapshotFor(SIMPLE_LIABILITY_ACCOUNT).get().getTotal());
        assertEquals(
                new BigDecimal("10000.00"),
                snapshot.getAccountSnapshotFor(SIMPLE_ASSET_ACCOUNT).get().getTotal());
        assertFalse(snapshot.getAccountSnapshotFor(notInSnapshot).isPresent());
        assertEquals(EXPECTED_NET_WORTH, snapshot.getNetWorth());
    }

    @Test
    public void getAccount_nonexisting() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(SIMPLE_ASSET_SNAPSHOT),
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of());

        // THEN
        assertFalse(snapshot.getAccountSnapshotFor(SIMPLE_LIABILITY_ACCOUNT).isPresent());
    }

    @Test
    public void getAccounts_containersAreImmutable() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(SIMPLE_ASSET_SNAPSHOT, SIMPLE_LIABILITY_SNAPSHOT),
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of());

        final SortedSet<AccountSnapshot> accountSnapshots = snapshot.getAccountSnapshots();

        // THEN
        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    accountSnapshots.remove(SIMPLE_ASSET_SNAPSHOT);
                });

        final Account anotherAccount = new SimpleAssetAccount("Another Account", CurrencyUnit.USD);
        final AccountSnapshot notInSnapshot =
                new SimpleAssetSnapshot(anotherAccount, new BigDecimal("50000"));
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
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of());

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
                            .remove(SIMPLE_LIABILITY_ACCOUNT);
                });
    }

    @Test
    public void addIncome_addNew() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of());

        // WHEN
        snapshot.addIncome(SALARY_INCOME);

        // THEN
        final SortedSet<Income> incomes = snapshot.getIncomes();
        assertEquals(1, incomes.size());
        assertEquals(SALARY_INCOME, incomes.iterator().next());
        assertEquals(snapshot, SALARY_INCOME.getSnapshot());
    }

    @Test
    public void removeIncome_existing() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of(SALARY_INCOME, SIDE_GIG_INCOME),
                        ImmutableSortedSet.of());

        // WHEN
        snapshot.removeIncome(SALARY_INCOME);

        // THEN
        assertEquals(1, snapshot.getIncomes().size());
        assertFalse(snapshot.getIncomes().contains(SALARY_INCOME));
        assertNull(SALARY_INCOME.getSnapshot());
    }

    @Test
    public void removeDonation_existing() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of(CHARITY_DONATION, BEGGAR_DONATION));

        // WHEN
        snapshot.removeDonation(CHARITY_DONATION);

        // THEN
        assertEquals(1, snapshot.getDonations().size());
        assertFalse(snapshot.getDonations().contains(CHARITY_DONATION));
        assertNull(CHARITY_DONATION.getSnapshot());
    }

    @Test
    public void addDonation_addNew() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of());

        // WHEN
        snapshot.addDonation(CHARITY_DONATION);

        // THEN
        final SortedSet<Donation> donations = snapshot.getDonations();
        assertEquals(1, donations.size());
        assertEquals(CHARITY_DONATION, donations.iterator().next());
        assertEquals(snapshot, CHARITY_DONATION.getSnapshot());
    }

    @Test
    public void equals() {
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of());

        // Itself
        assertTrue(snapshot.equals(snapshot));

        // Not instance of Snapshot
        assertFalse(snapshot.equals(null));
        assertFalse(snapshot.equals("Another type of object"));

        // Same Id null
        final Snapshot anotherSnapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of());
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
