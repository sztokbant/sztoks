package br.net.du.sztoks.test;

import br.net.du.sztoks.model.account.CreditCardAccount;
import br.net.du.sztoks.model.account.FutureTithingPolicy;
import br.net.du.sztoks.model.account.InvestmentAccount;
import br.net.du.sztoks.model.account.SimpleAssetAccount;
import br.net.du.sztoks.model.account.SimpleLiabilityAccount;
import br.net.du.sztoks.model.transaction.DonationCategory;
import br.net.du.sztoks.model.transaction.DonationTransaction;
import br.net.du.sztoks.model.transaction.IncomeCategory;
import br.net.du.sztoks.model.transaction.IncomeTransaction;
import br.net.du.sztoks.model.transaction.InvestmentCategory;
import br.net.du.sztoks.model.transaction.InvestmentTransaction;
import br.net.du.sztoks.model.transaction.RecurrencePolicy;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.joda.money.CurrencyUnit;

public class TestConstants {

    public static final int FIRST_SNAPSHOT_YEAR = 2021;
    public static final int FIRST_SNAPSHOT_MONTH = 3;

    public static final int SECOND_SNAPSHOT_YEAR = 2021;
    public static final int SECOND_SNAPSHOT_MONTH = 4;

    public static final int THIRD_SNAPSHOT_YEAR = 2021;
    public static final int THIRD_SNAPSHOT_MONTH = 5;

    public static final int FOURTH_SNAPSHOT_YEAR = 2021;
    public static final int FOURTH_SNAPSHOT_MONTH = 6;

    public static final int FIFTH_SNAPSHOT_YEAR = 2021;
    public static final int FIFTH_SNAPSHOT_MONTH = 7;

    public static final int SIXTH_SNAPSHOT_YEAR = 2021;
    public static final int SIXTH_SNAPSHOT_MONTH = 8;

    public static final int SEVENTH_SNAPSHOT_YEAR = 2021;
    public static final int SEVENTH_SNAPSHOT_MONTH = 9;

    public static final String EMAIL = "example@example.com";
    public static final String FIRST_NAME = "Bill";
    public static final String LAST_NAME = "Gates";
    public static final String PASSWORD = "password";

    public static final String EXTRA_SPACES = "  ";

    public static final String ACCOUNT_NAME = "Account Name";
    public static final CurrencyUnit CURRENCY_UNIT = CurrencyUnit.USD;
    public static final CurrencyUnit ANOTHER_CURRENCY_UNIT = CurrencyUnit.AUD;
    public static final BigDecimal TITHING_PERCENTAGE = new BigDecimal("15.00");

    public static final String AMOUNT_KEY = "amount";
    public static final String CONVERSION_RATE_FIELD = "conversionRate";
    public static final String CURRENCY_UNIT_KEY = "currencyUnit";
    public static final String DATE_KEY = "date";
    public static final String DESCRIPTION_KEY = "description";
    public static final String EMAIL_FIELD = "email";
    public static final String FIRST_NAME_FIELD = "firstName";
    public static final String FUTURE_TITHYNG_POLICY_KEY = "futureTithingPolicy";
    public static final String CATEGORY_KEY = "category";
    public static final String IS_TAX_DEDUCTIBLE_KEY = "isTaxDeductible";
    public static final String LAST_NAME_FIELD = "lastName";
    public static final String NAME_FIELD = "name";
    public static final String PASSWORD_CONFIRM_FIELD = "passwordConfirm";
    public static final String PASSWORD_FIELD = "password";
    public static final String RECURRENCE_POLICY_KEY = "recurrencePolicy";
    public static final String SUBTYPE_NAME_KEY = "subtypeName";
    public static final String TITHING_PERCENTAGE_KEY = "tithingPercentage";
    public static final String TYPE_NAME_KEY = "typeName";

    public static SimpleAssetAccount newSimpleAssetAccount(final CurrencyUnit currencyUnit) {
        return new SimpleAssetAccount(
                "SimpleAsset " + currencyUnit.getCode(),
                currencyUnit,
                FutureTithingPolicy.NONE,
                LocalDate.now(),
                new BigDecimal("10000.00"));
    }

    public static SimpleLiabilityAccount newSimpleLiabilityAccount(
            final CurrencyUnit currencyUnit) {
        return new SimpleLiabilityAccount(
                "SimpleLiability " + currencyUnit.getCode(),
                currencyUnit,
                LocalDate.now(),
                new BigDecimal("2500.00"));
    }

    public static CreditCardAccount newCreditCardAccount() {
        return new CreditCardAccount(
                "Chase Sapphire Reserve",
                CurrencyUnit.USD,
                LocalDate.now(),
                new BigDecimal("10000.00"),
                new BigDecimal("9500.00"),
                new BigDecimal("1000.00"));
    }

    public static InvestmentAccount newInvestmentAccount() {
        return new InvestmentAccount(
                "AMZN",
                CurrencyUnit.USD,
                FutureTithingPolicy.NONE,
                LocalDate.now(),
                new BigDecimal("175.00"),
                new BigDecimal("1100.00"),
                new BigDecimal("3500.00"));
    }

    public static InvestmentAccount newInvestmentAccountWithFutureTithing() {
        return new InvestmentAccount(
                "My Investment with Future Tithing",
                CurrencyUnit.USD,
                FutureTithingPolicy.PROFITS_ONLY,
                LocalDate.now(),
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
                RecurrencePolicy.RECURRING,
                new BigDecimal("20.00"),
                IncomeCategory.JOB);
    }

    public static IncomeTransaction newSingleIncome(final CurrencyUnit currencyUnit) {
        return new IncomeTransaction(
                LocalDate.of(2020, 12, 15),
                currencyUnit.getCode(),
                new BigDecimal("1800.00"),
                "Side Gig " + currencyUnit.getCode(),
                RecurrencePolicy.NONE,
                new BigDecimal("30.00"),
                IncomeCategory.SIDE_GIG);
    }

    public static DonationTransaction newRecurringNonTaxDeductibleDonation(
            final CurrencyUnit currencyUnit) {
        return new DonationTransaction(
                LocalDate.of(2020, 12, 31),
                currencyUnit.getCode(),
                new BigDecimal("75.00"),
                "Non-Tax Deductible Donation " + currencyUnit.getCode(),
                RecurrencePolicy.RECURRING,
                false,
                DonationCategory.SPIRITUAL);
    }

    public static DonationTransaction newSingleTaxDeductibleDonation(
            final CurrencyUnit currencyUnit) {
        return new DonationTransaction(
                LocalDate.of(2020, 12, 15),
                currencyUnit.getCode(),
                new BigDecimal("150.00"),
                "Tax Deductible Donation " + currencyUnit.getCode(),
                RecurrencePolicy.NONE,
                true,
                DonationCategory.OTHER);
    }

    public static InvestmentTransaction newRecurringInvestment() {
        return new InvestmentTransaction(
                LocalDate.of(2020, 12, 31),
                CurrencyUnit.USD.getCode(),
                new BigDecimal("1500.00"),
                "Retirement Fund",
                RecurrencePolicy.RECURRING,
                InvestmentCategory.LONG_TERM);
    }

    public static InvestmentTransaction newSingleInvestment(final CurrencyUnit currencyUnit) {
        return new InvestmentTransaction(
                LocalDate.of(2020, 12, 15),
                currencyUnit.getCode(),
                new BigDecimal("200.00"),
                "Investment " + currencyUnit.getCode(),
                RecurrencePolicy.NONE,
                InvestmentCategory.SHORT_TERM);
    }
}
