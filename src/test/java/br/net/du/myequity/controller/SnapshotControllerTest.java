package br.net.du.myequity.controller;

import static br.net.du.myequity.test.ControllerTestUtils.verifyRedirect;
import static br.net.du.myequity.test.ModelTestUtils.buildUser;
import static br.net.du.myequity.test.TestConstants.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.myequity.controller.viewmodel.SnapshotViewModelOutput;
import br.net.du.myequity.controller.viewmodel.UserViewModelOutput;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.SimpleAssetAccount;
import br.net.du.myequity.service.SnapshotService;
import com.google.common.collect.ImmutableSortedSet;
import java.time.LocalDate;
import java.util.Optional;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class SnapshotControllerTest extends GetControllerTestBase {

    private static final long SNAPSHOT_ID = 99L;
    private static final long SNAPSHOT_INDEX = 1L;
    private static final String ACCOUNT_ID_VALUE = "42";

    @MockBean private SnapshotService snapshotService;

    private User anotherUser;

    private Snapshot snapshot;

    private Account account;

    public SnapshotControllerTest() {
        super(String.format("/snapshot/%d", SNAPSHOT_ID));
    }

    @BeforeEach
    public void setUp() throws Exception {
        user = buildUser();

        anotherUser = buildUser();
        anotherUser.setId(user.getId() * 7);

        snapshot = new Snapshot(SNAPSHOT_INDEX, now, ImmutableSortedSet.of());
        snapshot.setId(SNAPSHOT_ID);

        account = new SimpleAssetAccount("Checking Account", CurrencyUnit.USD, LocalDate.now());
        account.setId(Long.parseLong(ACCOUNT_ID_VALUE));
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

    @Test
    public void get_happy() throws Exception {
        // GIVEN
        snapshot.setUser(user);

        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(snapshotService.findById(eq(SNAPSHOT_ID))).thenReturn(Optional.of(snapshot));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.get(url).with(user(user.getEmail())));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        assertEquals("snapshot", mvcResult.getModelAndView().getViewName());
        assertEquals(
                UserViewModelOutput.of(user), mvcResult.getModelAndView().getModel().get("user"));

        assertEquals(
                SnapshotViewModelOutput.of(snapshot),
                mvcResult.getModelAndView().getModel().get("snapshot"));
    }
}
