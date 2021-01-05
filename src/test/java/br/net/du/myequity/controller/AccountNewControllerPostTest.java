package br.net.du.myequity.controller;

import static br.net.du.myequity.test.ControllerTestUtils.verifyRedirect;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class AccountNewControllerPostTest extends PostControllerTestBase {

    private static final String NEW_ACCOUNT_VIEW_NAME = "new_account";
    private static final String URL = "/newaccount";

    private static final String CURRENCY_UNIT_KEY = "currencyUnit";
    private static final String CURRENCY_UNIT_VALUE = "USD";
    private static final String NAME_KEY = "name";
    private static final String NAME_VALUE = "My Account";
    private static final String TYPE_NAME_KEY = "typeName";
    private static final String TYPE_NAME_VALUE = "CreditCardAccount";

    public AccountNewControllerPostTest() {
        super(URL);
    }

    @BeforeEach
    public void setUp() {
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
    }

    @Test
    public void post_noAccountName_reloadForm() throws Exception {
        // GIVEN
        assertEquals(0, user.getAccounts().size());

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param(TYPE_NAME_KEY, TYPE_NAME_VALUE)
                                .param(CURRENCY_UNIT_KEY, CURRENCY_UNIT_VALUE));

        // THEN
        assertEquals(0, user.getAccounts().size());
        verify(userService, never()).save(eq(user));

        resultActions.andExpect(status().isOk());
        final MvcResult mvcResult = resultActions.andReturn();
        assertEquals(NEW_ACCOUNT_VIEW_NAME, mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void post_noTypeName_reloadForm() throws Exception {
        // GIVEN
        assertEquals(0, user.getAccounts().size());

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param(NAME_KEY, NAME_VALUE)
                                .param(CURRENCY_UNIT_KEY, CURRENCY_UNIT_VALUE));

        // THEN
        assertEquals(0, user.getAccounts().size());
        verify(userService, never()).save(eq(user));

        resultActions.andExpect(status().isOk());
        final MvcResult mvcResult = resultActions.andReturn();
        assertEquals(NEW_ACCOUNT_VIEW_NAME, mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void post_noCurrencyUnit_reloadForm() throws Exception {
        // GIVEN
        assertEquals(0, user.getAccounts().size());

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param(NAME_KEY, NAME_VALUE)
                                .param(TYPE_NAME_KEY, TYPE_NAME_VALUE));

        // THEN
        assertEquals(0, user.getAccounts().size());
        verify(userService, never()).save(eq(user));

        resultActions.andExpect(status().isOk());
        final MvcResult mvcResult = resultActions.andReturn();
        assertEquals(NEW_ACCOUNT_VIEW_NAME, mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void post_happy() throws Exception {
        // GIVEN
        assertEquals(0, user.getAccounts().size());

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param(NAME_KEY, NAME_VALUE)
                                .param(TYPE_NAME_KEY, TYPE_NAME_VALUE)
                                .param(CURRENCY_UNIT_KEY, CURRENCY_UNIT_VALUE));

        // THEN
        assertEquals(1, user.getAccounts().size());
        verify(userService).save(eq(user));
        verifyRedirect(resultActions, "/");
    }
}
