package br.net.du.sztoks.model.account;

import static org.hamcrest.MatcherAssert.assertThat;
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
import org.junit.jupiter.api.Test;

class FutureTithingAccountTest {

    public static final CurrencyUnit DEFAULT_CURRENCY_UNIT = CurrencyUnit.USD;
    private final Snapshot snapshot =
            new Snapshot(
                    2022,
                    10,
                    DEFAULT_CURRENCY_UNIT,
                    BigDecimal.TEN,
                    ImmutableSortedSet.of(),
                    ImmutableList.of(),
                    ImmutableMap.of());

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
        assertThat(actualFutureTithingAccount.getBalance(), is(new BigDecimal("0.49750000")));

        assertFalse(actualFutureTithingAccount == newFutureTithingAccount);
    }
}
