package br.net.du.myequity.model.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
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
        final Account account =
                new SimpleLiabilityAccount(accountName, currencyUnit, now, BigDecimal.ZERO);

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
        final Account account =
                new SimpleLiabilityAccount(
                        accountName, currencyUnit, LocalDate.now(), BigDecimal.ZERO);

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
        final Account account =
                new SimpleAssetAccount(
                        accountName,
                        currencyUnit,
                        FutureTithingPolicy.NONE,
                        LocalDate.now(),
                        BigDecimal.ZERO);
        assertEquals(currencyUnit, account.getCurrencyUnit());

        // WHEN
        final CurrencyUnit newCurrencyUnit = CurrencyUnit.of("BRL");
        account.setCurrencyUnit(newCurrencyUnit);

        // THEN
        assertEquals(newCurrencyUnit, account.getCurrencyUnit());
    }

    @Test
    public void equals() {
        final Account account =
                new SimpleLiabilityAccount(
                        "Mortgage", CurrencyUnit.USD, LocalDate.now(), BigDecimal.ZERO);

        // Itself
        assertTrue(account.equals(account));

        // Not instance of Account
        assertFalse(account.equals(null));
        assertFalse(account.equals("Another type of object"));

        // Same Id null
        final Account anotherAccount =
                new SimpleAssetAccount(
                        "Wallet",
                        CurrencyUnit.USD,
                        FutureTithingPolicy.NONE,
                        LocalDate.now(),
                        BigDecimal.ZERO);
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
