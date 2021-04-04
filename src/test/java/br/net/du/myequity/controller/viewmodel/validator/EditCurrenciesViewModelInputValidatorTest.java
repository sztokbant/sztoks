package br.net.du.myequity.controller.viewmodel.validator;

import static br.net.du.myequity.test.ModelTestUtils.SNAPSHOT_ID;
import static br.net.du.myequity.test.TestConstants.CURRENCY_UNIT;
import static br.net.du.myequity.test.TestConstants.FIRST_SNAPSHOT_NAME;
import static br.net.du.myequity.test.TestConstants.TITHING_PERCENTAGE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.myequity.controller.viewmodel.EditCurrenciesViewModelInput;
import br.net.du.myequity.model.Snapshot;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

class EditCurrenciesViewModelInputValidatorTest {

    private EditCurrenciesViewModelInputValidator validator;

    private EditCurrenciesViewModelInput editCurrenciesViewModelInput;

    private Snapshot snapshot;

    private Errors errors;

    @BeforeEach
    public void setUp() {
        snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_NAME,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of());
        snapshot.setId(SNAPSHOT_ID);

        snapshot.putCurrencyConversionRate(CurrencyUnit.EUR, new BigDecimal("0.85"));
        snapshot.putCurrencyConversionRate(CurrencyUnit.AUD, new BigDecimal("1.31"));

        validator = new EditCurrenciesViewModelInputValidator();

        editCurrenciesViewModelInput = new EditCurrenciesViewModelInput();

        errors = new BeanPropertyBindingResult(editCurrenciesViewModelInput, "editCurrenciesForm");
    }

    @Test
    public void supports_happy() {
        assertTrue(validator.supports(EditCurrenciesViewModelInput.class));
    }

    @Test
    public void supports_anotherClass_false() {
        assertFalse(validator.supports(String.class));
    }

    @Test
    public void validate_happy() {
        // GIVEN
        editCurrenciesViewModelInput.setCurrencyConversionRates(
                ImmutableMap.of(
                        "EUR", "1.0000",
                        "AUD", "1.5000"));

        // WHEN
        validator.validate(editCurrenciesViewModelInput, errors, snapshot);

        // THEN
        assertFalse(errors.hasErrors());
    }

    @Test
    public void validate_baseCurrency_hasErrors() {
        // GIVEN
        editCurrenciesViewModelInput.setCurrencyConversionRates(
                ImmutableMap.of(
                        "USD", "1.0000",
                        "AUD", "1.5000"));

        // WHEN
        validator.validate(editCurrenciesViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasFieldErrors("currencyConversionRates[USD]"));
    }

    @Test
    public void validate_invalidCurrency_hasErrors() {
        // GIVEN
        editCurrenciesViewModelInput.setCurrencyConversionRates(
                ImmutableMap.of(
                        "XYZ", "1.0000",
                        "AUD", "1.5000"));

        // WHEN
        validator.validate(editCurrenciesViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasFieldErrors("currencyConversionRates[XYZ]"));
    }

    @Test
    public void validate_invalidConversionRate_hasErrors() {
        // GIVEN
        editCurrenciesViewModelInput.setCurrencyConversionRates(
                ImmutableMap.of(
                        "EUR", "NotANumber",
                        "AUD", "1.5000"));

        // WHEN
        validator.validate(editCurrenciesViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasFieldErrors("currencyConversionRates[EUR]"));
    }
}
