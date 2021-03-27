package br.net.du.myequity.controller.viewmodel.validator;

import static br.net.du.myequity.test.TestConstants.EMAIL;
import static br.net.du.myequity.test.TestConstants.FIRST_NAME;
import static br.net.du.myequity.test.TestConstants.LAST_NAME;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import br.net.du.myequity.controller.viewmodel.UserViewModelInput;
import br.net.du.myequity.model.User;
import br.net.du.myequity.service.UserService;
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
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_nullEmail_hasErrors() {
        // GIVEN
        userViewModelInput.setEmail(null);

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_duplicateEmail_hasErrors() {
        // GIVEN
        final User existingUser = new User(EMAIL, FIRST_NAME, LAST_NAME);
        when(userService.findByEmail(eq(EMAIL))).thenReturn(existingUser);

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_nullFirstName_hasErrors() {
        // GIVEN
        userViewModelInput.setFirstName(null);

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_emptyFirstName_hasErrors() {
        // GIVEN
        userViewModelInput.setFirstName(StringUtils.EMPTY);

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_nullLastName_hasErrors() {
        // GIVEN
        userViewModelInput.setLastName(null);

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_emptyLastName_hasErrors() {
        // GIVEN
        userViewModelInput.setLastName(StringUtils.EMPTY);

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_emptyPassword_hasErrors() {
        // GIVEN
        userViewModelInput.setPassword(StringUtils.EMPTY);

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_nullPassword_hasErrors() {
        // GIVEN
        userViewModelInput.setPassword(null);

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_shortPassword_hasErrors() {
        // GIVEN
        userViewModelInput.setPassword("1234567");

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_longPassword_hasErrors() {
        // GIVEN
        userViewModelInput.setPassword("123456789012345678901234567890123");

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_passwordMismatch_hasErrors() {
        // GIVEN
        userViewModelInput.setPasswordConfirm("something-else");

        // WHEN
        validator.validate(userViewModelInput, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    private void populateUserViewModelInput() {
        userViewModelInput = new UserViewModelInput();

        userViewModelInput.setFirstName(FIRST_NAME);
        userViewModelInput.setLastName(LAST_NAME);
        userViewModelInput.setEmail(EMAIL);
        userViewModelInput.setPassword("password");
        userViewModelInput.setPasswordConfirm("password");
    }
}
