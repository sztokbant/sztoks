package br.net.du.sztoks.controller;

import static br.net.du.sztoks.test.ControllerTestUtils.verifyRedirect;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT_KEY;
import static br.net.du.sztoks.test.TestConstants.EMAIL;
import static br.net.du.sztoks.test.TestConstants.EMAIL_FIELD;
import static br.net.du.sztoks.test.TestConstants.EXTRA_SPACES;
import static br.net.du.sztoks.test.TestConstants.FIRST_NAME;
import static br.net.du.sztoks.test.TestConstants.FIRST_NAME_FIELD;
import static br.net.du.sztoks.test.TestConstants.LAST_NAME;
import static br.net.du.sztoks.test.TestConstants.LAST_NAME_FIELD;
import static br.net.du.sztoks.test.TestConstants.PASSWORD;
import static br.net.du.sztoks.test.TestConstants.PASSWORD_CONFIRM_FIELD;
import static br.net.du.sztoks.test.TestConstants.PASSWORD_FIELD;
import static br.net.du.sztoks.test.TestConstants.TITHING_PERCENTAGE;
import static br.net.du.sztoks.test.TestConstants.TITHING_PERCENTAGE_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.sztoks.controller.viewmodel.UserViewModelInput;
import br.net.du.sztoks.controller.viewmodel.validator.UserViewModelInputValidator;
import br.net.du.sztoks.service.SecurityService;
import br.net.du.sztoks.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private static final String SIGNUP_URL = "/signup";
    private static final String LOGIN_URL = "/login";

    private static final String LOGOUT_PARAM = "logout";
    private static final String ERROR_PARAM = "error";

    private static final String ERROR_KEY = "error";
    private static final String MESSAGE_KEY = "message";

    @Autowired private MockMvc mvc;

    @Autowired private UserViewModelInputValidator userValidator;

    @MockBean private UserService userService;

    @MockBean private SecurityService securityService;

    @Test
    public void get_signup_happy() throws Exception {
        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(SIGNUP_URL));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        assertEquals("signup", mvcResult.getModelAndView().getViewName());

        final UserViewModelInput userViewModelInput =
                (UserViewModelInput) mvcResult.getModelAndView().getModel().get("userForm");
        assertTrue(userViewModelInput instanceof UserViewModelInput);

        assertNull(userViewModelInput.getEmail());
        assertNull(userViewModelInput.getFirstName());
        assertNull(userViewModelInput.getLastName());
        assertNull(userViewModelInput.getPassword());
        assertNull(userViewModelInput.getPasswordConfirm());
    }

    @Test
    public void post_signupNoCsrfToken_forbidden() throws Exception {
        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(SIGNUP_URL));

        // THEN
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    public void post_signupUserHasErrors_happy() throws Exception {
        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.post(SIGNUP_URL).with(csrf()));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        assertEquals("signup", mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void post_signup_happy() throws Exception {
        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(SIGNUP_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param(EMAIL_FIELD, EMAIL + EXTRA_SPACES)
                                .param(FIRST_NAME_FIELD, FIRST_NAME + EXTRA_SPACES)
                                .param(LAST_NAME_FIELD, LAST_NAME + EXTRA_SPACES)
                                .param(CURRENCY_UNIT_KEY, CURRENCY_UNIT.getCode())
                                .param(TITHING_PERCENTAGE_KEY, TITHING_PERCENTAGE.toPlainString())
                                .param(PASSWORD_FIELD, PASSWORD)
                                .param(PASSWORD_CONFIRM_FIELD, PASSWORD));

        // THEN
        verifyRedirect(resultActions, "/");

        verify(userService)
                .signUp(
                        eq(EMAIL),
                        eq(FIRST_NAME),
                        eq(LAST_NAME),
                        eq(CURRENCY_UNIT),
                        eq(TITHING_PERCENTAGE),
                        eq(PASSWORD));
        verify(securityService).autoLogin(eq(EMAIL), eq(PASSWORD));
    }

    @Test
    public void get_loginWithLogoutParam_happy() throws Exception {
        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.get(LOGIN_URL)
                                .param(LOGOUT_PARAM, StringUtils.EMPTY));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        assertEquals("login", mvcResult.getModelAndView().getViewName());
        assertEquals(
                "You have been logged out successfully.",
                mvcResult.getModelAndView().getModel().get(MESSAGE_KEY));
    }

    @Test
    public void get_loginWithErrorParam_happy() throws Exception {
        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.get(LOGIN_URL)
                                .param(ERROR_PARAM, StringUtils.EMPTY));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        assertEquals("login", mvcResult.getModelAndView().getViewName());
        assertEquals(
                "Invalid E-mail or Password.",
                mvcResult.getModelAndView().getModel().get(ERROR_KEY));
    }

    @Test
    public void get_login_happy() throws Exception {
        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(LOGIN_URL));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        assertEquals("login", mvcResult.getModelAndView().getViewName());
    }
}
