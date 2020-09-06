package br.net.du.myequity.controller;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.SimpleAssetAccount;
import br.net.du.myequity.persistence.SnapshotRepository;
import br.net.du.myequity.service.UserService;
import br.net.du.myequity.viewmodel.SnapshotViewModelOutput;
import br.net.du.myequity.viewmodel.UserViewModelOutput;
import com.google.common.collect.ImmutableSortedSet;
import org.joda.money.CurrencyUnit;
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

import java.time.LocalDate;
import java.util.Optional;

import static br.net.du.myequity.test.ControllerTestUtil.verifyRedirect;
import static br.net.du.myequity.test.ModelTestUtil.buildUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SnapshotControllerTest {

    private static final String SNAPSHOT_URL_TEMPLATE = "/snapshot/%d";
    private static final Long SNAPSHOT_ID = 99L;

    private static final String ACCOUNT_ID_VALUE = "42";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SnapshotRepository snapshotRepository;

    @MockBean
    private UserService userService;

    private User user;

    private User anotherUser;

    private Snapshot snapshot;

    private Account account;

    @BeforeEach
    public void setUp() throws Exception {
        user = buildUser();

        anotherUser = buildUser();
        anotherUser.setId(user.getId() * 7);

        snapshot = new Snapshot(LocalDate.now(), ImmutableSortedSet.of());
        snapshot.setId(SNAPSHOT_ID);

        account = new SimpleAssetAccount("Checking Account", CurrencyUnit.USD, LocalDate.now());
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
        snapshot.setUser(anotherUser);

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
        snapshot.setUser(user);

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
        assertEquals(UserViewModelOutput.of(user), mvcResult.getModelAndView().getModel().get("user"));

        assertEquals(SnapshotViewModelOutput.of(snapshot), mvcResult.getModelAndView().getModel().get("snapshot"));
    }
}
