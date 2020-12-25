package br.net.du.myequity.controller;

import static br.net.du.myequity.test.ModelTestUtils.buildUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.myequity.controller.viewmodel.AccountViewModelInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest extends WebControlerTestBase {

    public AccountControllerTest() {
        super("/newaccount");
    }

    @BeforeEach
    public void setUp() {
        user = buildUser();
    }

    @Test
    public void get_newaccount_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.get(url).with(user(user.getEmail())));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        assertEquals("new_account", mvcResult.getModelAndView().getViewName());

        final AccountViewModelInput accountViewModelInput =
                (AccountViewModelInput) mvcResult.getModelAndView().getModel().get("accountForm");
        assertTrue(accountViewModelInput instanceof AccountViewModelInput);

        assertNull(accountViewModelInput.getName());
        assertNull(accountViewModelInput.getTypeName());
        assertNull(accountViewModelInput.getCurrencyUnit());
    }
}
