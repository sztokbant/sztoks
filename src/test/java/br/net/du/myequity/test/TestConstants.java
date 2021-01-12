package br.net.du.myequity.test;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.CreditCardAccount;
import br.net.du.myequity.model.account.SimpleAssetAccount;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import br.net.du.myequity.model.snapshot.InvestmentSnapshot;
import br.net.du.myequity.model.snapshot.SimpleAssetSnapshot;
import br.net.du.myequity.model.snapshot.SimpleLiabilitySnapshot;
import br.net.du.myequity.model.transaction.Donation;
import br.net.du.myequity.model.transaction.Income;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.joda.money.CurrencyUnit;

public class TestConstants {

    public static final String now = LocalDate.now().toString();

    public static final String EMAIL = "example@example.com";
    public static final String FIRST_NAME = "Bill";
    public static final String LAST_NAME = "Gates";
    public static final String PASSWORD = "password";

    public static final String EXTRA_SPACES = "  ";

    public static final Account SIMPLE_ASSET_ACCOUNT =
            new SimpleAssetAccount("Savings", CurrencyUnit.USD, LocalDate.now());
    public static final AccountSnapshot SIMPLE_ASSET_SNAPSHOT =
            new SimpleAssetSnapshot(SIMPLE_ASSET_ACCOUNT, new BigDecimal("10000.00"));

    public static final Account SIMPLE_LIABILITY_ACCOUNT =
            new SimpleLiabilityAccount("Mortgage", CurrencyUnit.USD, LocalDate.now());
    public static final AccountSnapshot SIMPLE_LIABILITY_SNAPSHOT =
            new SimpleLiabilitySnapshot(SIMPLE_LIABILITY_ACCOUNT, new BigDecimal("2500.00"));

    public static final Account CREDIT_CARD_ACCOUNT =
            new CreditCardAccount("Chase Sapphire Reserve", CurrencyUnit.USD, LocalDate.now());
    public static final AccountSnapshot CREDIT_CARD_SNAPSHOT =
            new CreditCardSnapshot(
                    CREDIT_CARD_ACCOUNT,
                    new BigDecimal("10000.00"),
                    new BigDecimal("9500.00"),
                    new BigDecimal("1000.00"));

    public static final Account INVESTMENT_ACCOUNT =
            new CreditCardAccount("AMZN", CurrencyUnit.USD, LocalDate.now());
    public static final AccountSnapshot INVESTMENT_SNAPSHOT =
            new InvestmentSnapshot(
                    INVESTMENT_ACCOUNT,
                    new BigDecimal("175.00"),
                    new BigDecimal("1100.00"),
                    new BigDecimal("3500.00"));

    public static final Income SALARY_INCOME =
            new Income(
                    LocalDate.of(2020, 12, 31),
                    CurrencyUnit.USD.getCode(),
                    new BigDecimal("12000.00"),
                    "Salary",
                    true,
                    new BigDecimal("20.00"));

    public static final Income SIDE_GIG_INCOME =
            new Income(
                    LocalDate.of(2020, 12, 15),
                    CurrencyUnit.USD.getCode(),
                    new BigDecimal("1700.00"),
                    "Side Gig",
                    false,
                    new BigDecimal("20.00"));

    public static final Donation CHARITY_DONATION =
            new Donation(
                    LocalDate.of(2020, 12, 15),
                    CurrencyUnit.USD.getCode(),
                    new BigDecimal("108.00"),
                    "Charity",
                    true,
                    true);

    public static final Donation BEGGAR_DONATION =
            new Donation(
                    LocalDate.of(2020, 12, 31),
                    CurrencyUnit.USD.getCode(),
                    new BigDecimal("10.00"),
                    "Beggar",
                    false,
                    true);
}
