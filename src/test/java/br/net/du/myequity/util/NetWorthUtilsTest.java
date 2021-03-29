package br.net.du.myequity.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.SimpleAssetSnapshot;
import br.net.du.myequity.model.snapshot.SimpleLiabilitySnapshot;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
import java.util.Map;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NetWorthUtilsTest {

    private SimpleAssetSnapshot simpleAssetAccount;
    private BigDecimal assetAmount;
    private SimpleLiabilitySnapshot simpleLiabilityAccount;
    private BigDecimal liabilityAmount;
    private BigDecimal expectedNetWorth;

    @BeforeEach
    public void setUp() {
        assetAmount = new BigDecimal("100.00");
        simpleAssetAccount =
                new SimpleAssetSnapshot("Asset Account", CurrencyUnit.USD, assetAmount);
        liabilityAmount = new BigDecimal("320000.00");
        simpleLiabilityAccount =
                new SimpleLiabilitySnapshot("Liability Account", CurrencyUnit.USD, liabilityAmount);
        expectedNetWorth = new BigDecimal("-319900.00");
    }

    @Test
    public void computeByCurrency_fromAccountSet_singleCurrency() {
        // GIVEN
        final ImmutableSortedSet<AccountSnapshot> accountSnapshots =
                ImmutableSortedSet.of(simpleAssetAccount, simpleLiabilityAccount);

        // WHEN
        final Map<CurrencyUnit, BigDecimal> netWorthByCurrency =
                NetWorthUtils.breakDownAccountSnapshotsByCurrency(accountSnapshots);

        // THEN
        assertEquals(1, netWorthByCurrency.size());
        assertTrue(netWorthByCurrency.containsKey(CurrencyUnit.USD));
        final BigDecimal netWorthUsd = netWorthByCurrency.get(CurrencyUnit.USD);
        assertEquals(expectedNetWorth, netWorthUsd);
    }

    @Test
    public void computeByCurrency_fromAccountSet_multipleCurrencies() {
        // GIVEN
        final CurrencyUnit brl = CurrencyUnit.of("BRL");
        final BigDecimal brlAssetAmount = new BigDecimal("700000.00");
        final BigDecimal brlLiabilityAmount = new BigDecimal("150000.00");

        final ImmutableSortedSet<AccountSnapshot> accountSnapshots =
                ImmutableSortedSet.of(
                        simpleAssetAccount,
                        simpleLiabilityAccount,
                        new SimpleAssetSnapshot("BRL Asset Account", brl, brlAssetAmount),
                        new SimpleLiabilitySnapshot(
                                "BRL Liability Account", brl, brlLiabilityAmount));

        // WHEN
        final Map<CurrencyUnit, BigDecimal> netWorthByCurrency =
                NetWorthUtils.breakDownAccountSnapshotsByCurrency(accountSnapshots);

        // THEN
        assertEquals(2, netWorthByCurrency.size());
        assertTrue(netWorthByCurrency.containsKey(CurrencyUnit.USD));
        assertTrue(netWorthByCurrency.containsKey(brl));
        final BigDecimal netWorthUsd = netWorthByCurrency.get(CurrencyUnit.USD);
        assertEquals(expectedNetWorth, netWorthUsd);
        final BigDecimal netWorthBrl = netWorthByCurrency.get(brl);
        assertEquals(new BigDecimal("550000.00"), netWorthBrl);
    }
}
