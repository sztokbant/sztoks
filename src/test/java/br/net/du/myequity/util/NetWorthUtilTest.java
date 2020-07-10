package br.net.du.myequity.util;

import br.net.du.myequity.model.Account;
import br.net.du.myequity.model.Snapshot;
import com.google.common.collect.ImmutableSet;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static br.net.du.myequity.test.TestUtil.newAssetAccount;
import static br.net.du.myequity.test.TestUtil.newLiabilityAccount;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NetWorthUtilTest {

    private Account assetAccount;
    private Account liabilityAccount;
    private BigDecimal expectedNetWorth;

    @BeforeEach
    public void setUp() {
        liabilityAccount = newLiabilityAccount("320000.00");
        assetAccount = newAssetAccount("100.00");
        expectedNetWorth = new BigDecimal("-319900.00");
    }

    @Test
    public void computeByCurrency_fromSnapshotAccountsMapAfterModifyingOriginalObjects_singleCurrency() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(LocalDate.now(), ImmutableSet.of(assetAccount, liabilityAccount));

        // WHEN
        assetAccount.setBalance(assetAccount.getBalance().plus(new BigDecimal("72.00")));
        liabilityAccount.setBalance(liabilityAccount.getBalance().minus(new BigDecimal("99.00")));
        final Map<CurrencyUnit, BigDecimal> netWorthByCurrency = NetWorthUtil.computeByCurrency(snapshot.getAccounts());

        // THEN
        assertEquals(1, netWorthByCurrency.size());
        assertTrue(netWorthByCurrency.containsKey(CurrencyUnit.USD));
        final BigDecimal netWorthUsd = netWorthByCurrency.get(CurrencyUnit.USD);
        assertEquals(expectedNetWorth, netWorthUsd);
    }

    @Test
    public void computeByCurrency_fromSnapshotAccountsMapAfterModifyingOriginalObjects_multipleCurrencies() {
        // GIVEN
        final CurrencyUnit brl = CurrencyUnit.of("BRL");
        final Account brlAsset = newAssetAccount(brl, "700000.00");
        final Account brlLiability = newLiabilityAccount(brl, "150000.00");
        final Snapshot snapshot =
                new Snapshot(LocalDate.now(), ImmutableSet.of(assetAccount, liabilityAccount, brlAsset, brlLiability));

        // WHEN
        assetAccount.setBalance(assetAccount.getBalance().plus(new BigDecimal("72.00")));
        liabilityAccount.setBalance(liabilityAccount.getBalance().minus(new BigDecimal("99.00")));
        brlAsset.setBalance(brlAsset.getBalance().plus(new BigDecimal("72.00")));
        brlLiability.setBalance(brlLiability.getBalance().minus(new BigDecimal("99.00")));
        final Map<CurrencyUnit, BigDecimal> netWorthByCurrency = NetWorthUtil.computeByCurrency(snapshot.getAccounts());

        // THEN
        assertEquals(2, netWorthByCurrency.size());
        assertTrue(netWorthByCurrency.containsKey(CurrencyUnit.USD));
        assertTrue(netWorthByCurrency.containsKey(brl));
        final BigDecimal netWorthUsd = netWorthByCurrency.get(CurrencyUnit.USD);
        assertEquals(expectedNetWorth, netWorthUsd);
        final BigDecimal netWorthBrl = netWorthByCurrency.get(brl);
        assertEquals(new BigDecimal("550000.00"), netWorthBrl);
    }

    @Test
    public void computeByCurrency_fromAccountSet_singleCurrency() {
        // GIVEN
        final Set<Account> accounts = ImmutableSet.of(assetAccount, liabilityAccount);

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
        final Account brlAsset = newAssetAccount(brl, "700000.00");
        final Account brlLiability = newLiabilityAccount(brl, "150000.00");
        final Set<Account> accounts = ImmutableSet.of(assetAccount, liabilityAccount, brlAsset, brlLiability);

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
