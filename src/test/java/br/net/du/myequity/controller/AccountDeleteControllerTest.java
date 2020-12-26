package br.net.du.myequity.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.myequity.controller.viewmodel.AccountDeleteJsonRequest;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
import br.net.du.myequity.service.AccountSnapshotService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class AccountDeleteControllerTest extends AccountControllerTestBase {

    private static final String JSON_ACCOUNT_ID = "accountId";

    @MockBean private AccountSnapshotService accountSnapshotService;

    public AccountDeleteControllerTest() {
        super("/account/delete");
    }

    @BeforeEach
    public void setUp() throws Exception {
        final AccountDeleteJsonRequest accountDeleteJsonRequest =
                AccountDeleteJsonRequest.builder().accountId(ACCOUNT_ID).build();
        requestContent = new ObjectMapper().writeValueAsString(accountDeleteJsonRequest);
    }

    @Override
    public void createEntity() {
        account = new SimpleLiabilityAccount(ACCOUNT_NAME, CURRENCY_UNIT, LocalDate.now());
        account.setId(ACCOUNT_ID);
    }

    @Test
    public void post_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        account.setUser(user);
        when(accountService.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestContent));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        final String resultContentAsString = mvcResult.getResponse().getContentAsString();
        assertNotNull(resultContentAsString);

        final JsonNode jsonNode = new ObjectMapper().readTree(resultContentAsString);
        assertEquals(ACCOUNT_ID, jsonNode.get(JSON_ACCOUNT_ID).numberValue().longValue());
    }
}
