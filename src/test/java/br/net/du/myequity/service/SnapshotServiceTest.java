package br.net.du.myequity.service;

import static br.net.du.myequity.test.ModelTestUtils.buildUser;
import static br.net.du.myequity.test.TestConstants.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import br.net.du.myequity.exception.MyEquityException;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.CreditCardAccount;
import br.net.du.myequity.model.account.SimpleAssetAccount;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import br.net.du.myequity.model.snapshot.InvestmentSnapshot;
import br.net.du.myequity.model.snapshot.SimpleAssetSnapshot;
import br.net.du.myequity.model.snapshot.SimpleLiabilitySnapshot;
import br.net.du.myequity.persistence.SnapshotRepository;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.SortedSet;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class SnapshotServiceTest {

    private static final Account SIMPLE_ASSET_ACCOUNT =
            new SimpleAssetAccount("Savings", CurrencyUnit.USD, LocalDate.now());
    private static final AccountSnapshot SIMPLE_ASSET_SNAPSHOT =
            new SimpleAssetSnapshot(SIMPLE_ASSET_ACCOUNT, new BigDecimal("10000.00"));

    private static final Account SIMPLE_LIABILITY_ACCOUNT =
            new SimpleLiabilityAccount("Mortgage", CurrencyUnit.USD, LocalDate.now());
    private static final AccountSnapshot SIMPLE_LIABILITY_SNAPSHOT =
            new SimpleLiabilitySnapshot(SIMPLE_LIABILITY_ACCOUNT, new BigDecimal("2500.00"));

    private static final Account CREDIT_CARD_ACCOUNT =
            new CreditCardAccount("Chase Sapphire Reserve", CurrencyUnit.USD, LocalDate.now());
    private static final AccountSnapshot CREDIT_CARD_SNAPSHOT =
            new CreditCardSnapshot(
                    CREDIT_CARD_ACCOUNT,
                    new BigDecimal("10000.00"),
                    new BigDecimal("9500.00"),
                    new BigDecimal("1000.00"));

    private static final Account INVESTMENT_ACCOUNT =
            new CreditCardAccount("AMZN", CurrencyUnit.USD, LocalDate.now());
    private static final AccountSnapshot INVESTMENT_SNAPSHOT =
            new InvestmentSnapshot(
                    INVESTMENT_ACCOUNT,
                    new BigDecimal("175.00"),
                    new BigDecimal("1100.00"),
                    new BigDecimal("3500.00"));
    private static final long SNAPSHOT_INDEX = 1L;

    private SnapshotService snapshotService;

    @Mock private SnapshotRepository snapshotRepository;

    @Mock private UserService userService;

    private User user;

    private Snapshot snapshot;

    @BeforeEach
    public void setUp() {
        initMocks(this);

        user = buildUser();

        snapshot = new Snapshot(SNAPSHOT_INDEX, now, ImmutableSortedSet.of());
        snapshot.setId(108L);
        user.addSnapshot(snapshot);

        snapshot.addAccountSnapshot(SIMPLE_ASSET_SNAPSHOT);
        snapshot.addAccountSnapshot(SIMPLE_LIABILITY_SNAPSHOT);
        snapshot.addAccountSnapshot(CREDIT_CARD_SNAPSHOT);
        snapshot.addAccountSnapshot(INVESTMENT_SNAPSHOT);

        snapshotService = new SnapshotService(snapshotRepository, userService);
    }

    @Test
    public void newSnapshot_happy() {
        // WHEN
        final Snapshot newSnapshot = snapshotService.newSnapshot(user);

        // THEN
        final SortedSet<AccountSnapshot> originalAccountSnapshots = snapshot.getAccountSnapshots();
        final SortedSet<AccountSnapshot> newAccountSnapshots = newSnapshot.getAccountSnapshots();
        assertEquals(originalAccountSnapshots.size(), newAccountSnapshots.size());

        for (final AccountSnapshot originalAccountSnapshot : originalAccountSnapshots) {
            boolean found = false;
            for (final AccountSnapshot newAccountSnapshot : newAccountSnapshots) {
                if (newAccountSnapshot.getClass().isInstance(originalAccountSnapshot)) {
                    found = newAccountSnapshot.equalsIgnoreId(originalAccountSnapshot);
                    break;
                }
            }
            assertTrue(found);
        }

        verify(snapshotRepository).save(newSnapshot);
    }

    @Test
    public void deleteSnapshot_happy() {
        // GIVEN
        final Snapshot secondSnapshot =
                new Snapshot(SNAPSHOT_INDEX + 1, now, ImmutableSortedSet.of());
        secondSnapshot.setId(99L);
        user.addSnapshot(secondSnapshot);
        assertEquals(2, user.getSnapshots().size());

        // WHEN
        snapshotService.deleteSnapshot(user, snapshot);

        // THEN
        assertEquals(1, user.getSnapshots().size());
    }

    @Test
    public void deleteSnapshot_lastSnapshot() {
        // GIVEN
        assertEquals(1, user.getSnapshots().size());

        // THEN
        assertThrows(
                MyEquityException.class,
                () -> {
                    snapshotService.deleteSnapshot(user, snapshot);
                });

        assertEquals(1, user.getSnapshots().size());
    }
}
