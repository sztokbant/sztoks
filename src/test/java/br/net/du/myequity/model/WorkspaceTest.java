package br.net.du.myequity.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static br.net.du.myequity.test.TestUtil.newAssetAccount;
import static br.net.du.myequity.test.TestUtil.newLiabilityAccount;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkspaceTest {

    private Workspace workspace;
    private Account assetAccount;
    private Account liabilityAccont;
    private Map<CurrencyUnit, BigDecimal> expectedNetWorth;
    private Snapshot snapshot;

    @BeforeEach
    public void setUp() {
        workspace = new Workspace("My Workspace", CurrencyUnit.USD);
        workspace.setId(1L);

        liabilityAccont = newLiabilityAccount("320000.00");
        liabilityAccont.setId(7L);

        assetAccount = newAssetAccount("100.00");
        assetAccount.setId(99L);

        expectedNetWorth = ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-319900.00"));

        snapshot = new Snapshot(LocalDate.now(), ImmutableSet.of(liabilityAccont, assetAccount));
        snapshot.setId(108L);
    }

    @Test
    public void getAccounts_containersAreImmutable() {
        // GIVEN
        assertTrue(workspace.getAccounts().isEmpty());
        workspace.addAccount(assetAccount);
        workspace.addAccount(liabilityAccont);
        final Map<AccountType, List<Account>> accounts = workspace.getAccounts();

        // THEN
        assertThrows(UnsupportedOperationException.class, () -> {
            accounts.remove(AccountType.ASSET);
        });

        final Account notInWorkspace = newAssetAccount("50000.00");
        assertThrows(UnsupportedOperationException.class, () -> {
            accounts.get(AccountType.ASSET).add(notInWorkspace);
        });
    }

    @Test
    public void addAccount() {
        // GIVEN
        assertTrue(workspace.getAccounts().isEmpty());

        // WHEN
        workspace.addAccount(liabilityAccont);

        // THEN
        final Map<AccountType, List<Account>> accounts = workspace.getAccounts();
        assertEquals(1, accounts.size());
        assertEquals(1, accounts.get(AccountType.LIABILITY).size());
        assertEquals(liabilityAccont, accounts.get(AccountType.LIABILITY).get(0));
        assertEquals(workspace, liabilityAccont.getWorkspace());
    }

    @Test
    public void addAccount_addSameTwice() {
        // GIVEN
        assertTrue(workspace.getAccounts().isEmpty());
        workspace.addAccount(liabilityAccont);

        // WHEN
        workspace.addAccount(liabilityAccont);

        // THEN
        final Map<AccountType, List<Account>> accounts = workspace.getAccounts();
        assertEquals(1, accounts.size());
        assertEquals(1, accounts.get(AccountType.LIABILITY).size());
        assertEquals(liabilityAccont, accounts.get(AccountType.LIABILITY).get(0));
        assertEquals(workspace, liabilityAccont.getWorkspace());
    }

    @Test
    public void removeAccount() {
        // GIVEN
        assertTrue(workspace.getAccounts().isEmpty());
        workspace.addAccount(liabilityAccont);

        // WHEN
        workspace.removeAccount(liabilityAccont);

        // THEN
        assertTrue(workspace.getAccounts().isEmpty());
        assertNull(liabilityAccont.getWorkspace());
    }

    @Test
    public void removeAccount_removeSameTwice() {
        // GIVEN
        assertTrue(workspace.getAccounts().isEmpty());
        workspace.addAccount(liabilityAccont);
        workspace.removeAccount(liabilityAccont);

        // WHEN
        workspace.removeAccount(liabilityAccont);

        // THEN
        assertTrue(workspace.getAccounts().isEmpty());
        assertNull(liabilityAccont.getWorkspace());
    }

    @Test
    public void addSnapshot() {
        // GIVEN
        assertTrue(workspace.getSnapshots().isEmpty());

        // WHEN
        workspace.addSnapshot(snapshot);

        // THEN
        final List<Snapshot> snapshots = workspace.getSnapshots();
        assertEquals(1, snapshots.size());
        assertEquals(snapshot, snapshots.get(0));
        assertEquals(workspace, snapshot.getWorkspace());
    }

    @Test
    public void addSnapshot_addSameTwice() {
        // GIVEN
        assertTrue(workspace.getSnapshots().isEmpty());
        workspace.addSnapshot(snapshot);

        // WHEN
        workspace.addSnapshot(snapshot);

        // THEN
        final List<Snapshot> snapshots = workspace.getSnapshots();
        assertEquals(1, snapshots.size());
        assertEquals(snapshot, snapshots.get(0));
        assertEquals(workspace, snapshot.getWorkspace());
    }

    @Test
    public void removeSnapshot() {
        // GIVEN
        assertTrue(workspace.getSnapshots().isEmpty());
        workspace.addSnapshot(snapshot);

        // WHEN
        workspace.removeSnapshot(snapshot);

        // THEN
        assertTrue(workspace.getSnapshots().isEmpty());
        assertNull(snapshot.getWorkspace());
    }

    @Test
    public void removeSnapshot_removeSameTwice() {
        // GIVEN
        assertTrue(workspace.getSnapshots().isEmpty());
        workspace.addSnapshot(snapshot);
        workspace.removeSnapshot(snapshot);

        // WHEN
        workspace.removeSnapshot(snapshot);

        // THEN
        assertTrue(workspace.getSnapshots().isEmpty());
        assertNull(snapshot.getWorkspace());
    }

    @Test
    public void generateSnapshot() {
        // GIVEN
        assertTrue(workspace.getAccounts().isEmpty());
        assertTrue(workspace.getSnapshots().isEmpty());
        workspace.addAccount(assetAccount);
        workspace.addAccount(liabilityAccont);

        // WHEN
        workspace.generateSnapshot(LocalDate.now());

        // THEN
        assertEquals(1, workspace.getSnapshots().size());

        final Snapshot snapshot = workspace.getSnapshots().get(0);
        assertEquals(2, snapshot.getAccounts().size());
        assertEquals(expectedNetWorth, snapshot.getNetWorth());
    }

    @Test
    public void getNetWorth() {
        // GIVEN
        assertTrue(workspace.getAccounts().isEmpty());
        workspace.addAccount(assetAccount);
        workspace.addAccount(liabilityAccont);

        // THEN
        assertEquals(expectedNetWorth, workspace.getNetWorth());
    }

    @Test
    public void getAssetsTotal() {
        // GIVEN
        assertTrue(workspace.getAccounts().isEmpty());
        workspace.addAccount(assetAccount);
        workspace.addAccount(liabilityAccont);

        // THEN
        assertEquals(ImmutableMap.of(assetAccount.getBalance().getCurrencyUnit(),
                                     assetAccount.getBalance().getAmount()), workspace.getAssetsTotal());
    }

    @Test
    public void getLiabilitiesTotal() {
        // GIVEN
        assertTrue(workspace.getAccounts().isEmpty());
        workspace.addAccount(assetAccount);
        workspace.addAccount(liabilityAccont);

        // THEN
        assertEquals(ImmutableMap.of(liabilityAccont.getBalance().getCurrencyUnit(),
                                     liabilityAccont.getBalance().getAmount().negate()),
                     workspace.getLiabilitiesTotal());
    }

    @Test
    public void equals() {
        // Itself
        assertTrue(workspace.equals(workspace));

        // Not instance of Workspace
        assertFalse(workspace.equals(null));
        assertFalse(workspace.equals("Another type of object"));

        // Same Id null
        final Workspace anotherWorkspace = new Workspace("My Workspace", CurrencyUnit.USD);
        workspace.setId(null);
        anotherWorkspace.setId(null);
        assertFalse(workspace.equals(anotherWorkspace));
        assertFalse(anotherWorkspace.equals(workspace));

        // Same Id not null
        workspace.setId(42L);
        anotherWorkspace.setId(42L);
        assertTrue(workspace.equals(anotherWorkspace));
        assertTrue(anotherWorkspace.equals(workspace));
    }
}