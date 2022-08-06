package br.net.du.sztoks.controller;

import static br.net.du.sztoks.test.ControllerTestUtils.verifyRedirect;
import static br.net.du.sztoks.test.ModelTestUtils.buildUser;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import br.net.du.sztoks.model.User;
import br.net.du.sztoks.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public abstract class GetControllerTestBase {
    @Autowired protected MockMvc mvc;

    @MockBean protected UserService userService;

    protected User user;

    protected final String url;

    public GetControllerTestBase(final String url) {
        this.url = url;
    }

    @BeforeEach
    public void getControllerTestBaseSetUp() {
        user = buildUser();
    }

    @Test
    public void get_userNotLoggedIn_redirectToLogin() throws Exception {
        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(url));

        // THEN
        verifyRedirect(resultActions, "/login");
    }

    @Test
    public void get_userNotFound_redirect() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(null);

        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.get(url).with(user(user.getEmail())));

        // THEN
        verifyRedirect(resultActions, "/login");
    }
}
