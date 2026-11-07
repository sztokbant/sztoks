package br.net.du.sztoks.model.account;

import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static br.net.du.sztoks.test.TestConstants.FIRST_SNAPSHOT_MONTH;
import static br.net.du.sztoks.test.TestConstants.FIRST_SNAPSHOT_YEAR;
import static br.net.du.sztoks.test.TestConstants.TITHING_PERCENTAGE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;

import br.net.du.sztoks.model.Snapshot;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdditionalCurrencyAccountTest {

    private static final CurrencyUnit EUR = CurrencyUnit.EUR;

    private Snapshot snapshot;

    @BeforeEach
    void setUp() {
        snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of(EUR.getCode(), new BigDecimal("0.80")));
    }

    @Test
    void setShares_updatesBalanceAndValueInBase() {
        final AdditionalCurrencyAccount account =
                new AdditionalCurrencyAccount(
                        "EUR cash",
                        EUR,
                        FutureTithingPolicy.NONE,
                        LocalDate.now(),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO);
        snapshot.addAccount(account);

        account.setShares(new BigDecimal("80.00"));
        account.setAmountInvested(new BigDecimal("100.00"));

        // 80 EUR / 0.80 = 100 USD
        assertThat(account.getBalance(), comparesEqualTo(new BigDecimal("80.00")));
        assertThat(
                account.getValueInBaseCurrency(), comparesEqualTo(new BigDecimal("100.00000000")));
        assertThat(
                account.getAveragePurchasePrice(), comparesEqualTo(new BigDecimal("1.25000000")));
        assertThat(account.getProfitPercentage(), comparesEqualTo(BigDecimal.ZERO));
    }

    @Test
    void onSnapshotBaseCurrencyChanged_invertsWhenAccountCurrencyBecomesBase() {
        final AdditionalCurrencyAccount account =
                new AdditionalCurrencyAccount(
                        "EUR cash",
                        EUR,
                        FutureTithingPolicy.NONE,
                        LocalDate.now(),
                        new BigDecimal("80.00"),
                        new BigDecimal("100.00"));
        snapshot.addAccount(account);

        // 1 USD = 0.80 EUR → becoming EUR base: shares 80 EUR → 100 USD, invested 100 USD → 80 EUR
        account.onSnapshotBaseCurrencyChanged(CURRENCY_UNIT, EUR, new BigDecimal("0.80"));

        assertThat(account.getCurrencyUnit(), comparesEqualTo(CURRENCY_UNIT));
        assertThat(account.getShares(), comparesEqualTo(new BigDecimal("100.00000000")));
        assertThat(account.getAmountInvested(), comparesEqualTo(new BigDecimal("80.00000000")));
    }

    @Test
    void changeBaseCurrency_roundTripUsdEurUsd_doesNotThrow() {
        final AdditionalCurrencyAccount eurHolding =
                new AdditionalCurrencyAccount(
                        "EUR Safe",
                        EUR,
                        FutureTithingPolicy.NONE,
                        LocalDate.now(),
                        new BigDecimal("80.00"),
                        new BigDecimal("100.00"));
        snapshot.addAccount(eurHolding);

        // USD -> EUR
        snapshot.changeBaseCurrencyUnitTo(EUR);
        assertThat(snapshot.getBaseCurrencyUnit(), comparesEqualTo(EUR));
        // Holding inverted into previous base (USD)
        assertThat(eurHolding.getCurrencyUnit(), comparesEqualTo(CURRENCY_UNIT));

        // EUR -> USD
        snapshot.changeBaseCurrencyUnitTo(CURRENCY_UNIT);
        assertThat(snapshot.getBaseCurrencyUnit(), comparesEqualTo(CURRENCY_UNIT));
        // Holding inverted back into EUR
        assertThat(eurHolding.getCurrencyUnit(), comparesEqualTo(EUR));
        // Quantity restored (80 EUR)
        assertThat(eurHolding.getShares(), comparesEqualTo(new BigDecimal("80.00000000")));
    }
}
