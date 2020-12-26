package br.net.du.myequity.controller;

import static br.net.du.myequity.test.ControllerTestUtils.verifyRedirect;
import static br.net.du.myequity.test.ModelTestUtils.buildUser;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.myequity.model.User;
import br.net.du.myequity.service.UserService;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public abstract class PostControllerTestBase {
    protected static final CurrencyUnit CURRENCY_UNIT = CurrencyUnit.of("BRL");

    protected static final String ACCOUNT_NAME = "Mortgage";

    @Autowired protected MockMvc mvc;

    @MockBean protected UserService userService;

    protected MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    protected User user;

    protected final String url;

    public PostControllerTestBase(final String url) {
        this.url = url;
    }

    @BeforeEach
    public void postControllerTestBaseSetUp() throws Exception {
        user = buildUser();
    }

    @Test
    public void post_noCsrfToken_forbidden() throws Exception {
        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .params(params));

        // THEN
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    public void post_withCsrfTokenUserNotLoggedIn_redirectToLogin() throws Exception {
        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .params(params));

        // THEN
        verifyRedirect(resultActions, "/login");
    }

    @Test
    public void post_userNotFound_redirectToLogin() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(null);

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .params(params));

        // THEN
        verifyRedirect(resultActions, "/login");
    }
}
