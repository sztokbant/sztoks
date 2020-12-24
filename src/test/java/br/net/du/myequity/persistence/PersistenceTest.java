package br.net.du.myequity.persistence;

import static br.net.du.myequity.test.TestConstants.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.SimpleAssetAccount;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.SimpleAssetSnapshot;
import br.net.du.myequity.model.snapshot.SimpleLiabilitySnapshot;
import br.net.du.myequity.service.UserService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import javax.transaction.Transactional;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class PersistenceTest {
    private static final String EMAIL = "example@example.com";
    private static final String FIRST_NAME = "Bill";
    private static final String LAST_NAME = "Gates";
    private static final String PASSWORD = "password";

    @Autowired private UserService userService;

    @Autowired private AccountRepository accountRepository;

    @Autowired private SnapshotRepository snapshotRepository;

    private User user;

    private Snapshot snapshot;

    private SimpleAssetAccount simpleAssetAccount;
    private BigDecimal assetAmount;
    private SimpleLiabilityAccount simpleLiabilityAccount;
    private BigDecimal liabilityAmount;

    @BeforeEach
    public void setUp() {
        user = new User(EMAIL, FIRST_NAME, LAST_NAME);
        user.setPassword(PASSWORD);
        user.setPasswordConfirm(PASSWORD);

        simpleAssetAccount = new SimpleAssetAccount("Asset Account", CurrencyUnit.USD);
        assetAmount = new BigDecimal("100.00");
        simpleLiabilityAccount = new SimpleLiabilityAccount("Liability Account", CurrencyUnit.USD);
        liabilityAmount = new BigDecimal("320000.00");

        saveNewUserAndAddAccounts();
        initSnapshot();
    }

    @Test
    public void addSnapshotToPersistedUser() {
        // WHEN
        user.addSnapshot(snapshot);

        // THEN
        final User actualUser = userService.findByEmail(EMAIL);
        assertNotNull(actualUser.getId());
        assertEquals(user, actualUser);
        assertEquals(2, actualUser.getSnapshots().size());
        assertEquals(snapshot, user.getSnapshots().first());

        final Snapshot actualSnapshot = snapshotRepository.findAllByUser(user).get(1);
        assertNotNull(actualSnapshot.getId());
        assertNotNull(actualSnapshot.getUser());
        assertEquals(user, actualSnapshot.getUser());

        final Map<AccountType, SortedSet<AccountSnapshot>> accountSnapshotsByType =
                actualSnapshot.getAccountSnapshotsByType();
        assertEquals(2, accountSnapshotsByType.size());
        assertEquals(1, accountSnapshotsByType.get(AccountType.ASSET).size());
        assertEquals(1, accountSnapshotsByType.get(AccountType.LIABILITY).size());

        assertNotNull(simpleAssetAccount.getId());
        assertNotNull(simpleAssetAccount.getUser());
        assertEquals(user, simpleAssetAccount.getUser());
        final BigDecimal actualAssetAmount =
                accountSnapshotsByType.get(AccountType.ASSET).iterator().next().getTotal();
        assertEquals(assetAmount, actualAssetAmount);

        assertNotNull(simpleLiabilityAccount.getId());
        assertNotNull(simpleLiabilityAccount.getUser());
        assertEquals(user, simpleLiabilityAccount.getUser());
        final BigDecimal actualLiabilityAmount =
                accountSnapshotsByType.get(AccountType.LIABILITY).iterator().next().getTotal();
        assertEquals(liabilityAmount, actualLiabilityAmount);
    }

    @Test
    public void removeAccountFromSnapshot() {
        // GIVEN
        user.addSnapshot(snapshot);

        final Snapshot savedSnapshot = snapshotRepository.findAll().get(1);
        assertEquals(2, savedSnapshot.getAccountSnapshots().size());

        // WHEN
        savedSnapshot.removeAccountSnapshotFor(simpleLiabilityAccount);

        // THEN
        final Snapshot actualSnapshot = snapshotRepository.findAll().get(1);
        assertEquals(1, actualSnapshot.getAccountSnapshots().size());
    }

    @Test
    public void updateAccountInSnapshot() {
        // GIVEN
        user.addSnapshot(snapshot);

        final Snapshot savedSnapshot = snapshotRepository.findAll().get(1);

        assertEquals(2, savedSnapshot.getAccountSnapshots().size());
        assertEquals(
                ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-319900.00")),
                savedSnapshot.getNetWorth());

        // WHEN
        final SimpleLiabilitySnapshot simpleLiabilitySnapshot =
                (SimpleLiabilitySnapshot)
                        savedSnapshot.getAccountSnapshotFor(simpleLiabilityAccount).get();
        simpleLiabilitySnapshot.setAmount(
                simpleLiabilitySnapshot.getAmount().add(new BigDecimal("100000.00")));

        // THEN
        final Snapshot actualSnapshot = snapshotRepository.findAll().get(1);
        assertEquals(2, actualSnapshot.getAccountSnapshots().size());
        assertEquals(
                ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-419900.00")),
                actualSnapshot.getNetWorth());
    }

    @Test
    public void removeSnapshotFromPersistedUser() {
        // GIVEN
        user.addSnapshot(snapshot);

        assertFalse(accountRepository.findAll().isEmpty());
        assertFalse(accountRepository.findByUser(user).isEmpty());
        assertFalse(snapshotRepository.findAll().isEmpty());

        // WHEN
        final User persistedUser = userService.findByEmail(EMAIL);
        final Snapshot savedSnapshot = persistedUser.getSnapshots().iterator().next();
        persistedUser.removeSnapshot(savedSnapshot);

        // THEN
        final User actualUser = userService.findByEmail(EMAIL);
        assertEquals(1, actualUser.getSnapshots().size());

        assertFalse(accountRepository.findAll().isEmpty());
        assertFalse(accountRepository.findByUser(actualUser).isEmpty());
        assertEquals(1, snapshotRepository.findAll().size());
    }

    private void saveNewUserAndAddAccounts() {
        assertNull(user.getId());
        userService.save(user);

        user.addAccount(simpleAssetAccount);
        user.addAccount(simpleLiabilityAccount);

        final List<Account> allAccounts = accountRepository.findAll();
        final List<Account> userAccounts = accountRepository.findByUser(user);

        assertFalse(allAccounts.isEmpty());
        assertTrue(allAccounts.contains(simpleAssetAccount));
        assertTrue(allAccounts.contains(simpleLiabilityAccount));

        assertFalse(userAccounts.isEmpty());
        assertTrue(userAccounts.contains(simpleAssetAccount));
        assertTrue(userAccounts.contains(simpleLiabilityAccount));
    }

    private void initSnapshot() {
        snapshot =
                new Snapshot(
                        2L,
                        now,
                        ImmutableSortedSet.of(
                                new SimpleAssetSnapshot(simpleAssetAccount, assetAmount),
                                new SimpleLiabilitySnapshot(
                                        simpleLiabilityAccount, liabilityAmount)));
        assertNull(snapshot.getId());
    }
}
