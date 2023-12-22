package br.net.du.sztoks.controller.account;

import static br.net.du.sztoks.controller.ControllerTestConstants.ACCOUNT_TYPE_KEY;
import static br.net.du.sztoks.controller.ControllerTestConstants.NAME_KEY;
import static br.net.du.sztoks.test.ControllerTestUtils.verifyRedirect;
import static br.net.du.sztoks.test.ModelTestUtils.SNAPSHOT_ID;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT_KEY;
import static br.net.du.sztoks.test.TestConstants.FUTURE_TITHYNG_POLICY_KEY;
import static br.net.du.sztoks.test.TestConstants.SUBTYPE_NAME_KEY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import br.net.du.sztoks.controller.PostControllerTestBase;
import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.account.DueDateUpdatable;
import br.net.du.sztoks.model.account.FutureTithingPolicy;
import br.net.du.sztoks.model.account.PayableAccount;
import br.net.du.sztoks.model.account.ReceivableAccount;
import br.net.du.sztoks.model.account.SimpleAssetAccount;
import br.net.du.sztoks.model.account.SimpleLiabilityAccount;
import br.net.du.sztoks.service.AccountService;
import br.net.du.sztoks.service.SnapshotService;
import java.time.YearMonth;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class AccountNewControllerTest extends PostControllerTestBase {

    private static final String POST_URL = "/snapshot/" + SNAPSHOT_ID + "/newAccount";
    private static final String SNAPSHOT_URL = String.format("/snapshot/%d", SNAPSHOT_ID);

    @MockBean protected SnapshotService snapshotService;
    @MockBean protected AccountService accountService;

    private Snapshot snapshot;

    public AccountNewControllerTest() {
        super(POST_URL);
    }

    @BeforeEach
    public void setUp() {
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        snapshot = user.getSnapshots().first();
        when(snapshotService.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));
        when(snapshotService.findTopByUser(eq(user))).thenReturn(snapshot);
    }

    @Test
    public void post_SimpleAssetAccount_happy() throws Exception {
        // GIVEN
        assertThat(snapshot.getAccounts().size(), is(0));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .param(NAME_KEY, "Simple Asset")
                                .param(ACCOUNT_TYPE_KEY, "ASSET")
                                .param(SUBTYPE_NAME_KEY, "SimpleAssetAccount")
                                .param(CURRENCY_UNIT_KEY, CURRENCY_UNIT.getCode())
                                .param(FUTURE_TITHYNG_POLICY_KEY, FutureTithingPolicy.ALL.name())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // THEN
        assertThat(snapshot.getAccounts().size(), is(1));

        final Account account = snapshot.getAccounts().first();
        assertTrue(account instanceof SimpleAssetAccount);
        assertThat(
                ((SimpleAssetAccount) account).getFutureTithingPolicy(),
                is(FutureTithingPolicy.ALL));

        verifyRedirect(resultActions, SNAPSHOT_URL);
    }

    @Test
    public void post_ReceivableAccount_happy() throws Exception {
        // GIVEN
        assertThat(snapshot.getAccounts().size(), is(0));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .param(NAME_KEY, "Receivable")
                                .param(ACCOUNT_TYPE_KEY, "ASSET")
                                .param(SUBTYPE_NAME_KEY, "ReceivableAccount")
                                .param(CURRENCY_UNIT_KEY, CURRENCY_UNIT.getCode())
                                .param(FUTURE_TITHYNG_POLICY_KEY, FutureTithingPolicy.ALL.name())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // THEN
        assertThat(snapshot.getAccounts().size(), is(1));

        final Account account = snapshot.getAccounts().first();
        assertTrue(account instanceof ReceivableAccount);
        assertThat(
                ((ReceivableAccount) account).getFutureTithingPolicy(),
                is(FutureTithingPolicy.ALL));
        assertThat(
                ((DueDateUpdatable) account).getDueDate(),
                is(YearMonth.of(snapshot.getYear(), snapshot.getMonth()).atEndOfMonth()));

        verifyRedirect(resultActions, SNAPSHOT_URL);
    }

    @Test
    public void post_SimpleLiabilityAccount_happy() throws Exception {
        // GIVEN
        assertThat(snapshot.getAccounts().size(), is(0));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .param(NAME_KEY, "Simple Liability")
                                .param(ACCOUNT_TYPE_KEY, "LIABILITY")
                                .param(SUBTYPE_NAME_KEY, "SimpleLiabilityAccount")
                                .param(CURRENCY_UNIT_KEY, CURRENCY_UNIT.getCode())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // THEN
        assertThat(snapshot.getAccounts().size(), is(1));
        assertTrue(snapshot.getAccounts().first() instanceof SimpleLiabilityAccount);
        verifyRedirect(resultActions, SNAPSHOT_URL);
    }

    @Test
    public void post_PayableAccount_happy() throws Exception {
        // GIVEN
        assertThat(snapshot.getAccounts().size(), is(0));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .param(NAME_KEY, "Payable")
                                .param(ACCOUNT_TYPE_KEY, "LIABILITY")
                                .param(SUBTYPE_NAME_KEY, "PayableAccount")
                                .param(CURRENCY_UNIT_KEY, CURRENCY_UNIT.getCode())
                                .param(FUTURE_TITHYNG_POLICY_KEY, FutureTithingPolicy.ALL.name())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // THEN
        assertThat(snapshot.getAccounts().size(), is(1));

        final Account account = snapshot.getAccounts().first();
        assertTrue(account instanceof PayableAccount);
        assertThat(
                ((DueDateUpdatable) account).getDueDate(),
                is(YearMonth.of(snapshot.getYear(), snapshot.getMonth()).atEndOfMonth()));

        verifyRedirect(resultActions, SNAPSHOT_URL);
    }

    @Test
    public void post_futureTithingPolityNotPresentForAsset_setToNONE() throws Exception {
        // GIVEN
        assertThat(snapshot.getAccounts().size(), is(0));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .param(NAME_KEY, "Receivable")
                                .param(ACCOUNT_TYPE_KEY, "ASSET")
                                .param(SUBTYPE_NAME_KEY, "ReceivableAccount")
                                .param(CURRENCY_UNIT_KEY, CURRENCY_UNIT.getCode())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // THEN
        assertThat(snapshot.getAccounts().size(), is(1));

        final Account account = snapshot.getAccounts().first();
        assertTrue(account instanceof ReceivableAccount);
        assertThat(
                ((ReceivableAccount) account).getFutureTithingPolicy(),
                is(FutureTithingPolicy.NONE));

        verifyRedirect(resultActions, SNAPSHOT_URL);
    }

    @Test
    public void post_invalidAccountTypeAndSubtypeCombination_createAccountBasedOnSubtype()
            throws Exception {
        // GIVEN
        assertThat(snapshot.getAccounts().size(), is(0));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .param(NAME_KEY, "Payable")
                                .param(ACCOUNT_TYPE_KEY, "ASSET")
                                .param(SUBTYPE_NAME_KEY, "PayableAccount")
                                .param(CURRENCY_UNIT_KEY, CURRENCY_UNIT.getCode())
                                .param(FUTURE_TITHYNG_POLICY_KEY, FutureTithingPolicy.ALL.name())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // THEN
        assertThat(snapshot.getAccounts().size(), is(1));

        final Account account = snapshot.getAccounts().first();
        assertTrue(account instanceof PayableAccount);
        assertThat(
                ((DueDateUpdatable) account).getDueDate(),
                is(YearMonth.of(snapshot.getYear(), snapshot.getMonth()).atEndOfMonth()));

        verifyRedirect(resultActions, SNAPSHOT_URL);
    }

    @Test
    public void post_invalidAccountType_redirectToHome() throws Exception {
        // GIVEN
        assertThat(snapshot.getAccounts().size(), is(0));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .param(NAME_KEY, "Payable")
                                .param(ACCOUNT_TYPE_KEY, "INVALID_TYPE")
                                .param(SUBTYPE_NAME_KEY, "PayableAccount")
                                .param(CURRENCY_UNIT_KEY, CURRENCY_UNIT.getCode())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // THEN
        assertThat(snapshot.getAccounts().size(), is(0));
        verifyRedirect(resultActions, "/");
    }
}
