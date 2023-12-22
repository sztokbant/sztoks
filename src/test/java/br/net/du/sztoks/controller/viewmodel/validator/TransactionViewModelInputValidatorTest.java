package br.net.du.sztoks.controller.viewmodel.validator;

import static br.net.du.sztoks.controller.viewmodel.TransactionViewModelInputTest.populateCommonAttributes;
import static br.net.du.sztoks.controller.viewmodel.TransactionViewModelInputTest.populateDonationTransactionAttributes;
import static br.net.du.sztoks.controller.viewmodel.TransactionViewModelInputTest.populateIncomeTransactionAttributes;
import static br.net.du.sztoks.controller.viewmodel.TransactionViewModelInputTest.populateInvestmentTransactionAttributes;
import static br.net.du.sztoks.test.ModelTestUtils.SNAPSHOT_ID;
import static br.net.du.sztoks.test.TestConstants.AMOUNT_KEY;
import static br.net.du.sztoks.test.TestConstants.ANOTHER_CURRENCY_UNIT;
import static br.net.du.sztoks.test.TestConstants.CATEGORY_KEY;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT_KEY;
import static br.net.du.sztoks.test.TestConstants.DATE_KEY;
import static br.net.du.sztoks.test.TestConstants.FIRST_SNAPSHOT_MONTH;
import static br.net.du.sztoks.test.TestConstants.FIRST_SNAPSHOT_YEAR;
import static br.net.du.sztoks.test.TestConstants.IS_TAX_DEDUCTIBLE_KEY;
import static br.net.du.sztoks.test.TestConstants.RECURRENCE_POLICY_KEY;
import static br.net.du.sztoks.test.TestConstants.TITHING_PERCENTAGE;
import static br.net.du.sztoks.test.TestConstants.TITHING_PERCENTAGE_KEY;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.sztoks.controller.viewmodel.transaction.TransactionViewModelInput;
import br.net.du.sztoks.model.Snapshot;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

class TransactionViewModelInputValidatorTest {

    private TransactionViewModelInputValidator validator;

    private TransactionViewModelInput transactionViewModelInput;

    private Snapshot snapshot;

    private Errors errors;

    @BeforeEach
    public void setUp() {
        snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of());
        snapshot.setId(SNAPSHOT_ID);

        validator = new TransactionViewModelInputValidator();

        transactionViewModelInput = new TransactionViewModelInput();
        populateCommonAttributes(transactionViewModelInput);

