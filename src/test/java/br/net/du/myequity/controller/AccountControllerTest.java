package br.net.du.myequity.controller;

import br.net.du.myequity.model.Account;
import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.Workspace;
import br.net.du.myequity.persistence.AccountRepository;
import br.net.du.myequity.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.BeforeEach;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static br.net.du.myequity.test.ControllerTestUtil.verifyRedirect;
import static br.net.du.myequity.test.ModelTestUtil.buildUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    private static final String ACCOUNT_URL = "/account";

    private static final long ACCOUNT_ID = 1L;
    private static final BigDecimal CURRENT_BALANCE = new BigDecimal("320000.00");
    private static final AccountType ACCOUNT_TYPE = AccountType.LIABILITY;
    private static final CurrencyUnit CURRENCY_UNIT = CurrencyUnit.of("BRL");

    private static final BigDecimal NEW_BALANCE = new BigDecimal("108.00");

    private static final String JSON_HAS_ERROR = "hasError";
    private static final String JSON_ID = "id";
    private static final String JSON_BALANCE = "balance";
    private static final String JSON_CURRENCY_UNIT = "currencyUnit";
    private static final String JSON_NET_WORTH = "netWorth";
    private static final String JSON_ACCOUNT_TYPE = "accountType";
    private static final String JSON_TOTAL_FOR_ACCOUNT_TYPE = "totalForAccountType";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AccountRepository accountRepository;

    private String requestContent;

    private User user;

    private Account account;

    @BeforeEach
    public void setUp() throws Exception {
        user = buildUser();

        // WHEN
        account = new Account("Mortgage", ACCOUNT_TYPE, Money.of(CURRENCY_UNIT, CURRENT_BALANCE), LocalDate.now());
        account.setId(ACCOUNT_ID);

        final AccountController.AccountJsonRequest accountJsonRequest =
                AccountController.AccountJsonRequest.builder().id(ACCOUNT_ID).balance(NEW_BALANCE).build();
        requestContent = new ObjectMapper().writeValueAsString(accountJsonRequest);
    }

    @Test
    public void post_noCsrfToken_forbidden() throws Exception {
        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(ACCOUNT_URL)
                                                                              .contentType(MediaType.APPLICATION_JSON)
                                                                              .content(requestContent));

        // THEN
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    public void post_withCsrfTokenUserNotLoggedIn_redirectToLogin() throws Exception {
        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(ACCOUNT_URL)
                                                                              .with(csrf())
                                                                              .contentType(MediaType.APPLICATION_JSON)
                                                                              .content(requestContent));

        // THEN
        verifyRedirect(resultActions, "/login");
    }

    @Test
    public void post_userNotFound_hasError() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(null);

        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(ACCOUNT_URL)
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
        assertTrue(jsonNode.get(JSON_HAS_ERROR).asBoolean());
    }

    @Test
    public void post_accountNotFound_hasError() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(ACCOUNT_URL)
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
        assertTrue(jsonNode.get(JSON_HAS_ERROR).asBoolean());
    }

    @Test
    public void post_accountDoesNotBelongToUser_hasError() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        final User anotherUser = new User(user.getEmail(), user.getFirstName(), user.getLastName());
        final Long anotherUserId = user.getId() * 7;
        anotherUser.setId(anotherUserId);

        final Workspace workspace = new Workspace("My Workspace", CurrencyUnit.USD);
        workspace.setUser(anotherUser);

        account.setWorkspace(workspace);
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(ACCOUNT_URL)
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
        assertTrue(jsonNode.get(JSON_HAS_ERROR).asBoolean());
    }

    @Test
    public void post_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        final Workspace workspace = new Workspace("My Workspace", CurrencyUnit.USD);
        workspace.setUser(user);

        account.setWorkspace(workspace);
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(ACCOUNT_URL)
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
        assertFalse(jsonNode.get(JSON_HAS_ERROR).asBoolean());
        assertEquals(ACCOUNT_ID, jsonNode.get(JSON_ID).asLong());
        assertEquals(NEW_BALANCE.toString(), jsonNode.get(JSON_BALANCE).asText());
        assertEquals(CURRENCY_UNIT.toString(), jsonNode.get(JSON_CURRENCY_UNIT).asText());
        assertEquals(NEW_BALANCE.negate().toString(), jsonNode.get(JSON_NET_WORTH).asText());
        assertEquals(ACCOUNT_TYPE.toString(), jsonNode.get(JSON_ACCOUNT_TYPE).asText());
        assertEquals(NEW_BALANCE.negate().toString(), jsonNode.get(JSON_TOTAL_FOR_ACCOUNT_TYPE).asText());
    }
}