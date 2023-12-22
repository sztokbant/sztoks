package br.net.du.sztoks.controller.viewmodel.validator;

import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT_KEY;
import static br.net.du.sztoks.test.TestConstants.EMAIL;
import static br.net.du.sztoks.test.TestConstants.EMAIL_FIELD;
import static br.net.du.sztoks.test.TestConstants.FIRST_NAME;
import static br.net.du.sztoks.test.TestConstants.FIRST_NAME_FIELD;
import static br.net.du.sztoks.test.TestConstants.LAST_NAME;
import static br.net.du.sztoks.test.TestConstants.LAST_NAME_FIELD;
import static br.net.du.sztoks.test.TestConstants.PASSWORD_CONFIRM_FIELD;
import static br.net.du.sztoks.test.TestConstants.PASSWORD_FIELD;
import static br.net.du.sztoks.test.TestConstants.TITHING_PERCENTAGE_FIELD;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import br.net.du.sztoks.controller.viewmodel.UserViewModelInput;
import br.net.du.sztoks.model.User;
import br.net.du.sztoks.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

class UserViewModelInputValidatorTest {

    @Mock private UserService userService;

    private UserViewModelInputValidator validator;

    private UserViewModelInput userViewModelInput;

    private Errors errors;

    @BeforeEach
    public void setUp() {
        initMocks(this);

        validator = new UserViewModelInputValidator(userService);

        when(userService.findByEmail(eq(EMAIL))).thenReturn(null);

        populateUserViewModelInput();

        errors = new BeanPropertyBindingResult(userViewModelInput, "user");
    }

    @Test
    public void supports_happy() {
        assertTrue(validator.supports(UserViewModelInput.class));
    }

    @Test
    public void supports_anotherClass_false() {
        assertFalse(validator.supports(String.class));
    }

    @Test
    public void validate_happy() {
        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertFalse(errors.hasErrors());
    }

    @Test
    public void validate_emptyObject_hasErrors() {
        // GIVEN
        userViewModelInput = new UserViewModelInput();

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_invalidEmail_hasErrors() {
        // GIVEN
        userViewModelInput.setEmail("not_an_email");

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(EMAIL_FIELD));
    }

    @Test
    public void validate_nullEmail_hasErrors() {
        // GIVEN
        userViewModelInput.setEmail(null);

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(EMAIL_FIELD));
    }

    @Test
    public void validate_duplicateEmail_hasErrors() {
        // GIVEN
        final User existingUser = new User(EMAIL, FIRST_NAME, LAST_NAME);
        when(userService.findByEmail(eq(EMAIL))).thenReturn(existingUser);

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(EMAIL_FIELD));
    }

    @Test
    public void validate_nullFirstName_hasErrors() {
        // GIVEN
        userViewModelInput.setFirstName(null);

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(FIRST_NAME_FIELD));
    }

    @Test
    public void validate_emptyFirstName_hasErrors() {
        // GIVEN
        userViewModelInput.setFirstName(StringUtils.EMPTY);

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(FIRST_NAME_FIELD));
    }

    @Test
    public void validate_nullLastName_hasErrors() {
        // GIVEN
        userViewModelInput.setLastName(null);

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(LAST_NAME_FIELD));
    }

    @Test
    public void validate_emptyLastName_hasErrors() {
        // GIVEN
        userViewModelInput.setLastName(StringUtils.EMPTY);

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(LAST_NAME_FIELD));
    }

    @Test
    public void validate_emptyCurrencyUnit_hasErrors() {
        // GIVEN
        userViewModelInput.setCurrencyUnit(StringUtils.EMPTY);

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(CURRENCY_UNIT_KEY));
    }

    @Test
    public void validate_nullCurrencyUnit_hasErrors() {
        // GIVEN
        userViewModelInput.setCurrencyUnit(null);

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(CURRENCY_UNIT_KEY));
    }

    @Test
    public void validate_invalidCurrencyUnit_hasErrors() {
        // GIVEN
        userViewModelInput.setCurrencyUnit("XYZ");

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(CURRENCY_UNIT_KEY));
    }

    @Test
    public void validate_emptyTithingPercentage_hasErrors() {
        // GIVEN
        userViewModelInput.setTithingPercentage(StringUtils.EMPTY);

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(TITHING_PERCENTAGE_FIELD));
    }

    @Test
    public void validate_nullTithingPercentage_hasErrors() {
        // GIVEN
        userViewModelInput.setTithingPercentage(null);

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(TITHING_PERCENTAGE_FIELD));
    }

    @Test
    public void validate_invalidTithingPercentage_hasErrors() {
        // GIVEN
        userViewModelInput.setTithingPercentage("NotANumber");

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(TITHING_PERCENTAGE_FIELD));
    }

    @Test
    public void validate_emptyPassword_hasErrors() {
        // GIVEN
        userViewModelInput.setPassword(StringUtils.EMPTY);

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(PASSWORD_FIELD));
    }

    @Test
    public void validate_nullPassword_hasErrors() {
        // GIVEN
        userViewModelInput.setPassword(null);

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(PASSWORD_FIELD));
    }

    @Test
    public void validate_shortPassword_hasErrors() {
        // GIVEN
        userViewModelInput.setPassword("1234567");

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(PASSWORD_FIELD));
    }

    @Test
    public void validate_longPassword_hasErrors() {
        // GIVEN
        userViewModelInput.setPassword("123456789012345678901234567890123");

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(PASSWORD_FIELD));
    }

    @Test
    public void validate_passwordMismatch_hasErrors() {
        // GIVEN
        userViewModelInput.setPasswordConfirm("something-else");

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasFieldErrors(PASSWORD_CONFIRM_FIELD));
    }

    private void populateUserViewModelInput() {
        userViewModelInput = new UserViewModelInput();

        userViewModelInput.setFirstName(FIRST_NAME);
        userViewModelInput.setLastName(LAST_NAME);
        userViewModelInput.setEmail(EMAIL);
        userViewModelInput.setCurrencyUnit("USD");
        userViewModelInput.setTithingPercentage("10.00");
        userViewModelInput.setPassword("password");
        userViewModelInput.setPasswordConfirm("password");
    }
}
