package br.net.du.sztoks.model.account;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import br.net.du.sztoks.model.Snapshot;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.Test;

class SharedBillReceivableAccountTest {

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

    private final SharedBillReceivableAccount populatedSharedBillReceivableAccount =
            new SharedBillReceivableAccount(
                    "My Shared Bill",
                    DEFAULT_CURRENCY_UNIT,
                    FutureTithingPolicy.ALL,
                    LocalDate.now(),
                    new BigDecimal("9.95"),
                    false,
                    1,
                    1);

    private final SharedBillReceivableAccount unpopulatedSharedBillReceivableAccount =
            new SharedBillReceivableAccount(
                    "My Shared Bill", DEFAULT_CURRENCY_UNIT, FutureTithingPolicy.ALL);

    @Test
    public void getBalance_addedUnpopulatedAccount_happy() {
        // GIVEN
        snapshot.addAccount(unpopulatedSharedBillReceivableAccount);
        unpopulatedSharedBillReceivableAccount.setBillAmount(new BigDecimal("9.95"));

        // WHEN
        final BigDecimal actualBalance = unpopulatedSharedBillReceivableAccount.getBalance();

        // THEN
        assertThat(actualBalance, is(new BigDecimal("4.97500000")));
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("4.48")));
    }

    @Test
    public void getBalance_addedUnpopulatedAccountThenSetIsPaid_balanceIsZero() {
        // GIVEN
        snapshot.addAccount(unpopulatedSharedBillReceivableAccount);
        unpopulatedSharedBillReceivableAccount.setBillAmount(new BigDecimal("9.95"));

        // WHEN
        unpopulatedSharedBillReceivableAccount.setIsPaid(true);

        // THEN
        assertThat(unpopulatedSharedBillReceivableAccount.getBalance(), is(BigDecimal.ZERO));
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("0.00")));
    }

    @Test
    public void getBalance_addedPopulatedAccount_happy() {
        // GIVEN
        snapshot.addAccount(populatedSharedBillReceivableAccount);

        // WHEN
        final BigDecimal actualBalance = populatedSharedBillReceivableAccount.getBalance();

        // THEN
        assertThat(actualBalance, is(new BigDecimal("4.97500000")));
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("4.48")));
    }

    @Test
    public void getBalance_addedPopulatedAccountThenSetIsPaid_balanceIsZero() {
        // GIVEN
        snapshot.addAccount(populatedSharedBillReceivableAccount);

        // WHEN
        populatedSharedBillReceivableAccount.setIsPaid(true);

        // THEN
        assertThat(populatedSharedBillReceivableAccount.getBalance(), is(BigDecimal.ZERO));
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("0.00")));
    }
}
