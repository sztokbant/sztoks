package br.net.du.myequity.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.SimpleAssetAccount;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
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

    private SimpleAssetAccount simpleAssetAccount;
    private BigDecimal assetAmount;
    private SimpleLiabilityAccount simpleLiabilityAccount;
    private BigDecimal liabilityAmount;
    private BigDecimal expectedNetWorth;

    @BeforeEach
    public void setUp() {
        simpleAssetAccount = new SimpleAssetAccount("Asset Account", CurrencyUnit.USD);
        assetAmount = new BigDecimal("100.00");
        simpleLiabilityAccount = new SimpleLiabilityAccount("Liability Account", CurrencyUnit.USD);
        liabilityAmount = new BigDecimal("320000.00");
        expectedNetWorth = new BigDecimal("-319900.00");
    }

    @Test
    public void computeByCurrency_fromAccountSet_singleCurrency() {
        // GIVEN
        final ImmutableSortedSet<AccountSnapshot> accountSnapshots =
                ImmutableSortedSet.of(
                        new SimpleAssetSnapshot(simpleAssetAccount, assetAmount),
                        new SimpleLiabilitySnapshot(simpleLiabilityAccount, liabilityAmount));

        // WHEN
        final Map<CurrencyUnit, BigDecimal> netWorthByCurrency =
                NetWorthUtils.computeByCurrency(accountSnapshots);

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
        final Account brlAsset = new SimpleAssetAccount("BRL Asset Account", brl);
        final BigDecimal brlAssetAmount = new BigDecimal("700000.00");
        final Account brlLiability = new SimpleLiabilityAccount("BRL Liability Account", brl);
        final BigDecimal brlLiabilityAmount = new BigDecimal("150000.00");

        final ImmutableSortedSet<AccountSnapshot> accountSnapshots =
                ImmutableSortedSet.of(
                        new SimpleAssetSnapshot(simpleAssetAccount, assetAmount),
                        new SimpleLiabilitySnapshot(simpleLiabilityAccount, liabilityAmount),
                        new SimpleAssetSnapshot(brlAsset, brlAssetAmount),
                        new SimpleLiabilitySnapshot(brlLiability, brlLiabilityAmount));

        // WHEN
        final Map<CurrencyUnit, BigDecimal> netWorthByCurrency =
                NetWorthUtils.computeByCurrency(accountSnapshots);

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
