package br.net.du.sztoks.model.account;

import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static br.net.du.sztoks.test.TestConstants.FIRST_SNAPSHOT_MONTH;
import static br.net.du.sztoks.test.TestConstants.FIRST_SNAPSHOT_YEAR;
import static br.net.du.sztoks.test.TestConstants.TITHING_PERCENTAGE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.sztoks.model.Snapshot;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FutureTithingAccountTest {

    public static final CurrencyUnit DEFAULT_CURRENCY_UNIT = CurrencyUnit.USD;

    private Snapshot snapshot;
    private FutureTithingAccount futureTithingAccount;

    @BeforeEach
    public void setUp() {
        snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of());

        // Attaches FTA to the snapshot (reference starts at 0)
        futureTithingAccount = snapshot.getFutureTithingAccount(CURRENCY_UNIT);
    }

    private final SharedBillReceivableAccount unpopulatedSharedBillReceivableAccount =
            new SharedBillReceivableAccount(
                    "My Shared Bill", DEFAULT_CURRENCY_UNIT, FutureTithingPolicy.ALL);

    @Test
    public void snapshotAddAccount_secondFutureTithingAccountForCurrency_noop() {
        // GIVEN
        snapshot.addAccount(unpopulatedSharedBillReceivableAccount);
        unpopulatedSharedBillReceivableAccount.setBillAmount(new BigDecimal("9.95"));

        final FutureTithingAccount currentFutureTithingAccount =
                snapshot.getFutureTithingAccount(DEFAULT_CURRENCY_UNIT);

        // WHEN
        final FutureTithingAccount newFutureTithingAccount =
                new FutureTithingAccount(DEFAULT_CURRENCY_UNIT, LocalDate.now(), BigDecimal.ZERO);
        snapshot.addAccount(newFutureTithingAccount);

        // THEN
        final FutureTithingAccount actualFutureTithingAccount =
                snapshot.getFutureTithingAccount(DEFAULT_CURRENCY_UNIT);

        assertTrue(actualFutureTithingAccount == currentFutureTithingAccount);
        assertThat(actualFutureTithingAccount.getBalance(), is(new BigDecimal("0.74625000")));

        assertFalse(actualFutureTithingAccount == newFutureTithingAccount);
    }

    @Test
    public void setReferenceAmount_subScalePositiveDust_normalizedToZero() {
        futureTithingAccount.setReferenceAmount(new BigDecimal("4.4E-9"));

        assertThat(futureTithingAccount.getReferenceAmount(), comparesEqualTo(BigDecimal.ZERO));
        assertThat(futureTithingAccount.getBalance(), comparesEqualTo(BigDecimal.ZERO));
    }

    @Test
    public void setReferenceAmount_subScaleNegativeDust_normalizedToZero() {
        futureTithingAccount.setReferenceAmount(new BigDecimal("-4.4E-9"));

        assertThat(futureTithingAccount.getReferenceAmount(), comparesEqualTo(BigDecimal.ZERO));
        assertThat(futureTithingAccount.getBalance(), comparesEqualTo(BigDecimal.ZERO));
    }

    @Test
    public void setReferenceAmount_exactlyOneUnitAtDivisionScale_preserved() {
        // DIVISION_SCALE is 8 → 1e-8 must not be treated as dust
        final BigDecimal minNonDust = new BigDecimal("0.00000001");

        futureTithingAccount.setReferenceAmount(minNonDust);

        assertThat(futureTithingAccount.getReferenceAmount(), comparesEqualTo(minNonDust));
    }

    @Test
    public void setReferenceAmount_normalAmount_preservedAtDivisionScale() {
        futureTithingAccount.setReferenceAmount(new BigDecimal("1000.123456789"));

        assertThat(
                futureTithingAccount.getReferenceAmount(),
                comparesEqualTo(new BigDecimal("1000.12345679")));
    }

    @Test
    public void setReferenceAmount_addThenSubtractLeavingDust_endsAtZero() {
        futureTithingAccount.setReferenceAmount(new BigDecimal("100.00"));
        futureTithingAccount.setReferenceAmount(
                futureTithingAccount.getReferenceAmount().add(new BigDecimal("4.4E-9")));
        futureTithingAccount.setReferenceAmount(
                futureTithingAccount.getReferenceAmount().subtract(new BigDecimal("100.00")));

        assertThat(futureTithingAccount.getReferenceAmount(), comparesEqualTo(BigDecimal.ZERO));
    }

    @Test
    public void removeAccount_afterSubScaleDust_allowsFutureTithingAccountRemoval() {
        futureTithingAccount.setReferenceAmount(new BigDecimal("4.4E-9"));

        // Must not throw SztoksException (non-zero reference guard)
        snapshot.removeAccount(futureTithingAccount);

        assertThat(
                snapshot.getAccounts().stream().noneMatch(a -> a instanceof FutureTithingAccount),
                org.hamcrest.Matchers.is(true));
    }
}
