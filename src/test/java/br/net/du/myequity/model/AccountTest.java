package br.net.du.myequity.model;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.SimpleAssetAccount;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountTest {

    @Test
    public void constructorWithNameTypeCurrencyAndDate() {
        // GIVEN
        final String accountName = "Mortgage";
        final AccountType accountType = AccountType.LIABILITY;
        final CurrencyUnit currencyUnit = CurrencyUnit.USD;
        final LocalDate now = LocalDate.now();

        // WHEN
        final Account account = new SimpleLiabilityAccount(accountName, currencyUnit, now);

        // THEN
        assertEquals(accountName, account.getName());
        assertEquals(accountType, account.getAccountType());
        assertEquals(currencyUnit, account.getCurrencyUnit());
        assertEquals(now, account.getCreateDate());
        assertFalse(account.isClosed());
        assertNull(account.getClosedDate());
    }

    @Test
    public void constructorWithNameTypeAndCurrency() {
        // GIVEN
        final String accountName = "Mortgage";
        final AccountType accountType = AccountType.LIABILITY;
        final CurrencyUnit currencyUnit = CurrencyUnit.USD;

        // WHEN
        final Account account = new SimpleLiabilityAccount(accountName, currencyUnit);

        // THEN
        assertEquals(accountName, account.getName());
        assertEquals(accountType, account.getAccountType());
        assertEquals(currencyUnit, account.getCurrencyUnit());
        assertFalse(account.isClosed());
        assertNull(account.getClosedDate());
    }

    @Test
    public void currencyGetterAndSetter() {
        // GIVEN
        final String accountName = "Wallet";
        final AccountType accountType = AccountType.ASSET;
        final CurrencyUnit currencyUnit = CurrencyUnit.USD;
        final Account account = new SimpleAssetAccount(accountName, currencyUnit);
        assertEquals(currencyUnit, account.getCurrencyUnit());

        // WHEN
        final CurrencyUnit newCurrencyUnit = CurrencyUnit.of("BRL");
        account.setCurrencyUnit(newCurrencyUnit);

        // THEN
        assertEquals(newCurrencyUnit, account.getCurrencyUnit());
    }

    @Test
    public void equals() {
        final Account account = new SimpleLiabilityAccount("Mortgage", CurrencyUnit.USD, LocalDate.now());

        // Itself
        assertTrue(account.equals(account));

        // Not instance of Account
        assertFalse(account.equals(null));
        assertFalse(account.equals("Another type of object"));

        // Same Id null
        final Account anotherAccount = new SimpleAssetAccount("Wallet", CurrencyUnit.USD, LocalDate.now());
        account.setId(null);
        anotherAccount.setId(null);
        assertFalse(account.equals(anotherAccount));
        assertFalse(anotherAccount.equals(account));

        // Same Id not null
        account.setId(42L);
        anotherAccount.setId(42L);
        assertTrue(account.equals(anotherAccount));
        assertTrue(anotherAccount.equals(account));
    }
}
