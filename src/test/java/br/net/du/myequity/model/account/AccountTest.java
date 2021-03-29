package br.net.du.myequity.model.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.SimpleAssetSnapshot;
import br.net.du.myequity.model.snapshot.SimpleLiabilitySnapshot;
import java.time.LocalDate;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.Test;

class AccountTest {

    @Test
    public void constructorWithNameTypeCurrencyAndDate() {
        // GIVEN
        final String accountName = "Mortgage";
        final AccountType accountType = AccountType.LIABILITY;
        final CurrencyUnit currencyUnit = CurrencyUnit.USD;
        final LocalDate now = LocalDate.now();

        // WHEN
        final AccountSnapshot account = new SimpleLiabilitySnapshot(accountName, currencyUnit, now);

        // THEN
        assertEquals(accountName, account.getName());
        assertEquals(accountType, account.getAccountType());
        assertEquals(currencyUnit, account.getCurrencyUnit());
        assertEquals(now, account.getCreateDate());
    }

    @Test
    public void constructorWithNameTypeAndCurrency() {
        // GIVEN
        final String accountName = "Mortgage";
        final AccountType accountType = AccountType.LIABILITY;
        final CurrencyUnit currencyUnit = CurrencyUnit.USD;

        // WHEN
        final AccountSnapshot account = new SimpleLiabilitySnapshot(accountName, currencyUnit);

        // THEN
        assertEquals(accountName, account.getName());
        assertEquals(accountType, account.getAccountType());
        assertEquals(currencyUnit, account.getCurrencyUnit());
    }

    @Test
    public void currencyGetterAndSetter() {
        // GIVEN
        final String accountName = "Wallet";
        final AccountType accountType = AccountType.ASSET;
        final CurrencyUnit currencyUnit = CurrencyUnit.USD;
        final AccountSnapshot account = new SimpleAssetSnapshot(accountName, currencyUnit);
        assertEquals(currencyUnit, account.getCurrencyUnit());

        // WHEN
        final CurrencyUnit newCurrencyUnit = CurrencyUnit.of("BRL");
        account.setCurrencyUnit(newCurrencyUnit);

        // THEN
        assertEquals(newCurrencyUnit, account.getCurrencyUnit());
    }

    @Test
    public void equals() {
        final AccountSnapshot account =
                new SimpleLiabilitySnapshot("Mortgage", CurrencyUnit.USD, LocalDate.now());

        // Itself
        assertTrue(account.equals(account));

        // Not instance of Account
        assertFalse(account.equals(null));
        assertFalse(account.equals("Another type of object"));

        // Same Id null
        final AccountSnapshot anotherAccount =
                new SimpleAssetSnapshot("Wallet", CurrencyUnit.USD, LocalDate.now());
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
