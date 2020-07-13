package br.net.du.myequity.validator;

import br.net.du.myequity.model.User;
import br.net.du.myequity.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class UserValidatorTest {

    private static final String EMAIL = "example@example.com";

    @Mock
    private UserService userService;

    private UserValidator userValidator;

    private User user;

    private Errors errors;

    @BeforeEach
    public void setUp() {
        initMocks(this);

        userValidator = new UserValidator(userService);

        when(userService.findByEmail(eq(EMAIL))).thenReturn(null);

        user = new User();
        user.setFirstName("Bill");
        user.setLastName("Gates");
        user.setEmail(EMAIL);
        user.setPassword("password");
        user.setPasswordConfirm("password");

        errors = new BeanPropertyBindingResult(user, "user");
    }

    @Test
    public void supports_happy() {
        assertTrue(userValidator.supports(User.class));
    }

    @Test
    public void supports_anotherClass_false() {
        assertFalse(userValidator.supports(String.class));
    }

    @Test
    public void validate_happy() {
        // WHEN
        userValidator.validate(user, errors);

        // THEN
        assertFalse(errors.hasErrors());
    }

    @Test
    public void validate_emptyObject_hasErrors() {
        // WHEN
        userValidator.validate(new User(), errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_invalidEmail_hasErrors() {
        // GIVEN
        user.setEmail("not_an_email");

        // WHEN
        userValidator.validate(user, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_nullEmail_hasErrors() {
        // GIVEN
        user.setEmail(null);

        // WHEN
        userValidator.validate(user, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_duplicateEmail_hasErrors() {
        // GIVEN
        when(userService.findByEmail(eq(EMAIL))).thenReturn(user);

        // WHEN
        userValidator.validate(user, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_nullFirstName_hasErrors() {
        // GIVEN
        user.setFirstName(null);

        // WHEN
        userValidator.validate(user, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_emptyFirstName_hasErrors() {
        // GIVEN
        user.setFirstName(StringUtils.EMPTY);

        // WHEN
        userValidator.validate(user, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_nullLastName_hasErrors() {
        // GIVEN
        user.setLastName(null);

        // WHEN
        userValidator.validate(user, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_emptyLastName_hasErrors() {
        // GIVEN
        user.setLastName(StringUtils.EMPTY);

        // WHEN
        userValidator.validate(user, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_emptyPassword_hasErrors() {
        // GIVEN
        user.setPassword(StringUtils.EMPTY);

        // WHEN
        userValidator.validate(user, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_nullPassword_hasErrors() {
        // GIVEN
        user.setPassword(null);

        // WHEN
        userValidator.validate(user, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_shortPassword_hasErrors() {
        // GIVEN
        user.setPassword("1234567");

        // WHEN
        userValidator.validate(user, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_longPassword_hasErrors() {
        // GIVEN
        user.setPassword("123456789012345678901234567890123");

        // WHEN
        userValidator.validate(user, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_passwordMismatch_hasErrors() {
        // GIVEN
        user.setPasswordConfirm("something-else");

        // WHEN
        userValidator.validate(user, errors);

        // THEN
        assertTrue(errors.hasErrors());
    }
}
