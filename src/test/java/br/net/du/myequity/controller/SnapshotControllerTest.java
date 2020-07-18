package br.net.du.myequity.controller;

import br.net.du.myequity.model.Account;
import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.Workspace;
import br.net.du.myequity.persistence.AccountRepository;
import br.net.du.myequity.persistence.SnapshotRepository;
import br.net.du.myequity.service.UserService;
import com.google.common.collect.ImmutableSet;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SnapshotControllerTest {

    private static final String SNAPSHOT_URL_TEMPLATE = "/snapshot/%d";
    private static final Long SNAPSHOT_ID = 99L;

    private static final String ACCOUNT_ID_KEY = "account_id";
    private static final String ACCOUNT_ID_VALUE = "42";
    private static final String BALANCE_AMOUNT_KEY = "balance_amount";
    private static final String BALANCE_AMOUNT_VALUE = "108.00";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SnapshotRepository snapshotRepository;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private UserService userService;

    private User user;

    private User anotherUser;

    private Snapshot snapshot;

    private Workspace workspace;

    private Account account;

    @BeforeEach
    public void setUp() throws Exception {
        user = buildUser();

        anotherUser = buildUser();
        anotherUser.setId(user.getId() * 7);

        snapshot = new Snapshot(LocalDate.now(), ImmutableSet.of());
        snapshot.setId(SNAPSHOT_ID);

        workspace = new Workspace("My Workspace", CurrencyUnit.USD);

        account = new Account("Mortgage",
                              AccountType.ASSET,
                              Money.of(CurrencyUnit.USD, new BigDecimal("99.00")),
                              LocalDate.now());
        account.setId(Long.parseLong(ACCOUNT_ID_VALUE));
    }

    @Test
    public void get_userNotLoggedIn_redirectToLogin() throws Exception {
        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.get(String.format(SNAPSHOT_URL_TEMPLATE, SNAPSHOT_ID)));

        // THEN
        verifyRedirect(resultActions, "/login");
    }

    @Test
    public void get_userNotFound_redirect() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(null);

        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.get(String.format(SNAPSHOT_URL_TEMPLATE, SNAPSHOT_ID))
                                                  .with(user(user.getEmail())));

        // THEN
        verifyRedirect(resultActions, "/");
    }

    @Test
    public void get_snapshotNotFound_redirect() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(snapshotRepository.findById(eq(SNAPSHOT_ID))).thenReturn(Optional.empty());

        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.get(String.format(SNAPSHOT_URL_TEMPLATE, SNAPSHOT_ID))
                                                  .with(user(user.getEmail())));

        // THEN
        verifyRedirect(resultActions, "/");
    }

    @Test
    public void get_snapshotDoesNotBelongToUser_redirect() throws Exception {
        // GIVEN
        workspace.setUser(anotherUser);
        snapshot.setWorkspace(workspace);

        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(snapshotRepository.findById(eq(SNAPSHOT_ID))).thenReturn(Optional.of(snapshot));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.get(String.format(SNAPSHOT_URL_TEMPLATE, SNAPSHOT_ID))
                                                  .with(user(user.getEmail())));

        // THEN
        verifyRedirect(resultActions, "/");
    }

    @Test
    public void get_happy() throws Exception {
        // GIVEN
        workspace.setUser(user);
        snapshot.setWorkspace(workspace);

        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(snapshotRepository.findById(eq(SNAPSHOT_ID))).thenReturn(Optional.of(snapshot));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.get(String.format(SNAPSHOT_URL_TEMPLATE, SNAPSHOT_ID))
                                                  .with(user(user.getEmail())));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        assertEquals("snapshot", mvcResult.getModelAndView().getViewName());
        assertEquals(user, mvcResult.getModelAndView().getModel().get("user"));

        assertEquals(snapshot, mvcResult.getModelAndView().getModel().get("snapshot"));

        assertEquals(AccountType.ASSET, mvcResult.getModelAndView().getModel().get("assetMapKey"));
        assertEquals(AccountType.LIABILITY, mvcResult.getModelAndView().getModel().get("liabilityMapKey"));
    }

    @Test
    public void post_noCsrfToken_forbidden() throws Exception {
        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.post(String.format(SNAPSHOT_URL_TEMPLATE, SNAPSHOT_ID))
                                                  .param(ACCOUNT_ID_KEY, ACCOUNT_ID_VALUE)
                                                  .param(BALANCE_AMOUNT_KEY, BALANCE_AMOUNT_VALUE));

        // THEN
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    public void post_withCsrfTokenUserNotLoggedIn_redirectToLogin() throws Exception {
        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.post(String.format(SNAPSHOT_URL_TEMPLATE, SNAPSHOT_ID))
                                                  .param(ACCOUNT_ID_KEY, ACCOUNT_ID_VALUE)
                                                  .param(BALANCE_AMOUNT_KEY, BALANCE_AMOUNT_VALUE)
                                                  .with(csrf()));

        // THEN
        verifyRedirect(resultActions, "/login");
    }

    @Test
    public void post_userNotFound_redirect() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(null);

        // WHEN
        final ResultActions resultActions = doWellFormedPost();

        // THEN
        verifyRedirect(resultActions, "/");
    }

    @Test
    public void post_snapshotNotFound_redirect() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(snapshotRepository.findById(eq(SNAPSHOT_ID))).thenReturn(Optional.empty());

        // WHEN
        final ResultActions resultActions = doWellFormedPost();

        // THEN
        verifyRedirect(resultActions, "/");
    }

    @Test
    public void post_snapshotDoesNotBelongToUser_redirect() throws Exception {
        // GIVEN
        workspace.setUser(anotherUser);
        snapshot.setWorkspace(workspace);

        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(snapshotRepository.findById(eq(SNAPSHOT_ID))).thenReturn(Optional.of(snapshot));

        // WHEN
        final ResultActions resultActions = doWellFormedPost();

        // THEN
        verifyRedirect(resultActions, "/");
    }

    @Test
    public void post_accountNotFound_redirect() throws Exception {
        // GIVEN
        workspace.setUser(user);
        snapshot.setWorkspace(workspace);

        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(snapshotRepository.findById(eq(SNAPSHOT_ID))).thenReturn(Optional.of(snapshot));
        when(accountRepository.findById(Long.parseLong(ACCOUNT_ID_VALUE))).thenReturn(Optional.empty());

        // WHEN
        final ResultActions resultActions = doWellFormedPost();

        // THEN
        verifyRedirect(resultActions, "/");
    }

    @Test
    public void post_accountDoesNotBelongToUser_redirect() throws Exception {
        // GIVEN
        workspace.setUser(user);
        snapshot.setWorkspace(workspace);

        final Workspace anotherWorkspace = new Workspace("Another Workspace", CurrencyUnit.USD);
        workspace.setUser(anotherUser);
        account.setWorkspace(anotherWorkspace);

        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(snapshotRepository.findById(eq(SNAPSHOT_ID))).thenReturn(Optional.of(snapshot));
        when(accountRepository.findById(Long.parseLong(ACCOUNT_ID_VALUE))).thenReturn(Optional.of(account));

        // WHEN
        final ResultActions resultActions = doWellFormedPost();

        // THEN
        verifyRedirect(resultActions, "/");
    }

    @Test
    public void post_accountDoesNotBelongInSnapshot_redirect() throws Exception {
        // GIVEN
        workspace.setUser(user);
        snapshot.setWorkspace(workspace);
        account.setWorkspace(workspace);

        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(snapshotRepository.findById(eq(SNAPSHOT_ID))).thenReturn(Optional.of(snapshot));
        when(accountRepository.findById(Long.parseLong(ACCOUNT_ID_VALUE))).thenReturn(Optional.of(account));

        // WHEN
        final ResultActions resultActions = doWellFormedPost();

        // THEN
        verifyRedirect(resultActions, "/");
    }

    @Test
    public void post_happy() throws Exception {
        // GIVEN
        workspace.setUser(user);
        snapshot.setWorkspace(workspace);
        account.setWorkspace(workspace);
        snapshot.setAccount(account);

        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(snapshotRepository.findById(eq(SNAPSHOT_ID))).thenReturn(Optional.of(snapshot));
        when(accountRepository.findById(Long.parseLong(ACCOUNT_ID_VALUE))).thenReturn(Optional.of(account));

        // WHEN
        final ResultActions resultActions = doWellFormedPost();

        // THEN
        verifyRedirect(resultActions, String.format("/snapshot/%d", snapshot.getId()));
        verify(snapshotRepository).save(snapshot);
    }

    private ResultActions doWellFormedPost() throws Exception {
        return mvc.perform(MockMvcRequestBuilders.post(String.format(SNAPSHOT_URL_TEMPLATE, SNAPSHOT_ID))
                                                 .param(ACCOUNT_ID_KEY, ACCOUNT_ID_VALUE)
                                                 .param(BALANCE_AMOUNT_KEY, BALANCE_AMOUNT_VALUE)
                                                 .with(csrf())
                                                 .with(user(user.getEmail())));
    }
}
