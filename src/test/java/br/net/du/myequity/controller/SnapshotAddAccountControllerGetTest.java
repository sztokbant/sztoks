package br.net.du.myequity.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.myequity.controller.viewmodel.AccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.UserViewModelOutput;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.SimpleAssetAccount;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
import com.google.common.collect.ImmutableList;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class SnapshotAddAccountControllerGetTest extends SnapshotControllerGetTestBase {

    private static final long ASSET_ACCOUNT_ID = 42L;
    private static final String ASSETS = "assets";
    private static final String LIABILITIES = "liabilities";
    private static final long LIABILITY_ACCOUNT_ID = 72L;
    private static final String URL = String.format("/snapshot/addAccounts/%d", SNAPSHOT_ID);
    private static final String USER = "user";
    private static final String VIEW_NAME = "add_accounts";

    protected Account assetAccount;

    protected Account liabilityAccount;

    public SnapshotAddAccountControllerGetTest() {
        super(URL);
    }

    @BeforeEach
    public void setUp() {
        user.addSnapshot(snapshot);
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(snapshotService.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

        assetAccount =
                new SimpleAssetAccount("Checking Account", CurrencyUnit.USD, LocalDate.now());
        assetAccount.setId(ASSET_ACCOUNT_ID);

        liabilityAccount =
                new SimpleLiabilityAccount("Mortgage", CurrencyUnit.USD, LocalDate.now());
        liabilityAccount.setId(LIABILITY_ACCOUNT_ID);

        when(accountService.findByUser(user))
                .thenReturn(ImmutableList.of(assetAccount, liabilityAccount));
    }

    @Test
    public void get_noAccountsInSnapshot_happy() throws Exception {
        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.get(url).with(user(user.getEmail())));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        assertEquals(VIEW_NAME, mvcResult.getModelAndView().getViewName());

        final Map<String, Object> model = mvcResult.getModelAndView().getModel();
        assertEquals(UserViewModelOutput.of(user), model.get(USER));
        assertEquals(ImmutableList.of(AccountViewModelOutput.of(assetAccount)), model.get(ASSETS));
        assertEquals(
                ImmutableList.of(AccountViewModelOutput.of(liabilityAccount)),
                model.get(LIABILITIES));
    }

    @Test
    public void get_assetAlreadyInSnapshot_happy() throws Exception {
        // GIVEN
        snapshot.addAccountSnapshot(assetAccount.newEmptySnapshot());

        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.get(url).with(user(user.getEmail())));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        assertEquals(VIEW_NAME, mvcResult.getModelAndView().getViewName());

        final Map<String, Object> model = mvcResult.getModelAndView().getModel();
        assertEquals(UserViewModelOutput.of(user), model.get(USER));
        assertEquals(ImmutableList.of(), model.get(ASSETS));
        assertEquals(
                ImmutableList.of(AccountViewModelOutput.of(liabilityAccount)),
                model.get(LIABILITIES));
    }

    @Test
    public void get_liabilityAlreadyInSnapshot_happy() throws Exception {
        // GIVEN
        snapshot.addAccountSnapshot(liabilityAccount.newEmptySnapshot());

        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.get(url).with(user(user.getEmail())));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        assertEquals(VIEW_NAME, mvcResult.getModelAndView().getViewName());

        final Map<String, Object> model = mvcResult.getModelAndView().getModel();
        assertEquals(UserViewModelOutput.of(user), model.get(USER));
        assertEquals(ImmutableList.of(AccountViewModelOutput.of(assetAccount)), model.get(ASSETS));
        assertEquals(ImmutableList.of(), model.get(LIABILITIES));
    }

    @Test
    public void get_noAvailableAccount_happy() throws Exception {
        // GIVEN
        snapshot.addAccountSnapshot(assetAccount.newEmptySnapshot());
        snapshot.addAccountSnapshot(liabilityAccount.newEmptySnapshot());

        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.get(url).with(user(user.getEmail())));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        assertEquals(VIEW_NAME, mvcResult.getModelAndView().getViewName());

        final Map<String, Object> model = mvcResult.getModelAndView().getModel();
        assertEquals(UserViewModelOutput.of(user), model.get(USER));
        assertEquals(ImmutableList.of(), model.get(ASSETS));
        assertEquals(ImmutableList.of(), model.get(LIABILITIES));
    }
}
