package br.net.du.sztoks.service;

import static br.net.du.sztoks.test.ModelTestUtils.buildUser;
import static br.net.du.sztoks.test.ModelTestUtils.equalsIgnoreIdAndDate;
import static br.net.du.sztoks.test.TestConstants.ANOTHER_CURRENCY_UNIT;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static br.net.du.sztoks.test.TestConstants.FOURTH_SNAPSHOT_MONTH;
import static br.net.du.sztoks.test.TestConstants.FOURTH_SNAPSHOT_YEAR;
import static br.net.du.sztoks.test.TestConstants.SECOND_SNAPSHOT_MONTH;
import static br.net.du.sztoks.test.TestConstants.SECOND_SNAPSHOT_YEAR;
import static br.net.du.sztoks.test.TestConstants.THIRD_SNAPSHOT_MONTH;
import static br.net.du.sztoks.test.TestConstants.THIRD_SNAPSHOT_YEAR;
import static br.net.du.sztoks.test.TestConstants.TITHING_PERCENTAGE;
import static br.net.du.sztoks.test.TestConstants.newCreditCardAccount;
import static br.net.du.sztoks.test.TestConstants.newInvestmentAccount;
import static br.net.du.sztoks.test.TestConstants.newRecurringIncome;
import static br.net.du.sztoks.test.TestConstants.newRecurringNonTaxDeductibleDonation;
import static br.net.du.sztoks.test.TestConstants.newSimpleAssetAccount;
import static br.net.du.sztoks.test.TestConstants.newSimpleLiabilityAccount;
import static br.net.du.sztoks.test.TestConstants.newSingleIncome;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import br.net.du.sztoks.exception.SztoksException;
import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.User;
import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.transaction.Transaction;
import br.net.du.sztoks.persistence.SnapshotRepository;
import br.net.du.sztoks.test.ModelTestUtils;
import br.net.du.sztoks.test.TestConstants;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.SortedSet;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class SnapshotServiceTest {

    private SnapshotService snapshotService;

    @Mock private SnapshotRepository snapshotRepository;

    @Mock private UserService userService;

    private User user;

    private Snapshot snapshot;

    @BeforeEach
    public void setUp() {
        initMocks(this);

        user = buildUser();

        snapshot = user.getSnapshots().first();

        snapshot.addAccount(newSimpleAssetAccount(CurrencyUnit.USD));
        snapshot.addAccount(newSimpleLiabilityAccount(CurrencyUnit.USD));
        snapshot.addAccount(newCreditCardAccount());
        snapshot.addAccount(newInvestmentAccount());

        snapshot.addTransaction(newRecurringIncome());
        snapshot.addTransaction(newSingleIncome(CurrencyUnit.USD));
        snapshot.addTransaction(newRecurringNonTaxDeductibleDonation(CurrencyUnit.USD));
        snapshot.addTransaction(TestConstants.newSingleTaxDeductibleDonation(CurrencyUnit.USD));

        snapshot.putCurrencyConversionRate(ANOTHER_CURRENCY_UNIT, new BigDecimal("1.31"));

        snapshotService = new SnapshotService(snapshotRepository, userService);
    }

    @Test
    public void newSnapshot_happy() throws Exception {
        // WHEN
        final Snapshot newSnapshot =
                snapshotService.newSnapshot(user, SECOND_SNAPSHOT_YEAR, SECOND_SNAPSHOT_MONTH);

        // THEN
        assertEquals(snapshot.getBaseCurrencyUnit(), newSnapshot.getBaseCurrencyUnit());
        assertThat(
                snapshot.getDefaultTithingPercentage(),
                comparesEqualTo(newSnapshot.getDefaultTithingPercentage()));
        assertTrue(newSnapshot.supports(CURRENCY_UNIT));
        assertTrue(newSnapshot.supports(ANOTHER_CURRENCY_UNIT));
        assertFalse(newSnapshot.supports(CurrencyUnit.EUR));

        assertEquals(1, newSnapshot.getCurrencyConversionRates().size());
        assertEquals(
                ANOTHER_CURRENCY_UNIT.toString(),
                newSnapshot.getCurrencyConversionRates().keySet().iterator().next());
        assertThat(
                newSnapshot.getCurrencyConversionRates().get(ANOTHER_CURRENCY_UNIT.toString()),
                comparesEqualTo(new BigDecimal("1.31")));

        final SortedSet<Account> originalAccounts = snapshot.getAccounts();
        final SortedSet<Account> newAccounts = newSnapshot.getAccounts();
        assertEquals(originalAccounts.size(), newAccounts.size());

        for (final Account originalAccount : originalAccounts) {
            boolean found = false;
            for (final Account newAccount : newAccounts) {
                final Class<? extends Account> newAccountClass = newAccount.getClass();
                if (newAccountClass.isInstance(originalAccount)) {
                    final Method equalsIgnoreId =
                            ModelTestUtils.class.getMethod(
                                    "equalsIgnoreId", newAccountClass, newAccountClass);
                    found = (Boolean) equalsIgnoreId.invoke(null, newAccount, originalAccount);
                    break;
                }
            }
            assertTrue(found);
        }

        // Only recurring Transactions are copied
        final SortedSet<Transaction> originalTransactions = snapshot.getTransactions();
        final SortedSet<Transaction> newTransactions = newSnapshot.getTransactions();

        assertEquals(4, originalTransactions.size());
        assertEquals(2, newTransactions.size());

        final Iterator<Transaction> iterator = newTransactions.iterator();
        assertTrue(
                equalsIgnoreIdAndDate(
                        newRecurringNonTaxDeductibleDonation(CurrencyUnit.USD), iterator.next()));
        assertTrue(equalsIgnoreIdAndDate(newRecurringIncome(), iterator.next()));

        verify(snapshotRepository).save(newSnapshot);

        assertNull(snapshot.getPrevious());

        assertEquals(newSnapshot, snapshot.getNext());
        assertEquals(snapshot, newSnapshot.getPrevious());
        assertNull(newSnapshot.getNext());
    }

    @Test
    public void newSnapshot_twice_properlySetsNextAndPrevious() {
        // WHEN
        final Snapshot secondSnapshot =
                snapshotService.newSnapshot(user, SECOND_SNAPSHOT_YEAR, SECOND_SNAPSHOT_MONTH);
        final Snapshot thirdSnapshot =
                snapshotService.newSnapshot(user, THIRD_SNAPSHOT_YEAR, THIRD_SNAPSHOT_MONTH);

        // THEN
        verify(snapshotRepository, times(1)).save(eq(secondSnapshot));
        verify(snapshotRepository, times(1)).save(eq(thirdSnapshot));

        assertNull(snapshot.getPrevious());
        assertEquals(secondSnapshot, snapshot.getNext());

        assertEquals(snapshot, secondSnapshot.getPrevious());
        assertEquals(thirdSnapshot, secondSnapshot.getNext());

        assertEquals(secondSnapshot, thirdSnapshot.getPrevious());
        assertNull(thirdSnapshot.getNext());
    }

    @Test
    public void deleteSnapshot_first_throws() {
        // GIVEN
        final Snapshot secondSnapshot =
                newEmptySnapshot(SECOND_SNAPSHOT_YEAR, SECOND_SNAPSHOT_MONTH);
        secondSnapshot.setId(snapshot.getId() + 1);
        user.addSnapshot(secondSnapshot);

        assertEquals(2, user.getSnapshots().size());

        // THEN
        assertThrows(
                SztoksException.class,
                () -> {
                    snapshotService.deleteSnapshot(user, snapshot);
                });

        assertEquals(2, user.getSnapshots().size());
    }

    @Test
    public void deleteSnapshot_last_happy() {
        // GIVEN
        final Snapshot secondSnapshot =
                newEmptySnapshot(SECOND_SNAPSHOT_YEAR, SECOND_SNAPSHOT_MONTH);
        secondSnapshot.setId(snapshot.getId() + 1);
        user.addSnapshot(secondSnapshot);

        assertEquals(2, user.getSnapshots().size());

        // WHEN
        snapshotService.deleteSnapshot(user, secondSnapshot);

        // THEN
        assertEquals(1, user.getSnapshots().size());
        assertEquals(snapshot, user.getSnapshots().first());

        assertNull(snapshot.getPrevious());
        assertNull(snapshot.getNext());
    }

    @Test
    public void deleteSnapshot_middle_throws() {
        // GIVEN
        final Snapshot secondSnapshot =
                newEmptySnapshot(SECOND_SNAPSHOT_YEAR, SECOND_SNAPSHOT_MONTH);
        secondSnapshot.setId(snapshot.getId() + 1);
        user.addSnapshot(secondSnapshot);

        final Snapshot thirdSnapshot = newEmptySnapshot(THIRD_SNAPSHOT_YEAR, THIRD_SNAPSHOT_MONTH);
        thirdSnapshot.setId(secondSnapshot.getId() + 1);
        user.addSnapshot(thirdSnapshot);

        final Snapshot fourthSnapshot =
                newEmptySnapshot(FOURTH_SNAPSHOT_YEAR, FOURTH_SNAPSHOT_MONTH);
        fourthSnapshot.setId(thirdSnapshot.getId() + 1);
        user.addSnapshot(fourthSnapshot);

        assertEquals(4, user.getSnapshots().size());

        // THEN
        assertThrows(
                SztoksException.class,
                () -> {
                    snapshotService.deleteSnapshot(user, thirdSnapshot);
                });

        assertEquals(4, user.getSnapshots().size());
    }

    @Test
    public void deleteSnapshot_onlyRemainingSnapshot() {
        // GIVEN
        assertEquals(1, user.getSnapshots().size());

        // THEN
        assertThrows(
                SztoksException.class,
                () -> {
                    snapshotService.deleteSnapshot(user, snapshot);
                });

        assertEquals(1, user.getSnapshots().size());
    }

    private Snapshot newEmptySnapshot(final int year, final int month) {
        return new Snapshot(
                year,
                month,
                CURRENCY_UNIT,
                TITHING_PERCENTAGE,
                ImmutableSortedSet.of(),
                ImmutableList.of(),
                ImmutableMap.of());
    }
}
