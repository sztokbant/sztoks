package br.net.du.myequity.controller;

import br.net.du.myequity.model.User;
import br.net.du.myequity.service.SecurityService;
import br.net.du.myequity.service.UserService;
import br.net.du.myequity.validator.UserValidator;
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

import static br.net.du.myequity.test.TestUtil.buildUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private static final String SIGNUP_URL = "/signup";
    private static final String LOGIN_URL = "/login";

    private static final String EMAIL_KEY = "email";
    private static final String FIRST_NAME_KEY = "firstName";
    private static final String LAST_NAME_KEY = "lastName";
    private static final String PASSWORD_KEY = "password";
    private static final String PASSWORD_CONFIRM_KEY = "passwordConfirm";

    private static final String PASSWORD_VALUE = "password";

    private static final String LOGOUT_PARAM = "logout";
    private static final String ERROR_PARAM = "error";

    private static final String ERROR_KEY = "error";
    private static final String MESSAGE_KEY = "message";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserValidator userValidator;

    @MockBean
    private UserService userService;

    @MockBean
    private SecurityService securityService;

    @Test
    public void get_signup_happy() throws Exception {
        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(SIGNUP_URL));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        assertEquals("signup", mvcResult.getModelAndView().getViewName());
        final User user = (User) mvcResult.getModelAndView().getModel().get("userForm");
        assertTrue(user instanceof User);
        assertNull(user.getId());
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
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(SIGNUP_URL).with(csrf()));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        assertEquals("signup", mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void post_signup_happy() throws Exception {
        // GIVEN
        final User user = buildUser();

        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(SIGNUP_URL)
                                                                              .with(csrf())
                                                                              .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                                                              .param(EMAIL_KEY, user.getEmail())
                                                                              .param(FIRST_NAME_KEY,
                                                                                     user.getFirstName())
                                                                              .param(LAST_NAME_KEY, user.getLastName())
                                                                              .param(PASSWORD_KEY, PASSWORD_VALUE)
                                                                              .param(PASSWORD_CONFIRM_KEY,
                                                                                     PASSWORD_VALUE));

        // THEN
        resultActions.andExpect(status().is3xxRedirection());

        final MvcResult mvcResult = resultActions.andReturn();
        assertTrue(mvcResult.getResponse().getHeader("Location").endsWith("/"));

        verify(userService).save(any(User.class));
        verify(securityService).autoLogin(eq(user.getEmail()), eq(PASSWORD_VALUE));
    }

    @Test
    public void get_loginWithLogoutParam_happy() throws Exception {
        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.get(LOGIN_URL).param(LOGOUT_PARAM, StringUtils.EMPTY));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        assertEquals("login", mvcResult.getModelAndView().getViewName());
        assertEquals("You have been logged out successfully.", mvcResult.getModelAndView().getModel().get(MESSAGE_KEY));
    }

    @Test
    public void get_loginWithErrorParam_happy() throws Exception {
        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.get(LOGIN_URL).param(ERROR_PARAM, StringUtils.EMPTY));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        assertEquals("login", mvcResult.getModelAndView().getViewName());
        assertEquals("Invalid E-mail or Password.", mvcResult.getModelAndView().getModel().get(ERROR_KEY));
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