        errors = new BeanPropertyBindingResult(transactionViewModelInput, "transactionForm");
    }

    @Test
    public void supports_happy() {
        assertTrue(validator.supports(TransactionViewModelInput.class));
    }

    @Test
    public void supports_anotherClass_false() {
        assertFalse(validator.supports(String.class));
    }

    @Test
    public void validate_income_happy() {
        // GIVEN
        populateIncomeTransactionAttributes(transactionViewModelInput);

        // WHEN
        validator.validate(transactionViewModelInput, errors, snapshot);

        // THEN
        assertFalse(errors.hasErrors());
    }

    @Test
    public void validate_investment_happy() {
        // GIVEN
        populateInvestmentTransactionAttributes(transactionViewModelInput);

        // WHEN
        validator.validate(transactionViewModelInput, errors, snapshot);

        // THEN
        assertFalse(errors.hasErrors());
    }

    @Test
    public void validate_donation_happy() {
        // GIVEN
        populateDonationTransactionAttributes(transactionViewModelInput);

        // WHEN
        validator.validate(transactionViewModelInput, errors, snapshot);

        // THEN
        assertFalse(errors.hasErrors());
    }

    @Test
    public void validate_noTransactionType_throws() {
        // GIVEN
        transactionViewModelInput = new TransactionViewModelInput();

        // WHEN/THEN
        assertThrows(
                NullPointerException.class,
                () -> {
                    validator.validate(transactionViewModelInput, errors, snapshot);
                });
    }

    @Test
    public void validate_invalidTransactionType_throws() {
        // GIVEN
        transactionViewModelInput = new TransactionViewModelInput();
        transactionViewModelInput.setTypeName("INVALID_TYPE");

        // WHEN/THEN
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    validator.validate(transactionViewModelInput, errors, snapshot);
                });
    }

    @Test
    public void validate_emptyObject_hasErrors() {
        // GIVEN
        transactionViewModelInput = new TransactionViewModelInput();
        transactionViewModelInput.setTypeName("INCOME");

        // WHEN
        validator.validate(transactionViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_invalidDate_hasErrors() {
        // GIVEN
        populateDonationTransactionAttributes(transactionViewModelInput);
        transactionViewModelInput.setDate("not_a_date");

        // WHEN
        validator.validate(transactionViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasFieldErrors(DATE_KEY));
    }

    @Test
    public void validate_invalidCurrencyUnit_hasErrors() {
        // GIVEN
        populateDonationTransactionAttributes(transactionViewModelInput);
        transactionViewModelInput.setCurrencyUnit("XYZ");

        // WHEN
        validator.validate(transactionViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasFieldErrors(CURRENCY_UNIT_KEY));
    }

    @Test
    public void validate_unsupportedCurrencyUnit_hasErrors() {
        // GIVEN
        populateDonationTransactionAttributes(transactionViewModelInput);
        transactionViewModelInput.setCurrencyUnit(ANOTHER_CURRENCY_UNIT.toString());

        // WHEN
        validator.validate(transactionViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasFieldErrors(CURRENCY_UNIT_KEY));
    }

    @Test
    public void validate_supportedAlternativeCurrencyUnit_happy() {
        // GIVEN
        snapshot.putCurrencyConversionRate(ANOTHER_CURRENCY_UNIT, new BigDecimal("1.31"));
        populateDonationTransactionAttributes(transactionViewModelInput);
        transactionViewModelInput.setCurrencyUnit(ANOTHER_CURRENCY_UNIT.toString());

        // WHEN
        validator.validate(transactionViewModelInput, errors, snapshot);

        // THEN
        assertFalse(errors.hasErrors());
    }

    @Test
    public void validate_invalidAmount_hasErrors() {
        // GIVEN
        populateDonationTransactionAttributes(transactionViewModelInput);
        transactionViewModelInput.setAmount("XYZ");

        // WHEN
        validator.validate(transactionViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasFieldErrors(AMOUNT_KEY));
    }

    @Test
    public void validate_nullRecurrencePolicy_hasErrors() {
        // GIVEN
        populateDonationTransactionAttributes(transactionViewModelInput);
        transactionViewModelInput.setRecurrencePolicy(null);

        // WHEN
        validator.validate(transactionViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasFieldErrors(RECURRENCE_POLICY_KEY));
    }

    @Test
    public void validate_invalidTithingPercentage_hasErrors() {
        // GIVEN
        populateIncomeTransactionAttributes(transactionViewModelInput);
        transactionViewModelInput.setTithingPercentage("XYZ");

        // WHEN
        validator.validate(transactionViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasFieldErrors(TITHING_PERCENTAGE_KEY));
    }

    @Test
    public void validate_invalidInvestmentCategory_hasErrors() {
        // GIVEN
        populateInvestmentTransactionAttributes(transactionViewModelInput);
        transactionViewModelInput.setCategory("XYZ");

        // WHEN
        validator.validate(transactionViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasFieldErrors(CATEGORY_KEY));
    }

    @Test
    public void validate_nullIsTaxDeductible_hasErrors() {
        // GIVEN
        populateDonationTransactionAttributes(transactionViewModelInput);
        transactionViewModelInput.setIsTaxDeductible(null);

        // WHEN
        validator.validate(transactionViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasFieldErrors(IS_TAX_DEDUCTIBLE_KEY));
    }
}
