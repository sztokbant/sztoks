package br.net.du.myequity.controller.viewmodel.validator;

import static br.net.du.myequity.controller.viewmodel.TransactionViewModelInputTest.populateCommonAttributes;
import static br.net.du.myequity.controller.viewmodel.TransactionViewModelInputTest.populateDonationTransactionAttributes;
import static br.net.du.myequity.controller.viewmodel.TransactionViewModelInputTest.populateIncomeTransactionAttributes;
import static br.net.du.myequity.controller.viewmodel.TransactionViewModelInputTest.populateInvestmentTransactionAttributes;
import static br.net.du.myequity.test.TestConstants.AMOUNT_FIELD;
import static br.net.du.myequity.test.TestConstants.CURRENCY_UNIT_FIELD;
import static br.net.du.myequity.test.TestConstants.DATE_FIELD;
import static br.net.du.myequity.test.TestConstants.INVESTMENT_CATEGORY_FIELD;
import static br.net.du.myequity.test.TestConstants.IS_RECURRING_FIELD;
import static br.net.du.myequity.test.TestConstants.IS_TAX_DEDUCTIBLE_FIELD;
import static br.net.du.myequity.test.TestConstants.TITHING_PERCENTAGE_FIELD;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.myequity.controller.viewmodel.TransactionViewModelInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

class TransactionViewModelInputValidatorTest {

    private TransactionViewModelInputValidator validator;

    private TransactionViewModelInput transactionViewModelInput;

    private Errors errors;

    @BeforeEach
    public void setUp() {
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
        validator.validate(transactionViewModelInput, errors);

        // THEN
        assertFalse(errors.hasErrors());
    }

    @Test
    public void validate_investment_happy() {
        // GIVEN
        populateInvestmentTransactionAttributes(transactionViewModelInput);

        // WHEN
        validator.validate(transactionViewModelInput, errors);

        // THEN
        assertFalse(errors.hasErrors());
    }

    @Test
    public void validate_donation_happy() {
        // GIVEN
        populateDonationTransactionAttributes(transactionViewModelInput);

        // WHEN
        validator.validate(transactionViewModelInput, errors);

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
                    validator.validate(transactionViewModelInput, errors);
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
                    validator.validate(transactionViewModelInput, errors);
                });
    }

    @Test
    public void validate_emptyObject_hasErrors() {
        // GIVEN
        transactionViewModelInput = new TransactionViewModelInput();
        transactionViewModelInput.setTypeName("INCOME");

        // WHEN
        validator.validate(transactionViewModelInput, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_invalidDate_hasErrors() {
        // GIVEN
        populateDonationTransactionAttributes(transactionViewModelInput);
        transactionViewModelInput.setDate("not_a_date");

        // WHEN
        validator.validate(transactionViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(DATE_FIELD));
    }

    @Test
    public void validate_invalidCurrencyUnit_hasErrors() {
        // GIVEN
        populateDonationTransactionAttributes(transactionViewModelInput);
        transactionViewModelInput.setCurrencyUnit("XYZ");

        // WHEN
        validator.validate(transactionViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(CURRENCY_UNIT_FIELD));
    }

    @Test
    public void validate_invalidAmount_hasErrors() {
        // GIVEN
        populateDonationTransactionAttributes(transactionViewModelInput);
        transactionViewModelInput.setAmount("XYZ");

        // WHEN
        validator.validate(transactionViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(AMOUNT_FIELD));
    }

    @Test
    public void validate_nullIsRecurring_hasErrors() {
        // GIVEN
        populateDonationTransactionAttributes(transactionViewModelInput);
        transactionViewModelInput.setIsRecurring(null);

        // WHEN
        validator.validate(transactionViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(IS_RECURRING_FIELD));
    }

    @Test
    public void validate_invalidTithingPercentage_hasErrors() {
        // GIVEN
        populateIncomeTransactionAttributes(transactionViewModelInput);
        transactionViewModelInput.setTithingPercentage("XYZ");

        // WHEN
        validator.validate(transactionViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(TITHING_PERCENTAGE_FIELD));
    }

    @Test
    public void validate_invalidInvestmentCategory_hasErrors() {
        // GIVEN
        populateInvestmentTransactionAttributes(transactionViewModelInput);
        transactionViewModelInput.setInvestmentCategory("XYZ");

        // WHEN
        validator.validate(transactionViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(INVESTMENT_CATEGORY_FIELD));
    }

    @Test
    public void validate_nullIsTaxDeductible_hasErrors() {
        // GIVEN
        populateDonationTransactionAttributes(transactionViewModelInput);
        transactionViewModelInput.setIsTaxDeductible(null);

        // WHEN
        validator.validate(transactionViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(IS_TAX_DEDUCTIBLE_FIELD));
    }
}
