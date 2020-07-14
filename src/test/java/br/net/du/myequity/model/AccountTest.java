package br.net.du.myequity.model;

import org.apache.commons.lang3.StringUtils;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountTest {

    @Test
    public void constructorWithNameTypeValueAndDate() {
        // GIVEN
        final String accountName = "Mortgage";
        final AccountType accountType = AccountType.LIABILITY;
        final Money balance = Money.of(CurrencyUnit.USD, new BigDecimal("320000.00"));
        final LocalDate now = LocalDate.now();

        // WHEN
        final Account account = new Account(accountName, accountType, balance, now);

        // THEN
        assertEquals(accountName, account.getName());
        assertEquals(accountType, account.getAccountType());
        assertEquals(balance, account.getBalance());
        assertEquals(now, account.getCreateDate());
        assertFalse(account.isClosed());
        assertNull(account.getClosedDate());
        assertEquals(StringUtils.EMPTY, account.getCategory());
    }

    @Test
    public void constructorWithNameTypeAndBalance() {
        // GIVEN
        final String accountName = "Mortgage";
        final AccountType accountType = AccountType.LIABILITY;
        final Money balance = Money.of(CurrencyUnit.USD, new BigDecimal("320000.00"));

        // WHEN
        final Account account = new Account(accountName, accountType, balance);

        // THEN
        assertEquals(accountName, account.getName());
        assertEquals(accountType, account.getAccountType());
        assertEquals(balance, account.getBalance());
        assertFalse(account.isClosed());
        assertNull(account.getClosedDate());
        assertEquals(StringUtils.EMPTY, account.getCategory());
    }

    @Test
    public void constructorWithNameTypeAndCurrency() {
        // GIVEN
        final String accountName = "Mortgage";
        final AccountType accountType = AccountType.LIABILITY;

        // WHEN
        final Account account = new Account(accountName, accountType, CurrencyUnit.USD);

        // THEN
        assertEquals(accountName, account.getName());
        assertEquals(accountType, account.getAccountType());
        assertEquals(new BigDecimal("0.00"), account.getBalance().getAmount());
        assertFalse(account.isClosed());
        assertNull(account.getClosedDate());
        assertEquals(StringUtils.EMPTY, account.getCategory());
    }

    @Test
    public void balanceGetterAndSetter() {
        // GIVEN
        final String accountName = "Wallet";
        final AccountType accountType = AccountType.ASSET;
        final Money originalBalance = Money.of(CurrencyUnit.USD, new BigDecimal("100.00"));
        final Account account = new Account(accountName, accountType, originalBalance);
        assertEquals(originalBalance, account.getBalance());

        // WHEN
        final Money newBalance = Money.of(CurrencyUnit.of("BRL"), new BigDecimal("550.00"));
        account.setBalance(newBalance);

        // THEN
        assertEquals(newBalance, account.getBalance());
    }

    @Test
    public void setBalanceAmount() {
        // GIVEN
        final String accountName = "Mortgage";
        final AccountType accountType = AccountType.LIABILITY;
        final Money balance = Money.of(CurrencyUnit.USD, new BigDecimal("320000.00"));
        final Account account = new Account(accountName, accountType, balance);

        // WHEN
        final BigDecimal newAmount = new BigDecimal("100000.00");
        account.setBalanceAmount(newAmount);

        // THEN
        assertEquals(Money.of(CurrencyUnit.USD, newAmount), account.getBalance());
    }

    @Test
    public void equals() {
        final Account account = new Account("Mortgage",
                                            AccountType.LIABILITY,
                                            Money.of(CurrencyUnit.USD, new BigDecimal("320000.00")),
                                            LocalDate.now());

        // Itself
        assertTrue(account.equals(account));

        // Not instance of Workspace
        assertFalse(account.equals(null));
        assertFalse(account.equals("Another type of object"));

        // Same Id null
        final Account anotherAccount = new Account("Wallet",
                                                   AccountType.ASSET,
                                                   Money.of(CurrencyUnit.USD, new BigDecimal("100.00")),
                                                   LocalDate.now());
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