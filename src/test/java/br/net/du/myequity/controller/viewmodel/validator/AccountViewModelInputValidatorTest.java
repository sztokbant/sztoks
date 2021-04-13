package br.net.du.myequity.controller.viewmodel.validator;

import static br.net.du.myequity.test.ModelTestUtils.SNAPSHOT_ID;
import static br.net.du.myequity.test.TestConstants.ANOTHER_CURRENCY_UNIT;
import static br.net.du.myequity.test.TestConstants.CURRENCY_UNIT;
import static br.net.du.myequity.test.TestConstants.CURRENCY_UNIT_FIELD;
import static br.net.du.myequity.test.TestConstants.FIRST_SNAPSHOT_MONTH;
import static br.net.du.myequity.test.TestConstants.FIRST_SNAPSHOT_YEAR;
import static br.net.du.myequity.test.TestConstants.NAME_FIELD;
import static br.net.du.myequity.test.TestConstants.SUBTYPE_NAME_FIELD;
import static br.net.du.myequity.test.TestConstants.TITHING_PERCENTAGE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import br.net.du.myequity.controller.viewmodel.account.AccountViewModelInput;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.SimpleAssetAccount;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
import br.net.du.myequity.service.AccountService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

class AccountViewModelInputValidatorTest {

    private static final String ACCOUNT_NAME = "My Account";
    private static final String SUBTYPE_NAME = "SimpleAssetAccount";
    private static final String ANOTHER_ACCOUNT_NAME = "Another Account";

    @Mock private AccountService accountService;

    private AccountViewModelInputValidator accountViewModelInputValidator;

    private AccountViewModelInput accountViewModelInput;

    private Snapshot snapshot;

    private Errors errors;

    @BeforeEach
    public void setUp() {
        initMocks(this);

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

        accountViewModelInputValidator = new AccountViewModelInputValidator(accountService);

        accountViewModelInput = new AccountViewModelInput();

        errors = new BeanPropertyBindingResult(accountViewModelInput, "accountForm");
    }

    @Test
    public void supports_happy() {
        assertTrue(accountViewModelInputValidator.supports(AccountViewModelInput.class));
    }

    @Test
    public void supports_anotherClass_false() {
        assertFalse(accountViewModelInputValidator.supports(String.class));
    }

    @Test
    public void validate_happyFirstAccount() {
        // GIVEN
        populateAccountForm(ACCOUNT_NAME, SUBTYPE_NAME, CURRENCY_UNIT.toString());
        defineExistingAccounts(ImmutableList.of());

        // WHEN
        accountViewModelInputValidator.validate(accountViewModelInput, errors, snapshot);

        // THEN
        assertFalse(errors.hasErrors());
    }

    @Test
    public void validate_happyExistingAccountWithDifferentName() {
        // GIVEN
        populateAccountForm(ACCOUNT_NAME, SUBTYPE_NAME, CURRENCY_UNIT.toString());
        defineExistingAccounts(
                ImmutableList.of(new SimpleAssetAccount(ANOTHER_ACCOUNT_NAME, CurrencyUnit.USD)));

        // WHEN
        accountViewModelInputValidator.validate(accountViewModelInput, errors, snapshot);

        // THEN
        assertFalse(errors.hasErrors());
    }

    @Test
    public void validate_happyExistingAccountWithSameNameAndDifferentSubtype() {
        // GIVEN
        populateAccountForm(ACCOUNT_NAME, SUBTYPE_NAME, CURRENCY_UNIT.toString());
        defineExistingAccounts(
                ImmutableList.of(new SimpleLiabilityAccount(ACCOUNT_NAME, CurrencyUnit.USD)));

        // WHEN
        accountViewModelInputValidator.validate(accountViewModelInput, errors, snapshot);

        // THEN
        assertFalse(errors.hasErrors());
    }

