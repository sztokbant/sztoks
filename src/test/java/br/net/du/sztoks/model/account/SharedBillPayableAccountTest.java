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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SharedBillPayableAccountTest {

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

    private final SharedBillPayableAccount sharedBillPayableAccount =
            new SharedBillPayableAccount(
                    "My Shared Bill",
                    DEFAULT_CURRENCY_UNIT,
                    LocalDate.now(),
                    new BigDecimal("9.95"),
                    false,
                    1,
                    1);

    @BeforeEach
    public void setUp() {
        snapshot.addAccount(sharedBillPayableAccount);
    }

    @Test
    public void getBalance_happy() {
        // WHEN
        final BigDecimal actualBalance = sharedBillPayableAccount.getBalance();

        // THEN
        assertThat(actualBalance, is(new BigDecimal("4.97500000")));
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("-4.98")));
    }

    @Test
    public void getBalance_isPaid_isZero() {
        // WHEN
        sharedBillPayableAccount.setIsPaid(true);

        // THEN
        assertThat(sharedBillPayableAccount.getBalance(), is(BigDecimal.ZERO));
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("0.00")));
    }
}
