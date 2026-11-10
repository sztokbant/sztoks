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
    void onSnapshotBaseCurrencyChanged_keepsCurrencyAndShares_restatesAmountInvested() {
        final AdditionalCurrencyAccount account =
                new AdditionalCurrencyAccount(
                        "EUR cash",
                        EUR,
                        FutureTithingPolicy.NONE,
                        LocalDate.now(),
                        new BigDecimal("80.00"),
                        new BigDecimal("100.00"));
        snapshot.addAccount(account);

        // 1 USD = 0.80 EUR → base becomes EUR: cost 100 USD → 80 EUR; still 80 EUR shares
        account.onSnapshotBaseCurrencyChanged(new BigDecimal("0.80"));

        assertThat(account.getCurrencyUnit(), comparesEqualTo(EUR));
        assertThat(account.getShares(), comparesEqualTo(new BigDecimal("80.00")));
        assertThat(account.getAmountInvested(), comparesEqualTo(new BigDecimal("80.00000000")));
    }

    @Test
    void changeBaseCurrency_roundTripUsdEurUsd_keepsAccountCurrency() {
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
        assertThat(eurHolding.getCurrencyUnit(), comparesEqualTo(EUR));
        assertThat(eurHolding.getShares(), comparesEqualTo(new BigDecimal("80.00")));
        assertThat(eurHolding.getAmountInvested(), comparesEqualTo(new BigDecimal("80.00000000")));

        // EUR -> USD (rate for USD is 1/0.8 = 1.25 after first flip)
        snapshot.changeBaseCurrencyUnitTo(CURRENCY_UNIT);
        assertThat(snapshot.getBaseCurrencyUnit(), comparesEqualTo(CURRENCY_UNIT));
        assertThat(eurHolding.getCurrencyUnit(), comparesEqualTo(EUR));
        assertThat(eurHolding.getShares(), comparesEqualTo(new BigDecimal("80.00")));
        assertThat(eurHolding.getAmountInvested(), comparesEqualTo(new BigDecimal("100.00000000")));
    }

    @Test
    void setShares_profitsOnly_updatesFutureTithingByProfitDiff() {
        final AdditionalCurrencyAccount account =
                new AdditionalCurrencyAccount(
                        "EUR Safe",
                        EUR,
                        FutureTithingPolicy.PROFITS_ONLY,
                        LocalDate.now(),
                        BigDecimal.ZERO,
                        new BigDecimal("100.00")); // base cost
        snapshot.addAccount(account);

        account.setShares(
                new BigDecimal("80.00")); // value in base = 100, profit in account ccy = 0

        final FutureTithingAccount fta = snapshot.getFutureTithingAccount(EUR);
        // profitInAccountCurrency = 80 - 100*0.80 = 0
        assertThat(fta.getReferenceAmount(), comparesEqualTo(BigDecimal.ZERO));

        account.setShares(new BigDecimal("96.00")); // value base = 120, profit ccy = 96 - 80 = 16
        assertThat(fta.getReferenceAmount(), comparesEqualTo(new BigDecimal("16.00000000")));
    }

    @Test
    void putCurrencyConversionRate_profitsOnly_updatesFutureTithingInvariant() {
        final AdditionalCurrencyAccount account =
                new AdditionalCurrencyAccount(
                        "EUR Safe",
                        EUR,
                        FutureTithingPolicy.PROFITS_ONLY,
                        LocalDate.now(),
                        new BigDecimal("80.00"),
                        new BigDecimal("100.00")); // base cost
        snapshot.addAccount(account);
        // rate 0.80 → cost in EUR = 80, profit = 0
        final FutureTithingAccount fta = snapshot.getFutureTithingAccount(EUR);
        assertThat(fta.getReferenceAmount(), comparesEqualTo(BigDecimal.ZERO));
        assertThat(account.getFutureTithingReferenceAmount(), comparesEqualTo(BigDecimal.ZERO));

        // Cheaper EUR in USD terms: 1 USD = 0.50 EUR → cost in EUR = 50, profit = 30
        snapshot.putCurrencyConversionRate(EUR, new BigDecimal("0.50"));

        assertThat(
                account.getFutureTithingReferenceAmount(),
                comparesEqualTo(new BigDecimal("30.00000000")));
        // Stored FTA should match (this is what fails today — still 0)
        assertThat(
                fta.getReferenceAmount(),
                comparesEqualTo(account.getFutureTithingReferenceAmount()));
    }
}
