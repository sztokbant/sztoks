package br.net.du.myequity.model;

import com.google.common.collect.ImmutableMap;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnapshotTest {

    private LocalDate now;
    private Account assetAccount;
    private BigDecimal assetAmount;
    private Account liabilityAccount;
    private BigDecimal liabilityAmount;
    private Map<CurrencyUnit, BigDecimal> expectedNetWorth;

    @BeforeEach
    public void setUp() {
        now = LocalDate.now();
        assetAccount = new Account("Asset Account", AccountType.ASSET, CurrencyUnit.USD);
        assetAmount = new BigDecimal("100.00");
        liabilityAccount = new Account("Liability Account", AccountType.LIABILITY, CurrencyUnit.USD);
        liabilityAmount = new BigDecimal("320000.00");
        expectedNetWorth = ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-319900.00"));
    }

    @Test
    public void constructor() {
        // WHEN
        final Snapshot snapshot =
                new Snapshot(now, ImmutableMap.of(assetAccount, assetAmount, liabilityAccount, liabilityAmount));

        // THEN
        assertEquals(now, snapshot.getDate());
        assertEquals(2, snapshot.getAccounts().size());
        assertEquals(expectedNetWorth, snapshot.getNetWorth());
    }

    @Test
    public void getNetWorth() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(now, ImmutableMap.of(assetAccount, assetAmount, liabilityAccount, liabilityAmount));

        // THEN
        assertEquals(expectedNetWorth, snapshot.getNetWorth());
    }

    @Test
    public void getTotalForAccountType_assets() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(now, ImmutableMap.of(assetAccount, assetAmount, liabilityAccount, liabilityAmount));

        // THEN
        assertEquals(ImmutableMap.of(assetAccount.getCurrencyUnit(), new BigDecimal("100.00")),
                     snapshot.getTotalForAccountType(AccountType.ASSET));
    }

    @Test
    public void getTotalForAccountType_liabilities() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(now, ImmutableMap.of(assetAccount, assetAmount, liabilityAccount, liabilityAmount));

        // THEN
        assertEquals(ImmutableMap.of(liabilityAccount.getCurrencyUnit(), new BigDecimal("320000.00").negate()),
                     snapshot.getTotalForAccountType(AccountType.LIABILITY));
    }

    @Test
    public void putAccount_addNew() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableMap.of());

        // WHEN
        snapshot.putAccount(liabilityAccount, liabilityAmount);

        // THEN
        assertEquals(1, snapshot.getAccounts().size());
        assertEquals(new BigDecimal("320000.00"), snapshot.getAccounts().get(liabilityAccount));
        assertEquals(ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-320000.00")), snapshot.getNetWorth());
    }

    @Test
    public void putAccount_updateExisting() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(now, ImmutableMap.of(assetAccount, assetAmount, liabilityAccount, liabilityAmount));

        // WHEN
        snapshot.putAccount(liabilityAccount, liabilityAmount.add(new BigDecimal("100000.00")));

        // THEN
        assertEquals(2, snapshot.getAccounts().size());
        assertEquals(new BigDecimal("420000.00"), snapshot.getAccounts().get(liabilityAccount));
        assertEquals(ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-419900.00")), snapshot.getNetWorth());
    }

    @Test
    public void removeAccount_existing() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(now, ImmutableMap.of(assetAccount, assetAmount, liabilityAccount, liabilityAmount));

        // WHEN
        snapshot.removeAccount(liabilityAccount);

        // THEN
        assertEquals(1, snapshot.getAccounts().size());
        assertNull(snapshot.getAccounts().get(liabilityAccount));
        assertEquals(ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("100.00")), snapshot.getNetWorth());
    }

    @Test
    public void removeAccount_nonexisting() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(now, ImmutableMap.of(assetAccount, assetAmount, liabilityAccount, liabilityAmount));

        // WHEN
        final Account notInSnapshot = new Account("Another Account", AccountType.ASSET, CurrencyUnit.USD);
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
        final Snapshot snapshot = new Snapshot(now, ImmutableMap.of(assetAccount, assetAmount));

        // THEN
        assertNull(snapshot.getAccount(liabilityAccount));
    }

    @Test
    public void getAccounts_containersAreImmutable() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(now, ImmutableMap.of(assetAccount, assetAmount, liabilityAccount, liabilityAmount));
        final Map<Account, BigDecimal> accounts = snapshot.getAccounts();

        // THEN
        assertThrows(UnsupportedOperationException.class, () -> {
            accounts.remove(assetAccount);
        });

        final Account notInSnapshot = new Account("Another Account", AccountType.ASSET, CurrencyUnit.USD);
        assertThrows(UnsupportedOperationException.class, () -> {
            accounts.put(notInSnapshot, new BigDecimal("50000"));
        });
    }

    @Test
    public void getAccountsByType() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(now, ImmutableMap.of(assetAccount, assetAmount, liabilityAccount, liabilityAmount));

        // WHEN
        final Map<AccountType, Map<Account, BigDecimal>> accounts = snapshot.getAccountsByType();

        // THEN
        assertEquals(2, accounts.size());
        assertTrue(accounts.containsKey(AccountType.ASSET));
        final Map<Account, BigDecimal> assetAccounts = accounts.get(AccountType.ASSET);
        assertEquals(1, assetAccounts.size());
        assertEquals(assetAmount, assetAccounts.get(assetAccount));

        assertTrue(accounts.containsKey(AccountType.LIABILITY));
        final Map<Account, BigDecimal> liabilityAccounts = accounts.get(AccountType.LIABILITY);
        assertEquals(1, liabilityAccounts.size());
        assertEquals(liabilityAmount, liabilityAccounts.get(liabilityAccount));

        assertThrows(UnsupportedOperationException.class, () -> {
            accounts.remove(AccountType.LIABILITY);
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            accounts.get(AccountType.LIABILITY).remove(liabilityAccount);
        });
    }

    @Test
    public void equals() {
        final Snapshot snapshot = new Snapshot(LocalDate.now(), ImmutableMap.of());

        // Itself
        assertTrue(snapshot.equals(snapshot));

        // Not instance of Snapshot
        assertFalse(snapshot.equals(null));
        assertFalse(snapshot.equals("Another type of object"));

        // Same Id null
        final Snapshot anotherSnapshot = new Snapshot(LocalDate.now(), ImmutableMap.of());
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
