package br.net.du.sztoks.model.account;

import static br.net.du.sztoks.test.ModelTestUtils.equalsIgnoreId;
import static br.net.du.sztoks.test.TestConstants.ACCOUNT_NAME;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static br.net.du.sztoks.test.TestConstants.FIRST_SNAPSHOT_MONTH;
import static br.net.du.sztoks.test.TestConstants.FIRST_SNAPSHOT_YEAR;
import static br.net.du.sztoks.test.TestConstants.TITHING_PERCENTAGE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.sztoks.model.Snapshot;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class InvestmentAccountTest {

    @Test
    public void newEmptySnapshot_happy() {
        // WHEN
        final InvestmentAccount actual =
                new InvestmentAccount(
                        ACCOUNT_NAME,
                        CURRENCY_UNIT,
                        FutureTithingPolicy.NONE,
                        LocalDate.now(),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO);

        // THEN
        final InvestmentAccount expected =
                new InvestmentAccount(
                        ACCOUNT_NAME,
                        CURRENCY_UNIT,
                        FutureTithingPolicy.NONE,
                        LocalDate.now(),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO);

        assertTrue(equalsIgnoreId(actual, expected));
    }

    @Test
    public void constructor_negativeShares_throws() {
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new InvestmentAccount(
                                ACCOUNT_NAME,
                                CURRENCY_UNIT,
                                FutureTithingPolicy.NONE,
                                LocalDate.now(),
                                new BigDecimal("-0.00000001"),
                                BigDecimal.ZERO,
                                BigDecimal.ZERO));
    }

    @Test
    public void setShares_negative_throws() {
        final InvestmentAccount account =
                new InvestmentAccount(
                        ACCOUNT_NAME,
                        CURRENCY_UNIT,
                        FutureTithingPolicy.NONE,
                        LocalDate.now(),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO);

        assertThrows(
                IllegalArgumentException.class, () -> account.setShares(new BigDecimal("-1.00")));
    }

    @Test
    public void setShares_zero_ok() {
        final Snapshot snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of());

        final InvestmentAccount account =
                new InvestmentAccount(
                        ACCOUNT_NAME,
                        CURRENCY_UNIT,
                        FutureTithingPolicy.NONE,
                        LocalDate.now(),
                        new BigDecimal("10.00"),
                        new BigDecimal("100.00"),
                        new BigDecimal("12.00"));
        snapshot.addAccount(account);

        account.setShares(BigDecimal.ZERO);

        assertThat(account.getShares(), comparesEqualTo(BigDecimal.ZERO));
        assertThat(account.getBalance(), comparesEqualTo(BigDecimal.ZERO));
    }

    @Test
    public void setShares_positive_ok() {
        final Snapshot snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of());

        final InvestmentAccount account =
                new InvestmentAccount(
                        ACCOUNT_NAME,
                        CURRENCY_UNIT,
                        FutureTithingPolicy.NONE,
                        LocalDate.now(),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        new BigDecimal("10.00"));
        snapshot.addAccount(account);

        account.setShares(new BigDecimal("3.00"));

        assertThat(account.getShares(), comparesEqualTo(new BigDecimal("3.00")));
        assertThat(account.getBalance(), comparesEqualTo(new BigDecimal("30.00")));
    }
}
