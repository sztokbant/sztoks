package br.net.du.sztoks.model.account;

import static br.net.du.sztoks.test.ModelTestUtils.equalsIgnoreId;
import static br.net.du.sztoks.test.TestConstants.ACCOUNT_NAME;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static br.net.du.sztoks.test.TestConstants.FIRST_SNAPSHOT_MONTH;
import static br.net.du.sztoks.test.TestConstants.FIRST_SNAPSHOT_YEAR;
import static br.net.du.sztoks.test.TestConstants.TITHING_PERCENTAGE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import br.net.du.sztoks.model.Snapshot;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class ReceivableAccountTest {

    @Test
    public void newEmptySnapshot_happy() {
        // GIVEN
        final LocalDate now = LocalDate.of(2020, 12, 31);

        // WHEN
        final ReceivableAccount actual;
        try (MockedStatic<LocalDate> localDateStaticMock = mockStatic(LocalDate.class)) {
            localDateStaticMock.when(LocalDate::now).thenReturn(now);
            actual =
                    new ReceivableAccount(
                            ACCOUNT_NAME,
                            CURRENCY_UNIT,
                            FutureTithingPolicy.NONE,
                            now,
                            now,
                            BigDecimal.ZERO,
                            false);
        }

        // THEN
        final ReceivableAccount expected =
                new ReceivableAccount(
                        ACCOUNT_NAME,
                        CURRENCY_UNIT,
                        FutureTithingPolicy.NONE,
                        now,
                        now,
                        BigDecimal.ZERO,
                        false);

        assertTrue(equalsIgnoreId(actual, expected));
    }

    @Test
    public void receivable_setIsPaid_updatesFutureTithingReference() {
        final Snapshot snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of());

        final ReceivableAccount receivable =
                new ReceivableAccount(
                        "Loan to friend",
                        CURRENCY_UNIT,
                        FutureTithingPolicy.ALL,
                        LocalDate.now(),
                        LocalDate.now(),
                        new BigDecimal("1000.00"),
                        false);

        snapshot.addAccount(receivable);

        final FutureTithingAccount fta = snapshot.getFutureTithingAccount(CURRENCY_UNIT);
        assertThat(fta.getReferenceAmount(), comparesEqualTo(new BigDecimal("1000.00")));

        receivable.setIsPaid(true);

        assertThat(fta.getReferenceAmount(), comparesEqualTo(BigDecimal.ZERO));
    }

    @Test
    public void receivable_setBillAmount_updatesFutureTithingReference() {
        final Snapshot snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of());

        final ReceivableAccount receivable =
                new ReceivableAccount(
                        "Loan to friend",
                        CURRENCY_UNIT,
                        FutureTithingPolicy.ALL,
                        LocalDate.now(),
                        LocalDate.now(),
                        new BigDecimal("1000.00"),
                        false);

        snapshot.addAccount(receivable);
        final FutureTithingAccount fta = snapshot.getFutureTithingAccount(CURRENCY_UNIT);

        receivable.setBillAmount(new BigDecimal("400.00"));

        assertThat(fta.getReferenceAmount(), comparesEqualTo(new BigDecimal("400.00")));
    }
}
