package br.net.du.myequity.persistence;

import br.net.du.myequity.model.Account;
import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.Workspace;
import br.net.du.myequity.service.UserService;
import com.google.common.collect.ImmutableMap;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static br.net.du.myequity.test.ModelTestUtil.buildPopulatedWorkspace;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PersistenceTest {

    private static final String EMAIL = "example@example.com";
    private static final String FIRST_NAME = "Bill";
    private static final String LAST_NAME = "Gates";
    private static final String PASSWORD = "password";

    @Autowired
    private UserService userService;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SnapshotRepository snapshotRepository;

    private User user;

    private Workspace workspace;

    @BeforeEach
    public void setUp() {
        user = new User(EMAIL, FIRST_NAME, LAST_NAME);
        user.setPassword(PASSWORD);
        user.setPasswordConfirm(PASSWORD);
    }

    @Test
    @Transactional
    public void persistUser() {
        // GIVEN
        assertNull(user.getId());

        // WHEN
        userService.save(user);

        // THEN
        final User actual = userService.findByEmail(EMAIL);
        assertNotNull(actual.getId());
        assertEquals(user, actual);
    }

    @Test
    @Transactional
    public void addFlatWorkspaceToPersistedUser() {
        // GIVEN
        assertNull(user.getId());
        userService.save(user);

        workspace = new Workspace("My Workspace", CurrencyUnit.USD);
        assertNull(workspace.getId());

        // WHEN
        user.addWorkspace(workspace);

        // THEN
        final User actualUser = userService.findByEmail(EMAIL);
        assertNotNull(actualUser.getId());
        assertEquals(user, actualUser);
        assertEquals(1, actualUser.getWorkspaces().size());

        final Workspace actualWorkspace = workspaceRepository.findAll().get(0);
        assertNotNull(actualWorkspace.getId());
        assertNotNull(actualWorkspace.getUser());
        assertEquals(user, actualWorkspace.getUser());

        final Map<AccountType, List<Account>> accounts = actualWorkspace.getAccounts();
        assertTrue(accounts.isEmpty());

        final List<Snapshot> snapshots = actualWorkspace.getSnapshots();
        assertTrue(snapshots.isEmpty());
    }

    @Test
    @Transactional
    public void addPopulatedWorkspaceToPersistedUser() {
        // GIVEN
        assertNull(user.getId());
        userService.save(user);

        workspace = buildPopulatedWorkspace();
        assertNull(workspace.getId());

        // WHEN
        user.addWorkspace(workspace);

        // THEN
        final User actualUser = userService.findByEmail(EMAIL);
        assertNotNull(actualUser.getId());
        assertEquals(user, actualUser);
        assertEquals(1, actualUser.getWorkspaces().size());

        final Workspace actualWorkspace = workspaceRepository.findAll().get(0);
        assertNotNull(actualWorkspace.getId());
        assertNotNull(actualWorkspace.getUser());
        assertEquals(user, actualWorkspace.getUser());

        final Map<AccountType, List<Account>> accounts = actualWorkspace.getAccounts();
        assertEquals(2, accounts.size());
        assertEquals(1, accounts.get(AccountType.ASSET).size());
        assertEquals(1, accounts.get(AccountType.LIABILITY).size());
        final Account assetAccount = accounts.get(AccountType.ASSET).get(0);
        assertEquals(AccountType.ASSET, assetAccount.getAccountType());
        assertNotNull(assetAccount.getId());
        assertNotNull(assetAccount.getWorkspace());
        assertEquals(actualWorkspace, assetAccount.getWorkspace());

        final Account liabilityAccount = accounts.get(AccountType.LIABILITY).get(0);
        assertEquals(AccountType.LIABILITY, liabilityAccount.getAccountType());
        assertNotNull(liabilityAccount.getId());
        assertNotNull(liabilityAccount.getWorkspace());
        assertEquals(actualWorkspace, liabilityAccount.getWorkspace());

        final List<Snapshot> snapshots = actualWorkspace.getSnapshots();
        assertEquals(1, snapshots.size());
        final Snapshot snapshot = snapshots.get(0);
        assertEquals(2, snapshot.getAccounts().size());
        assertNotNull(snapshot.getId());
        assertNotNull(snapshot.getWorkspace());
        assertEquals(actualWorkspace, snapshot.getWorkspace());
    }

    @Test
    @Transactional
    public void removeAccountFromSnapshot() {
        // GIVEN
        assertNull(user.getId());
        userService.save(user);

        workspace = buildPopulatedWorkspace();
        assertNull(workspace.getId());

        user.addWorkspace(workspace);

        final Workspace savedWorkspace = workspaceRepository.findAll().get(0);
        final List<Snapshot> savedSnapshots = savedWorkspace.getSnapshots();
        final Snapshot savedSnapshot = savedSnapshots.get(0);

        assertEquals(2, savedSnapshot.getAccounts().size());

        // WHEN
        final Account liabilityAccount = savedWorkspace.getAccounts().get(AccountType.LIABILITY).get(0);
        savedSnapshot.removeAccount(liabilityAccount);

        // THEN
        final Snapshot actualSnapshot = snapshotRepository.findAll().get(0);
        assertEquals(1, actualSnapshot.getAccounts().size());
    }

    @Test
    @Transactional
    public void updateAccountInSnapshot() {
        // GIVEN
        assertNull(user.getId());
        userService.save(user);

        workspace = buildPopulatedWorkspace();
        assertNull(workspace.getId());

        user.addWorkspace(workspace);

        final Workspace savedWorkspace = workspaceRepository.findAll().get(0);
        final List<Snapshot> savedSnapshots = savedWorkspace.getSnapshots();
        final Snapshot savedSnapshot = savedSnapshots.get(0);

        assertEquals(2, savedSnapshot.getAccounts().size());
        assertEquals(ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-319900.00")), savedSnapshot.getNetWorth());

        // WHEN
        final Account liabilityAccount = savedWorkspace.getAccounts().get(AccountType.LIABILITY).get(0);
        savedSnapshot.setAccount(liabilityAccount,
                                 liabilityAccount.getBalance().getAmount().add(new BigDecimal("100000.00")));

        // THEN
        final Snapshot actualSnapshot = snapshotRepository.findAll().get(0);
        assertEquals(2, actualSnapshot.getAccounts().size());
        assertEquals(ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-419900.00")), actualSnapshot.getNetWorth());
    }

    @Test
    @Transactional
    public void removeWorkspaceFromPersistedUser() {
        // GIVEN
        user.addWorkspace(buildPopulatedWorkspace());
        userService.save(user);

        assertFalse(workspaceRepository.findAll().isEmpty());
        assertFalse(accountRepository.findAll().isEmpty());
        assertFalse(snapshotRepository.findAll().isEmpty());

        // WHEN
        final User persistedUser = userService.findByEmail(EMAIL);
        final Workspace savedWorkspace = persistedUser.getWorkspaces().iterator().next();
        persistedUser.removeWorkspace(savedWorkspace);

        // THEN
        final User actualUser = userService.findByEmail(EMAIL);
        assertTrue(actualUser.getWorkspaces().isEmpty());

        assertTrue(workspaceRepository.findAll().isEmpty());
        assertTrue(accountRepository.findAll().isEmpty());
        assertTrue(snapshotRepository.findAll().isEmpty());
    }
}
