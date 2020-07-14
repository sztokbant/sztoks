package br.net.du.myequity.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;

import static br.net.du.myequity.test.TestUtil.newAssetAccount;
import static br.net.du.myequity.test.TestUtil.newLiabilityAccount;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnapshotTest {

    private LocalDate now;
    private Account assetAccount;
    private Account liabilityAccount;
    private Map<CurrencyUnit, BigDecimal> expectedNetWorth;

    @BeforeEach
    public void setUp() {
        now = LocalDate.now();
        assetAccount = newAssetAccount("100.00");
        liabilityAccount = newLiabilityAccount("320000.00");
        expectedNetWorth = ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-319900.00"));
    }

    @Test
    public void constructor() {
        // WHEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSet.of(assetAccount, liabilityAccount));

        // THEN
        assertEquals(now, snapshot.getDate());
        assertEquals(2, snapshot.getAccounts().size());
        assertEquals(expectedNetWorth, snapshot.getNetWorth());
    }

    @Test
    public void getNetWorth_afterModifyingOriginalObjects() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSet.of(assetAccount, liabilityAccount));

        // WHEN
        final Money newBalance = assetAccount.getBalance().plus(new BigDecimal("72.00"));
        assetAccount.setBalance(newBalance);
        liabilityAccount.setBalance(liabilityAccount.getBalance().minus(new BigDecimal("99.00")));

        // THEN
        assertEquals(expectedNetWorth, snapshot.getNetWorth());
    }

    @Test
    public void getAssetsTotal_afterModifyingOriginalObjects() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSet.of(assetAccount, liabilityAccount));

        // WHEN
        assetAccount.setBalance(assetAccount.getBalance().plus(new BigDecimal("72.00")));
        liabilityAccount.setBalance(liabilityAccount.getBalance().minus(new BigDecimal("99.00")));

        // THEN
        assertEquals(ImmutableMap.of(assetAccount.getBalance().getCurrencyUnit(), new BigDecimal("100.00")),
                     snapshot.getAssetsTotal());
    }

    @Test
    public void getLiabilitiesTotal_afterModifyingOriginalObjects() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSet.of(assetAccount, liabilityAccount));

        // WHEN
        assetAccount.setBalance(assetAccount.getBalance().plus(new BigDecimal("72.00")));
        liabilityAccount.setBalance(liabilityAccount.getBalance().minus(new BigDecimal("99.00")));

        // THEN
        assertEquals(ImmutableMap.of(liabilityAccount.getBalance().getCurrencyUnit(),
                                     new BigDecimal("320000.00").negate()), snapshot.getLiabilitiesTotal());
    }

    @Test
    public void setAccount_addNew() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSet.of());

        // WHEN
        snapshot.setAccount(liabilityAccount);

        // THEN
        assertEquals(1, snapshot.getAccounts().size());
        assertEquals(new BigDecimal("320000.00"), liabilityAccount.getBalance().getAmount());
        assertEquals(new BigDecimal("320000.00"), snapshot.getAccounts().get(liabilityAccount));
        assertEquals(ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-320000.00")), snapshot.getNetWorth());
    }

    @Test
    public void setAccount_addNewWithBalance() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSet.of());

        // WHEN
        snapshot.setAccount(liabilityAccount,
                            liabilityAccount.getBalance().getAmount().add(new BigDecimal("100000.00")));

        // THEN
        assertEquals(1, snapshot.getAccounts().size());
        assertEquals(new BigDecimal("320000.00"), liabilityAccount.getBalance().getAmount());
        assertEquals(new BigDecimal("420000.00"), snapshot.getAccounts().get(liabilityAccount));
        assertEquals(ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-420000.00")), snapshot.getNetWorth());
    }

    @Test
    public void setAccount_updateExisting() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSet.of(assetAccount, liabilityAccount));

        // WHEN
        snapshot.setAccount(liabilityAccount,
                            liabilityAccount.getBalance().getAmount().add(new BigDecimal("100000.00")));

        // THEN
        assertEquals(2, snapshot.getAccounts().size());
        assertEquals(new BigDecimal("320000.00"), liabilityAccount.getBalance().getAmount());
        assertEquals(new BigDecimal("420000.00"), snapshot.getAccounts().get(liabilityAccount));
        assertEquals(ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-419900.00")), snapshot.getNetWorth());
    }

    @Test
    public void removeAccount_existing() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSet.of(assetAccount, liabilityAccount));

        // WHEN
        snapshot.removeAccount(liabilityAccount);

        // THEN
        assertEquals(1, snapshot.getAccounts().size());
        assertEquals(new BigDecimal("320000.00"), liabilityAccount.getBalance().getAmount());
        assertNull(snapshot.getAccounts().get(liabilityAccount));
        assertEquals(ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("100.00")), snapshot.getNetWorth());
    }

    @Test
    public void removeAccount_nonexisting() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSet.of(assetAccount, liabilityAccount));

        // WHEN
        final Account notInSnapshot = newAssetAccount("50000.00");
        snapshot.removeAccount(notInSnapshot);

        // THEN
        assertEquals(2, snapshot.getAccounts().size());
        assertEquals(new BigDecimal("320000.00"), snapshot.getAccounts().get(liabilityAccount));
        assertEquals(new BigDecimal("100.00"), snapshot.getAccounts().get(assetAccount));
        assertNull(snapshot.getAccount(notInSnapshot));
        assertEquals(expectedNetWorth, snapshot.getNetWorth());
    }

    @Test
    public void getAccount_nonexisting() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSet.of(assetAccount));

        // THEN
        assertNull(snapshot.getAccount(liabilityAccount));
    }

    @Test
    public void getAccounts_containersAreImmutable() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSet.of(assetAccount, liabilityAccount));
        final Map<Account, BigDecimal> accounts = snapshot.getAccounts();

        // THEN
        assertThrows(UnsupportedOperationException.class, () -> {
            accounts.remove(assetAccount);
        });

        final Account notInSnapshot = newAssetAccount("50000.00");
        assertThrows(UnsupportedOperationException.class, () -> {
            accounts.put(notInSnapshot, notInSnapshot.getBalance().getAmount());
        });
    }

    @Test
    public void getAccountsByType() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSet.of(assetAccount, liabilityAccount));

        // WHEN
        final Map<AccountType, Map<Account, BigDecimal>> accounts = snapshot.getAccountsByType();

        // THEN
        assertEquals(2, accounts.size());
        assertTrue(accounts.containsKey(AccountType.ASSET));
        final Map<Account, BigDecimal> assetAccounts = accounts.get(AccountType.ASSET);
        assertEquals(1, assetAccounts.size());
        assertEquals(assetAccount.getBalance().getAmount(), assetAccounts.get(assetAccount));

        assertTrue(accounts.containsKey(AccountType.LIABILITY));
        final Map<Account, BigDecimal> liabilityAccounts = accounts.get(AccountType.LIABILITY);
        assertEquals(1, liabilityAccounts.size());
        assertEquals(liabilityAccount.getBalance().getAmount(), liabilityAccounts.get(liabilityAccount));

        assertThrows(UnsupportedOperationException.class, () -> {
            accounts.remove(AccountType.LIABILITY);
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            accounts.get(AccountType.LIABILITY).remove(liabilityAccount);
        });
    }

    @Test
    public void equals() {
        final Snapshot snapshot = new Snapshot(LocalDate.now(), new HashSet<>());

        // Itself
        assertTrue(snapshot.equals(snapshot));

        // Not instance of Workspace
        assertFalse(snapshot.equals(null));
        assertFalse(snapshot.equals("Another type of object"));

        // Same Id null
        final Snapshot anotherSnapshot = new Snapshot(LocalDate.now(), new HashSet<>());
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