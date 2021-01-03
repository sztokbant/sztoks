package br.net.du.myequity.controller;

import static br.net.du.myequity.test.ControllerTestUtils.verifyRedirect;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.SimpleAssetAccount;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
import com.google.common.collect.ImmutableList;
import java.time.LocalDate;
import java.util.Optional;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class SnapshotAddAccountControllerPostTest extends SnapshotControllerPostTestBase {

    private static final String ACCOUNTS = "accounts";
    private static final long ASSET_ACCOUNT_ID = 42L;
    private static final long BOGUS_ACCOUNT_ID = 666L;
    private static final long LIABILITY_ACCOUNT_ID = 72L;
    private static final String SNAPSHOT_URL = String.format("/snapshot/%d", SNAPSHOT_ID);
    private static final String URL = String.format("/snapshot/addAccounts/%d", SNAPSHOT_ID);

    protected Account assetAccount;

    protected Account liabilityAccount;

    public SnapshotAddAccountControllerPostTest() {
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
    public void post_accountDoesNotBelongToUser_noOp() throws Exception {
        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param(ACCOUNTS, new String[] {String.valueOf(BOGUS_ACCOUNT_ID)}));

        // THEN
        verify(snapshotService, never()).save(eq(snapshot));
        verifyRedirect(resultActions, SNAPSHOT_URL);
    }

    @Test
    public void post_noAccountsInSnapshot_happy() throws Exception {
        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param(
                                        ACCOUNTS,
                                        new String[] {
                                            String.valueOf(ASSET_ACCOUNT_ID),
                                            String.valueOf(LIABILITY_ACCOUNT_ID)
                                        }));

        // THEN
        verify(snapshotService).save(eq(snapshot));
        verifyRedirect(resultActions, SNAPSHOT_URL);
    }

    @Test
    public void post_assetAlreadyInSnapshot_happy() throws Exception {
        // GIVEN
        snapshot.addAccountSnapshot(assetAccount.newEmptySnapshot());

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param(
                                        ACCOUNTS,
                                        new String[] {
                                            String.valueOf(ASSET_ACCOUNT_ID),
                                            String.valueOf(LIABILITY_ACCOUNT_ID)
                                        }));

        // THEN
        verify(snapshotService).save(eq(snapshot));
        verifyRedirect(resultActions, SNAPSHOT_URL);
    }

    @Test
    public void post_allAccountsAlreadyInSnapshot_happy() throws Exception {
        // GIVEN
        snapshot.addAccountSnapshot(assetAccount.newEmptySnapshot());
        snapshot.addAccountSnapshot(liabilityAccount.newEmptySnapshot());

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param(
                                        ACCOUNTS,
                                        new String[] {
                                            String.valueOf(ASSET_ACCOUNT_ID),
                                            String.valueOf(LIABILITY_ACCOUNT_ID)
                                        }));

        // THEN
        verify(snapshotService, never()).save(eq(snapshot));
        verifyRedirect(resultActions, SNAPSHOT_URL);
    }
}
