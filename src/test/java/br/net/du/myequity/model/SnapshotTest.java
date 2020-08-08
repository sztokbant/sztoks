package br.net.du.myequity.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.SortedSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnapshotTest {

    private LocalDate now;
    private Account assetAccount;
    private AccountSnapshotMetadata assetData;
    private Account liabilityAccount;
    private AccountSnapshotMetadata liabilityData;
    private Map<CurrencyUnit, BigDecimal> expectedNetWorth;

    @BeforeEach
    public void setUp() {
        now = LocalDate.now();
        assetAccount = new Account("Asset Account", AccountType.ASSET, CurrencyUnit.USD);
        assetData = new AccountSnapshotMetadata(assetAccount, new BigDecimal("100.00"));
        liabilityAccount = new Account("Liability Account", AccountType.LIABILITY, CurrencyUnit.USD);
        liabilityData = new AccountSnapshotMetadata(liabilityAccount, new BigDecimal("320000.00"));
        expectedNetWorth = ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-319900.00"));
    }

    @Test
    public void constructor() {
        // WHEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSortedSet.of(assetData, liabilityData));

        // THEN
        assertEquals(now, snapshot.getDate());
        assertEquals(2, snapshot.getAccountSnapshotMetadataSet().size());
        assertEquals(expectedNetWorth, snapshot.getNetWorth());
    }

    @Test
    public void getNetWorth() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSortedSet.of(assetData, liabilityData));

        // THEN
        assertEquals(expectedNetWorth, snapshot.getNetWorth());
    }

    @Test
    public void getTotalForAccountType_assets() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSortedSet.of(assetData, liabilityData));

        // THEN
        assertEquals(ImmutableMap.of(assetAccount.getCurrencyUnit(), new BigDecimal("100.00")),
                     snapshot.getTotalForAccountType(AccountType.ASSET));
    }

    @Test
    public void getTotalForAccountType_liabilities() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSortedSet.of(assetData, liabilityData));

        // THEN
        assertEquals(ImmutableMap.of(liabilityAccount.getCurrencyUnit(), new BigDecimal("320000.00").negate()),
                     snapshot.getTotalForAccountType(AccountType.LIABILITY));
    }

    @Test
    public void putAccount_addNew() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSortedSet.of());

        // WHEN
        snapshot.addAccountSnapshotMetadata(liabilityData);

        // THEN
        assertEquals(1, snapshot.getAccountSnapshotMetadataSet().size());
        assertEquals(new BigDecimal("320000.00"),
                     snapshot.getAccountSnapshotMetadataFor(liabilityAccount).get().getAmount());
        assertEquals(ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-320000.00")), snapshot.getNetWorth());
    }

    @Test
    public void removeAccount_existing() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSortedSet.of(assetData, liabilityData));

        // WHEN
        snapshot.removeAccountSnapshotMetadataFor(liabilityAccount);

        // THEN
        assertEquals(1, snapshot.getAccountSnapshotMetadataSet().size());
        assertFalse(snapshot.getAccountSnapshotMetadataFor(liabilityAccount).isPresent());
        assertEquals(ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("100.00")), snapshot.getNetWorth());
    }

    @Test
    public void removeAccount_nonexisting() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSortedSet.of(assetData, liabilityData));

        // WHEN
        final Account notInSnapshot = new Account("Another Account", AccountType.ASSET, CurrencyUnit.USD);
        snapshot.removeAccountSnapshotMetadataFor(notInSnapshot);

        // THEN
        assertEquals(2, snapshot.getAccountSnapshotMetadataSet().size());
        assertEquals(new BigDecimal("320000.00"),
                     snapshot.getAccountSnapshotMetadataFor(liabilityAccount).get().getAmount());
        assertEquals(new BigDecimal("100.00"), snapshot.getAccountSnapshotMetadataFor(assetAccount).get().getAmount());
        assertFalse(snapshot.getAccountSnapshotMetadataFor(notInSnapshot).isPresent());
        assertEquals(expectedNetWorth, snapshot.getNetWorth());
    }

    @Test
    public void getAccount_nonexisting() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSortedSet.of(assetData));

        // THEN
        assertFalse(snapshot.getAccountSnapshotMetadataFor(liabilityAccount).isPresent());
    }

    @Test
    public void getAccounts_containersAreImmutable() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSortedSet.of(assetData, liabilityData));

        final SortedSet<AccountSnapshotMetadata> accountSnapshotMetadata = snapshot.getAccountSnapshotMetadataSet();

        // THEN
        assertThrows(UnsupportedOperationException.class, () -> {
            accountSnapshotMetadata.remove(assetData);
        });

        final Account anotherAccount = new Account("Another Account", AccountType.ASSET, CurrencyUnit.USD);
        final AccountSnapshotMetadata notInSnapshot =
                new AccountSnapshotMetadata(anotherAccount, new BigDecimal("50000"));
        assertThrows(UnsupportedOperationException.class, () -> {
            accountSnapshotMetadata.add(notInSnapshot);
        });
    }

    @Test
    public void getAccountsByType() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSortedSet.of(assetData, liabilityData));

        // WHEN
        final Map<AccountType, SortedSet<AccountSnapshotMetadata>> accountSnapshotDatas =
                snapshot.getAccountSnapshotMetadataByType();

        // THEN
        assertEquals(2, accountSnapshotDatas.size());
        assertTrue(accountSnapshotDatas.containsKey(AccountType.ASSET));
        final SortedSet<AccountSnapshotMetadata> assetAccounts = accountSnapshotDatas.get(AccountType.ASSET);
        assertEquals(1, assetAccounts.size());
        assertEquals(assetData, assetAccounts.iterator().next());

        assertTrue(accountSnapshotDatas.containsKey(AccountType.LIABILITY));
        final SortedSet<AccountSnapshotMetadata> liabilityAccounts = accountSnapshotDatas.get(AccountType.LIABILITY);
        assertEquals(1, liabilityAccounts.size());
        assertEquals(liabilityData, liabilityAccounts.iterator().next());

        assertThrows(UnsupportedOperationException.class, () -> {
            accountSnapshotDatas.remove(AccountType.LIABILITY);
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            accountSnapshotDatas.get(AccountType.LIABILITY).remove(liabilityAccount);
        });
    }

    @Test
    public void equals() {
        final Snapshot snapshot = new Snapshot(LocalDate.now(), ImmutableSortedSet.of());

        // Itself
        assertTrue(snapshot.equals(snapshot));

        // Not instance of Snapshot
        assertFalse(snapshot.equals(null));
        assertFalse(snapshot.equals("Another type of object"));

        // Same Id null
        final Snapshot anotherSnapshot = new Snapshot(LocalDate.now(), ImmutableSortedSet.of());
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
