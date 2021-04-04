package br.net.du.myequity.test;

import br.net.du.myequity.model.account.CreditCardAccount;
import br.net.du.myequity.model.account.InvestmentAccount;
import br.net.du.myequity.model.account.SimpleAssetAccount;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
import br.net.du.myequity.model.transaction.DonationTransaction;
import br.net.du.myequity.model.transaction.IncomeTransaction;
import br.net.du.myequity.model.transaction.InvestmentCategory;
import br.net.du.myequity.model.transaction.InvestmentTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.joda.money.CurrencyUnit;

public class TestConstants {

    public static final String FIRST_SNAPSHOT_NAME = "2021-03";
    public static final String SECOND_SNAPSHOT_NAME = "2021-04";
    public static final String THIRD_SNAPSHOT_NAME = "2021-05";
    public static final String FOURTH_SNAPSHOT_NAME = "2021-06";
    public static final String FIFTH_SNAPSHOT_NAME = "2021-07";
    public static final String SIXTH_SNAPSHOT_NAME = "2021-08";
    public static final String SEVENTH_SNAPSHOT_NAME = "2021-09";

    public static final String EMAIL = "example@example.com";
    public static final String FIRST_NAME = "Bill";
    public static final String LAST_NAME = "Gates";
    public static final String PASSWORD = "password";

    public static final String EXTRA_SPACES = "  ";

    public static final String ACCOUNT_NAME = "Account Name";
    public static final CurrencyUnit CURRENCY_UNIT = CurrencyUnit.USD;
    public static final String TITHING_PERCENTAGE = "15.00";

    public static final String AMOUNT_FIELD = "amount";
    public static final String CURRENCY_UNIT_FIELD = "currencyUnit";
    public static final String DATE_FIELD = "date";
    public static final String EMAIL_FIELD = "email";
    public static final String FIRST_NAME_FIELD = "firstName";
    public static final String INVESTMENT_CATEGORY_FIELD = "investmentCategory";
    public static final String IS_RECURRING_FIELD = "isRecurring";
    public static final String IS_TAX_DEDUCTIBLE_FIELD = "isTaxDeductible";
    public static final String LAST_NAME_FIELD = "lastName";
    public static final String NAME_FIELD = "name";
    public static final String PASSWORD_CONFIRM_FIELD = "passwordConfirm";
    public static final String PASSWORD_FIELD = "password";
    public static final String SUBTYPE_NAME_FIELD = "subtypeName";
    public static final String TITHING_PERCENTAGE_FIELD = "tithingPercentage";

    public static SimpleAssetAccount newSimpleAssetAccount() {
        return new SimpleAssetAccount("Savings", CurrencyUnit.USD, new BigDecimal("10000.00"));
    }

    public static SimpleLiabilityAccount newSimpleLiabilityAccount() {
        return new SimpleLiabilityAccount("Mortgage", CurrencyUnit.USD, new BigDecimal("2500.00"));
    }

    public static CreditCardAccount newCreditCardAccount() {
        return new CreditCardAccount(
                "Chase Sapphire Reserve",
                CurrencyUnit.USD,
                new BigDecimal("10000.00"),
                new BigDecimal("9500.00"),
                new BigDecimal("1000.00"));
    }

    public static InvestmentAccount newInvestmentAccount() {
        return new InvestmentAccount(
                "AMZN",
                CurrencyUnit.USD,
                new BigDecimal("175.00"),
                new BigDecimal("1100.00"),
                new BigDecimal("3500.00"));
    }

    public static IncomeTransaction newRecurringIncome(final long id) {
        final IncomeTransaction incomeTransaction = newRecurringIncome();
        incomeTransaction.setId(id);
        return incomeTransaction;
    }

    public static IncomeTransaction newRecurringIncome() {
        return new IncomeTransaction(
                LocalDate.of(2020, 12, 31),
                CurrencyUnit.USD.getCode(),
                new BigDecimal("12000.00"),
                "Salary",
                true,
                new BigDecimal("20.00"));
    }

    public static IncomeTransaction newSingleIncome(final long id) {
        final IncomeTransaction incomeTransaction = newSingleIncome();
        incomeTransaction.setId(id);
        return incomeTransaction;
    }

    public static IncomeTransaction newSingleIncome() {
        return new IncomeTransaction(
                LocalDate.of(2020, 12, 15),
                CurrencyUnit.USD.getCode(),
                new BigDecimal("1700.00"),
                "Side Gig",
                false,
                new BigDecimal("20.00"));
    }

    public static DonationTransaction newRecurringDonation(final long id) {
        final DonationTransaction donationTransaction = newRecurringDonation();
        donationTransaction.setId(id);
        return donationTransaction;
    }

    public static DonationTransaction newRecurringDonation() {
        return new DonationTransaction(
                LocalDate.of(2020, 12, 31),
                CurrencyUnit.USD.getCode(),
                new BigDecimal("108.00"),
                "Charity",
                true,
                true);
    }

    public static DonationTransaction newSingleDonation(final long id) {
        final DonationTransaction donationTransaction = newSingleDonation();
        donationTransaction.setId(id);
        return donationTransaction;
    }

    public static DonationTransaction newSingleDonation() {
        return new DonationTransaction(
                LocalDate.of(2020, 12, 15),
                CurrencyUnit.USD.getCode(),
                new BigDecimal("10.00"),
                "Beggar",
                false,
                true);
    }

    public static InvestmentTransaction newRecurringInvestment(final long id) {
        final InvestmentTransaction investmentTransaction = newRecurringInvestment();
        investmentTransaction.setId(id);
        return investmentTransaction;
    }

    public static InvestmentTransaction newRecurringInvestment() {
        return new InvestmentTransaction(
                LocalDate.of(2020, 12, 31),
                CurrencyUnit.USD.getCode(),
                new BigDecimal("1500.00"),
                "Retirement Fund",
                true,
                InvestmentCategory.LONG_TERM);
    }

    public static InvestmentTransaction newSingleInvestment(final long id) {
        final InvestmentTransaction investmentTransaction = newSingleInvestment();
        investmentTransaction.setId(id);
        return investmentTransaction;
    }

    public static InvestmentTransaction newSingleInvestment() {
        return new InvestmentTransaction(
                LocalDate.of(2020, 12, 15),
                CurrencyUnit.USD.getCode(),
                new BigDecimal("200.00"),
                "Savings",
                false,
                InvestmentCategory.SHORT_TERM);
    }
}
