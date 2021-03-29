package br.net.du.myequity.test;

import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import br.net.du.myequity.model.snapshot.InvestmentSnapshot;
import br.net.du.myequity.model.snapshot.SimpleAssetSnapshot;
import br.net.du.myequity.model.snapshot.SimpleLiabilitySnapshot;
import br.net.du.myequity.model.transaction.DonationTransaction;
import br.net.du.myequity.model.transaction.IncomeTransaction;
import br.net.du.myequity.model.transaction.InvestmentCategory;
import br.net.du.myequity.model.transaction.InvestmentTransaction;
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

    public static final String ACCOUNT_NAME = "Account Name";
    public static final CurrencyUnit CURRENCY_UNIT = CurrencyUnit.USD;

    public static final AccountSnapshot SIMPLE_ASSET_SNAPSHOT =
            new SimpleAssetSnapshot("Savings", CurrencyUnit.USD, new BigDecimal("10000.00"));

    public static final AccountSnapshot SIMPLE_LIABILITY_SNAPSHOT =
            new SimpleLiabilitySnapshot("Mortgage", CurrencyUnit.USD, new BigDecimal("2500.00"));

    public static final AccountSnapshot CREDIT_CARD_SNAPSHOT =
            new CreditCardSnapshot(
                    "Chase Sapphire Reserve",
                    CurrencyUnit.USD,
                    new BigDecimal("10000.00"),
                    new BigDecimal("9500.00"),
                    new BigDecimal("1000.00"));

    public static final AccountSnapshot INVESTMENT_SNAPSHOT =
            new InvestmentSnapshot(
                    "AMZN",
                    CurrencyUnit.USD,
                    new BigDecimal("175.00"),
                    new BigDecimal("1100.00"),
                    new BigDecimal("3500.00"));

    public static final IncomeTransaction newRecurringIncome(final long id) {
        final IncomeTransaction incomeTransaction = newRecurringIncome();
        incomeTransaction.setId(id);
        return incomeTransaction;
    }

    public static final IncomeTransaction newRecurringIncome() {
        return new IncomeTransaction(
                LocalDate.of(2020, 12, 31),
                CurrencyUnit.USD.getCode(),
                new BigDecimal("12000.00"),
                "Salary",
                true,
                new BigDecimal("20.00"));
    }

    public static final IncomeTransaction newSingleIncome(final long id) {
        final IncomeTransaction incomeTransaction = newSingleIncome();
        incomeTransaction.setId(id);
        return incomeTransaction;
    }

    public static final IncomeTransaction newSingleIncome() {
        return new IncomeTransaction(
                LocalDate.of(2020, 12, 15),
                CurrencyUnit.USD.getCode(),
                new BigDecimal("1700.00"),
                "Side Gig",
                false,
                new BigDecimal("20.00"));
    }

    public static final DonationTransaction newRecurringDonation(final long id) {
        final DonationTransaction donationTransaction = newRecurringDonation();
        donationTransaction.setId(id);
        return donationTransaction;
    }

    public static final DonationTransaction newRecurringDonation() {
        return new DonationTransaction(
                LocalDate.of(2020, 12, 31),
                CurrencyUnit.USD.getCode(),
                new BigDecimal("108.00"),
                "Charity",
                true,
                true);
    }

    public static final DonationTransaction newSingleDonation(final long id) {
        final DonationTransaction donationTransaction = newSingleDonation();
        donationTransaction.setId(id);
        return donationTransaction;
    }

    public static final DonationTransaction newSingleDonation() {
        return new DonationTransaction(
                LocalDate.of(2020, 12, 15),
                CurrencyUnit.USD.getCode(),
                new BigDecimal("10.00"),
                "Beggar",
                false,
                true);
    }

    public static final InvestmentTransaction newRecurringInvestment(final long id) {
        final InvestmentTransaction investmentTransaction = newRecurringInvestment();
        investmentTransaction.setId(id);
        return investmentTransaction;
    }

    public static final InvestmentTransaction newRecurringInvestment() {
        return new InvestmentTransaction(
                LocalDate.of(2020, 12, 31),
                CurrencyUnit.USD.getCode(),
                new BigDecimal("1500.00"),
                "Retirement Fund",
                true,
                InvestmentCategory.LONG_TERM);
    }

    public static final InvestmentTransaction newSingleInvestment(final long id) {
        final InvestmentTransaction investmentTransaction = newSingleInvestment();
        investmentTransaction.setId(id);
        return investmentTransaction;
    }

    public static final InvestmentTransaction newSingleInvestment() {
        return new InvestmentTransaction(
                LocalDate.of(2020, 12, 15),
                CurrencyUnit.USD.getCode(),
                new BigDecimal("200.00"),
                "Savings",
                false,
                InvestmentCategory.SHORT_TERM);
    }
}
