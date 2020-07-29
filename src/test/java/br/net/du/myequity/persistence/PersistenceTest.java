package br.net.du.myequity.persistence;

import br.net.du.myequity.model.Account;
import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.service.UserService;
import com.google.common.collect.ImmutableMap;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    private AccountRepository accountRepository;

    @Autowired
    private SnapshotRepository snapshotRepository;

    private User user;

    private Snapshot snapshot;

    private Account assetAccount;
    private BigDecimal assetAmount;
    private Account liabilityAccount;
    private BigDecimal liabilityAmount;

    @BeforeEach
    public void setUp() {
        user = new User(EMAIL, FIRST_NAME, LAST_NAME);
        user.setPassword(PASSWORD);
        user.setPasswordConfirm(PASSWORD);

        assetAccount = new Account("Asset Account", AccountType.ASSET, CurrencyUnit.USD);
        assetAmount = new BigDecimal("100.00");
        liabilityAccount = new Account("Liability Account", AccountType.LIABILITY, CurrencyUnit.USD);
        liabilityAmount = new BigDecimal("320000.00");
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
    public void addEmptySnapshotToPersistedUser() {
        // GIVEN
        assertNull(user.getId());
        userService.save(user);

        snapshot = new Snapshot(LocalDate.now(), ImmutableMap.of());
        assertNull(snapshot.getId());

        // WHEN
        user.addSnapshot(snapshot);

        // THEN
        final User actualUser = userService.findByEmail(EMAIL);
        assertNotNull(actualUser.getId());
        assertEquals(user, actualUser);
        assertEquals(1, actualUser.getSnapshots().size());
        assertEquals(snapshot, user.getSnapshots().iterator().next());

        final Snapshot actualSnapshot = snapshotRepository.findAll().get(0);
        assertNotNull(actualSnapshot.getId());
        assertNotNull(actualSnapshot.getUser());
        assertEquals(user, actualSnapshot.getUser());

        final Map<Account, BigDecimal> accounts = actualSnapshot.getAccounts();
        assertTrue(accounts.isEmpty());
    }

    @Test
    @Transactional
    public void addPopulatedSnapshotToPersistedUser() {
        // GIVEN
        saveNewUserWithAccounts();
        initSnapshot();

        // WHEN
        user.addSnapshot(snapshot);

        // THEN
        final User actualUser = userService.findByEmail(EMAIL);
        assertNotNull(actualUser.getId());
        assertEquals(user, actualUser);
        assertEquals(1, actualUser.getSnapshots().size());

        final Snapshot actualSnapshot = snapshotRepository.findAll().get(0);
        assertNotNull(actualSnapshot.getId());
        assertNotNull(actualSnapshot.getUser());
        assertEquals(user, actualSnapshot.getUser());

        final Map<AccountType, Map<Account, BigDecimal>> accounts = actualSnapshot.getAccountsByType();
        assertEquals(2, accounts.size());
        assertEquals(1, accounts.get(AccountType.ASSET).size());
        assertEquals(1, accounts.get(AccountType.LIABILITY).size());

        assertNotNull(assetAccount.getId());
        assertNotNull(assetAccount.getUser());
        assertEquals(user, assetAccount.getUser());
        final BigDecimal actualAssetAmount = accounts.get(AccountType.ASSET).get(assetAccount);
        assertEquals(assetAmount, actualAssetAmount);

        assertNotNull(liabilityAccount.getId());
        assertNotNull(liabilityAccount.getUser());
        assertEquals(user, liabilityAccount.getUser());
        final BigDecimal actualLiabilityAmount = accounts.get(AccountType.LIABILITY).get(liabilityAccount);
        assertEquals(liabilityAmount, actualLiabilityAmount);
    }

    @Test
    @Transactional
    public void removeAccountFromSnapshot() {
        // GIVEN
        saveNewUserWithAccounts();
        initSnapshot();

        user.addSnapshot(snapshot);

        final Snapshot savedSnapshot = snapshotRepository.findAll().get(0);
        assertEquals(2, savedSnapshot.getAccounts().size());

        // WHEN
        savedSnapshot.removeAccount(liabilityAccount);

        // THEN
        final Snapshot actualSnapshot = snapshotRepository.findAll().get(0);
        assertEquals(1, actualSnapshot.getAccounts().size());
    }

    @Test
    @Transactional
    public void updateAccountInSnapshot() {
        // GIVEN
        saveNewUserWithAccounts();
        initSnapshot();

        user.addSnapshot(snapshot);

        final Snapshot savedSnapshot = snapshotRepository.findAll().get(0);

        assertEquals(2, savedSnapshot.getAccounts().size());
        assertEquals(ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-319900.00")), savedSnapshot.getNetWorth());

        // WHEN
        savedSnapshot.putAccount(liabilityAccount, liabilityAmount.add(new BigDecimal("100000.00")));

        // THEN
        final Snapshot actualSnapshot = snapshotRepository.findAll().get(0);
        assertEquals(2, actualSnapshot.getAccounts().size());
        assertEquals(ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-419900.00")), actualSnapshot.getNetWorth());
    }

    @Test
    @Transactional
    public void removeSnapshotFromPersistedUser() {
        // GIVEN
        saveNewUserWithAccounts();
        initSnapshot();

        user.addSnapshot(snapshot);

        userService.save(user);

        assertFalse(accountRepository.findAll().isEmpty());
        assertFalse(accountRepository.findByUser(user).isEmpty());
        assertFalse(snapshotRepository.findAll().isEmpty());

        // WHEN
        final User persistedUser = userService.findByEmail(EMAIL);
        final Snapshot savedSnapshot = persistedUser.getSnapshots().iterator().next();
        persistedUser.removeSnapshot(savedSnapshot);

        // THEN
        final User actualUser = userService.findByEmail(EMAIL);
        assertTrue(actualUser.getSnapshots().isEmpty());

        assertFalse(accountRepository.findAll().isEmpty());
        assertFalse(accountRepository.findByUser(actualUser).isEmpty());
        assertTrue(snapshotRepository.findAll().isEmpty());
    }

    private void saveNewUserWithAccounts() {
        assertNull(user.getId());

        user.addAccount(assetAccount);
        user.addAccount(liabilityAccount);
        userService.save(user);

        final List<Account> allAccounts = accountRepository.findAll();
        final List<Account> userAccounts = accountRepository.findByUser(user);

        assertFalse(allAccounts.isEmpty());
        assertTrue(allAccounts.contains(assetAccount));
        assertTrue(allAccounts.contains(liabilityAccount));

        assertFalse(userAccounts.isEmpty());
        assertTrue(userAccounts.contains(assetAccount));
        assertTrue(userAccounts.contains(liabilityAccount));
    }

    private void initSnapshot() {
        snapshot = new Snapshot(LocalDate.now(),
                                ImmutableMap.of(assetAccount, assetAmount, liabilityAccount, liabilityAmount));
        assertNull(snapshot.getId());
    }
}