    @Test
    public void validate_emptyObject_hasErrors() {
        // GIVEN
        populateAccountForm(null, null, null);
        defineExistingAccounts(ImmutableList.of());

        // WHEN
        accountViewModelInputValidator.validate(accountViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_nullName_hasErrors() {
        // GIVEN
        populateAccountForm(null, SUBTYPE_NAME, CURRENCY_UNIT.toString());
        defineExistingAccounts(ImmutableList.of());

        // WHEN
        accountViewModelInputValidator.validate(accountViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasFieldErrors(NAME_FIELD));
    }

    @Test
    public void validate_nullSubtype_hasErrors() {
        // GIVEN
        populateAccountForm(ACCOUNT_NAME, null, CURRENCY_UNIT.toString());
        defineExistingAccounts(ImmutableList.of());

        // WHEN
        accountViewModelInputValidator.validate(accountViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasFieldErrors(SUBTYPE_NAME_FIELD));
    }

    @Test
    public void validate_invalidSubtype_hasErrors() {
        // GIVEN
        populateAccountForm(ACCOUNT_NAME, "SomeInvalidAccount", CURRENCY_UNIT.toString());
        defineExistingAccounts(ImmutableList.of());

        // WHEN
        accountViewModelInputValidator.validate(accountViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasFieldErrors(SUBTYPE_NAME_FIELD));
    }

    @Test
    public void validate_tithingSubtype_hasErrors() {
        // GIVEN
        populateAccountForm(ACCOUNT_NAME, "TithingAccount", CURRENCY_UNIT.toString());
        defineExistingAccounts(ImmutableList.of());

        // WHEN
        accountViewModelInputValidator.validate(accountViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasFieldErrors(SUBTYPE_NAME_FIELD));
    }

    @Test
    public void validate_nullCurrencyUnit_hasErrors() {
        // GIVEN
        populateAccountForm(ACCOUNT_NAME, SUBTYPE_NAME, null);
        defineExistingAccounts(ImmutableList.of());

        // WHEN
        accountViewModelInputValidator.validate(accountViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasFieldErrors(CURRENCY_UNIT_FIELD));
    }

    @Test
    public void validate_emptyCurrencyUnit_hasErrors() {
        // GIVEN
        populateAccountForm(ACCOUNT_NAME, SUBTYPE_NAME, StringUtils.EMPTY);
        defineExistingAccounts(ImmutableList.of());

        // WHEN
        accountViewModelInputValidator.validate(accountViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasFieldErrors(CURRENCY_UNIT_FIELD));
    }

    @Test
    public void validate_invalidCurrencyUnit_hasErrors() {
        // GIVEN
        populateAccountForm(ACCOUNT_NAME, SUBTYPE_NAME, "xyz");
        defineExistingAccounts(ImmutableList.of());

        // WHEN
        accountViewModelInputValidator.validate(accountViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasFieldErrors(CURRENCY_UNIT_FIELD));
    }

    @Test
    public void validate_unsupportedCurrencyUnit_hasErrors() {
        // GIVEN
        populateAccountForm(ACCOUNT_NAME, SUBTYPE_NAME, ANOTHER_CURRENCY_UNIT.toString());
        defineExistingAccounts(ImmutableList.of());

        // WHEN
        accountViewModelInputValidator.validate(accountViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasFieldErrors(CURRENCY_UNIT_FIELD));
    }

    @Test
    public void validate_supportedAlternativeCurrencyUnit_happy() {
        // GIVEN
        snapshot.putCurrencyConversionRate(ANOTHER_CURRENCY_UNIT, new BigDecimal("1.31"));
        populateAccountForm(ACCOUNT_NAME, SUBTYPE_NAME, ANOTHER_CURRENCY_UNIT.toString());
        defineExistingAccounts(ImmutableList.of());

        // WHEN
        accountViewModelInputValidator.validate(accountViewModelInput, errors, snapshot);

        // THEN
        assertFalse(errors.hasErrors());
    }

    @Test
    public void validate_emptyName_hasErrors() {
        // GIVEN
        populateAccountForm(StringUtils.EMPTY, SUBTYPE_NAME, CURRENCY_UNIT.toString());
        defineExistingAccounts(ImmutableList.of());

        // WHEN
        accountViewModelInputValidator.validate(accountViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasFieldErrors(NAME_FIELD));
    }

    @Test
    public void validate_existingName_hasErrors() {
        // GIVEN
        populateAccountForm(ACCOUNT_NAME, SUBTYPE_NAME, CURRENCY_UNIT.toString());
        defineExistingAccounts(
                ImmutableList.of(new SimpleAssetAccount(ACCOUNT_NAME, CurrencyUnit.USD)));

        // WHEN
        accountViewModelInputValidator.validate(accountViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasFieldErrors(NAME_FIELD));
    }

    @Test
    public void validate_existingNameExtraSpaces_hasErrors() {
        // GIVEN
        populateAccountForm(" " + ACCOUNT_NAME + " ", SUBTYPE_NAME, CURRENCY_UNIT.toString());
        defineExistingAccounts(
                ImmutableList.of(new SimpleAssetAccount(ACCOUNT_NAME, CurrencyUnit.USD)));

        // WHEN
        accountViewModelInputValidator.validate(accountViewModelInput, errors, snapshot);

        // THEN
        assertTrue(errors.hasFieldErrors(NAME_FIELD));
    }

    @Test
    public void validate_noValidationHints_throws() {
        // GIVEN
        populateAccountForm(ACCOUNT_NAME, SUBTYPE_NAME, CURRENCY_UNIT.toString());

        // WHEN/THEN
        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    accountViewModelInputValidator.validate(accountViewModelInput, errors);
                });
    }

    @Test
    public void validate_emptyValidationHints_throws() {
        // GIVEN
        populateAccountForm(ACCOUNT_NAME, SUBTYPE_NAME, CURRENCY_UNIT.toString());

        // WHEN/THEN
        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    accountViewModelInputValidator.validate(
                            accountViewModelInput, errors, new Object[] {});
                });
    }

    @Test
    public void validate_notUserInValidationHints_throws() {
        // GIVEN
        populateAccountForm(ACCOUNT_NAME, SUBTYPE_NAME, CURRENCY_UNIT.toString());

        // WHEN/THEN
        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    accountViewModelInputValidator.validate(
                            accountViewModelInput, errors, "A String!");
                });
    }

    private void populateAccountForm(
            final String name, final String subtypeName, final String currency) {
        accountViewModelInput.setName(name);
        accountViewModelInput.setSubtypeName(subtypeName);
        accountViewModelInput.setCurrencyUnit(currency);
    }

    private void defineExistingAccounts(final List<Account> accounts) {
        when(accountService.findBySnapshot(snapshot)).thenReturn(accounts);
    }
}
