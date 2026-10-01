package br.net.du.sztoks.model.transaction;

import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static br.net.du.sztoks.test.TestConstants.FIRST_SNAPSHOT_MONTH;
import static br.net.du.sztoks.test.TestConstants.FIRST_SNAPSHOT_YEAR;
import static br.net.du.sztoks.test.TestConstants.TITHING_PERCENTAGE;
import static br.net.du.sztoks.test.TestConstants.newRecurringIncome;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.sztoks.model.Snapshot;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class IncomeTest {

    @Test
    public void equals_differentIds() {
        // GIVEN
        final IncomeTransaction first = newRecurringIncome();
        first.setId(42L);
        final IncomeTransaction second = newRecurringIncome();
        second.setId(77L);

        // THEN
        assertFalse(first.equals(second));
    }

    @Test
    public void equals_sameIds() {
        // GIVEN
        final IncomeTransaction first = newRecurringIncome();
        first.setId(42L);
        final IncomeTransaction second = newRecurringIncome();
        second.setId(42L);

        // THEN
        assertTrue(first.equals(second));
    }

    @Test
    public void constructor_tithingPercentageNegative_throws() {
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new IncomeTransaction(
                                LocalDate.now(),
                                CURRENCY_UNIT.getCode(),
                                new BigDecimal("1000.00"),
                                "Job",
                                RecurrencePolicy.NONE,
                                new BigDecimal("-0.01"),
                                IncomeCategory.JOB));
    }

    @Test
    public void constructor_tithingPercentageGreaterThan100_throws() {
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new IncomeTransaction(
                                LocalDate.now(),
                                CURRENCY_UNIT.getCode(),
                                new BigDecimal("1000.00"),
                                "Job",
                                RecurrencePolicy.NONE,
                                new BigDecimal("100.01"),
                                IncomeCategory.JOB));
    }

    @Test
    public void constructor_tithingPercentageZero_ok() {
        final IncomeTransaction income =
                new IncomeTransaction(
                        LocalDate.now(),
                        CURRENCY_UNIT.getCode(),
                        new BigDecimal("1000.00"),
                        "Job",
                        RecurrencePolicy.NONE,
                        BigDecimal.ZERO,
                        IncomeCategory.JOB);

        assertThat(income.getTithingPercentage(), comparesEqualTo(BigDecimal.ZERO));
    }

    @Test
    public void constructor_tithingPercentage100_ok() {
        final IncomeTransaction income =
                new IncomeTransaction(
                        LocalDate.now(),
                        CURRENCY_UNIT.getCode(),
                        new BigDecimal("1000.00"),
                        "Job",
                        RecurrencePolicy.NONE,
                        new BigDecimal("100.00"),
                        IncomeCategory.JOB);

        assertThat(income.getTithingPercentage(), comparesEqualTo(new BigDecimal("100.00")));
    }

    @Test
    public void setTithingPercentage_negative_throws() {
        final IncomeTransaction income = newRecurringIncome();

        assertThrows(
                IllegalArgumentException.class,
                () -> income.setTithingPercentage(new BigDecimal("-1.00")));
    }

    @Test
    public void setTithingPercentage_greaterThan100_throws() {
        final IncomeTransaction income = newRecurringIncome();

        assertThrows(
                IllegalArgumentException.class,
                () -> income.setTithingPercentage(new BigDecimal("100.01")));
    }

    @Test
    public void setTithingPercentage_zeroAnd100_ok() {
        final Snapshot snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of());

        final IncomeTransaction income = newRecurringIncome();
        snapshot.addTransaction(income);

        income.setTithingPercentage(BigDecimal.ZERO);
        assertThat(income.getTithingPercentage(), comparesEqualTo(BigDecimal.ZERO));

        income.setTithingPercentage(new BigDecimal("100.00"));
        assertThat(income.getTithingPercentage(), comparesEqualTo(new BigDecimal("100.00")));
    }
}
