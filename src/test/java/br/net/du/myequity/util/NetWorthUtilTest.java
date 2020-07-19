package br.net.du.myequity.util;

import br.net.du.myequity.model.Account;
import br.net.du.myequity.model.AccountType;
import com.google.common.collect.ImmutableSet;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NetWorthUtilTest {

    private Account assetAccount;
    private BigDecimal assetAmount;
    private Account liabilityAccount;
    private BigDecimal liabilityAmount;
    private BigDecimal expectedNetWorth;

    @BeforeEach
    public void setUp() {
        assetAccount = new Account("Asset Account", AccountType.ASSET, CurrencyUnit.USD);
        assetAmount = new BigDecimal("100.00");
        liabilityAccount = new Account("Liability Account", AccountType.LIABILITY, CurrencyUnit.USD);
        liabilityAmount = new BigDecimal("320000.00");
        expectedNetWorth = new BigDecimal("-319900.00");
    }

    @Test
    public void computeByCurrency_fromAccountSet_singleCurrency() {
        // GIVEN
        final Set<Map.Entry<Account, BigDecimal>> accounts =
                ImmutableSet.of((Map.Entry) new AbstractMap.SimpleEntry<>(assetAccount, assetAmount),
                                (Map.Entry) new AbstractMap.SimpleEntry<>(liabilityAccount, liabilityAmount));

        // WHEN
        final Map<CurrencyUnit, BigDecimal> netWorthByCurrency = NetWorthUtil.computeByCurrency(accounts);

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
        final Account brlAsset = new Account("BRL Asset Account", AccountType.ASSET, brl);
        final BigDecimal brlAssetAmount = new BigDecimal("700000.00");
        final Account brlLiability = new Account("BRL Liability Account", AccountType.LIABILITY, brl);
        final BigDecimal brlLiabilityAmount = new BigDecimal("150000.00");

        final Set<Map.Entry<Account, BigDecimal>> accounts =
                ImmutableSet.of((Map.Entry) new AbstractMap.SimpleEntry<>(assetAccount, assetAmount),
                                (Map.Entry) new AbstractMap.SimpleEntry<>(liabilityAccount, liabilityAmount),
                                (Map.Entry) new AbstractMap.SimpleEntry<>(brlAsset, brlAssetAmount),
                                (Map.Entry) new AbstractMap.SimpleEntry<>(brlLiability, brlLiabilityAmount));

        // WHEN
        final Map<CurrencyUnit, BigDecimal> netWorthByCurrency = NetWorthUtil.computeByCurrency(accounts);

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
