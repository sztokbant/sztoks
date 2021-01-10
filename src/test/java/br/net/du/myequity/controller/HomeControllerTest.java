package br.net.du.myequity.controller;

import static br.net.du.myequity.test.ControllerTestUtils.verifyRedirect;
import static br.net.du.myequity.test.ModelTestUtils.buildUser;
import static br.net.du.myequity.test.TestConstants.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.myequity.controller.viewmodel.AccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.SnapshotViewModelOutput;
import br.net.du.myequity.controller.viewmodel.UserViewModelOutput;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.SimpleAssetAccount;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
import br.net.du.myequity.service.UserService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import java.time.LocalDate;
import java.util.Map;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

@SpringBootTest
@AutoConfigureMockMvc
class HomeControllerTest {

    private static final String HOME_URL = "/";

    private static final long ASSET_ACCOUNT_ID = 42L;
    private static final String ASSET_ACCOUNTS_KEY = "assetAccounts";
    private static final long LIABILITY_ACCOUNT_ID = 72L;
    private static final String LIABILITY_ACCOUNTS_KEY = "liabilityAccounts";
    private static final Long SNAPSHOT_ID = 99L;
    private static final long SNAPSHOT_INDEX = 1L;
    private static final String SNAPSHOTS_KEY = "snapshots";
    private static final String USER_KEY = "user";

    @Autowired private MockMvc mvc;

    @MockBean private UserService userService;

    private User user;

    private Snapshot snapshot;

    private Account assetAccount;

    private Account liabilityAccount;

    @BeforeEach
    public void setUp() throws Exception {
        user = buildUser();
        snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of(),
                        ImmutableSortedSet.of());
        snapshot.setId(SNAPSHOT_ID);
        user.addSnapshot(snapshot);

        assetAccount =
                new SimpleAssetAccount("Checking Account", CurrencyUnit.USD, LocalDate.now());
        assetAccount.setId(ASSET_ACCOUNT_ID);

        liabilityAccount =
                new SimpleLiabilityAccount("Mortgage", CurrencyUnit.USD, LocalDate.now());
        liabilityAccount.setId(LIABILITY_ACCOUNT_ID);
    }

    @Test
    public void get_userNotLoggedIn_redirectToLogin() throws Exception {
        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(HOME_URL));

        // THEN
        verifyRedirect(resultActions, "/login");
    }

    @Test
    public void get_happy_noAccounts() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.get(HOME_URL).with(user(user.getEmail())));

        // THEN
        resultActions.andExpect(status().isOk());

        final ModelAndView modelAndView = resultActions.andReturn().getModelAndView();

        assertEquals("home", modelAndView.getViewName());

        final Map<String, Object> model = modelAndView.getModel();

        assertEquals(UserViewModelOutput.of(user), model.get(USER_KEY));
        assertEquals(
                ImmutableList.of(SnapshotViewModelOutput.of(snapshot)), model.get(SNAPSHOTS_KEY));

        assertEquals(ImmutableList.of(), model.get(ASSET_ACCOUNTS_KEY));
        assertEquals(ImmutableList.of(), model.get(LIABILITY_ACCOUNTS_KEY));
    }

    @Test
    public void get_happy_withAccounts() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        user.addAccount(assetAccount);
        user.addAccount(liabilityAccount);

        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.get(HOME_URL).with(user(user.getEmail())));

        // THEN
        resultActions.andExpect(status().isOk());

        final ModelAndView modelAndView = resultActions.andReturn().getModelAndView();

        assertEquals("home", modelAndView.getViewName());

        final Map<String, Object> model = modelAndView.getModel();

        assertEquals(UserViewModelOutput.of(user), model.get(USER_KEY));
        assertEquals(
                ImmutableList.of(SnapshotViewModelOutput.of(snapshot)), model.get("snapshots"));

        assertEquals(
                ImmutableList.of(AccountViewModelOutput.of(assetAccount)),
                model.get(ASSET_ACCOUNTS_KEY));
        assertEquals(
                ImmutableList.of(AccountViewModelOutput.of(liabilityAccount)),
                model.get(LIABILITY_ACCOUNTS_KEY));
    }
}
