package br.net.du.myequity.controller;

import static br.net.du.myequity.test.ControllerTestUtils.verifyRedirect;
import static br.net.du.myequity.test.ModelTestUtils.SNAPSHOT_ID;
import static br.net.du.myequity.test.ModelTestUtils.buildUser;
import static br.net.du.myequity.test.TestConstants.FIRST_SNAPSHOT_NAME;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.SimpleAssetAccount;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
import br.net.du.myequity.service.AccountService;
import br.net.du.myequity.service.SnapshotService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import java.time.LocalDate;
import java.util.Optional;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

abstract class SnapshotControllerGetTestBase extends GetControllerTestBase {

    protected static final long ASSET_ACCOUNT_ID = 42L;
    protected static final long LIABILITY_ACCOUNT_ID = 72L;
    protected static final long SNAPSHOT_INDEX = 1L;

    @MockBean protected SnapshotService snapshotService;

    @MockBean protected AccountService accountService;

    protected User anotherUser;

    protected Snapshot snapshot;

    protected Account assetAccount;

    protected Account liabilityAccount;

    public SnapshotControllerGetTestBase(final String url) {
        super(url);
    }

    @BeforeEach
    public void snapshotControllerGetTestBaseSetUp() {
        anotherUser = buildUser();
        anotherUser.setId(user.getId() * 7);

        snapshot = new Snapshot(FIRST_SNAPSHOT_NAME, ImmutableSortedSet.of(), ImmutableList.of());
        snapshot.setId(SNAPSHOT_ID);

        assetAccount =
                new SimpleAssetAccount("Checking Account", CurrencyUnit.USD, LocalDate.now());
        assetAccount.setId(ASSET_ACCOUNT_ID);

        liabilityAccount =
                new SimpleLiabilityAccount("Mortgage", CurrencyUnit.USD, LocalDate.now());
        liabilityAccount.setId(LIABILITY_ACCOUNT_ID);
    }

    @Test
    public void get_snapshotNotFound_redirect() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(snapshotService.findById(eq(SNAPSHOT_ID))).thenReturn(Optional.empty());

        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.get(url).with(user(user.getEmail())));

        // THEN
        verifyRedirect(resultActions, "/");
    }

    @Test
    public void get_snapshotDoesNotBelongToUser_redirect() throws Exception {
        // GIVEN
        snapshot.setUser(anotherUser);

        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(snapshotService.findById(eq(SNAPSHOT_ID))).thenReturn(Optional.of(snapshot));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.get(url).with(user(user.getEmail())));

        // THEN
        verifyRedirect(resultActions, "/");
    }
}
