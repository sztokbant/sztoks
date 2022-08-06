package br.net.du.sztoks.controller;

import static br.net.du.sztoks.test.ControllerTestUtils.verifyRedirect;
import static br.net.du.sztoks.test.ModelTestUtils.buildUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.sztoks.controller.viewmodel.SnapshotSummaryViewModelOutput;
import br.net.du.sztoks.controller.viewmodel.UpdatableTotals;
import br.net.du.sztoks.controller.viewmodel.UserViewModelOutput;
import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.SnapshotSummary;
import br.net.du.sztoks.model.User;
import br.net.du.sztoks.service.SnapshotService;
import br.net.du.sztoks.service.UserService;
import com.google.common.collect.ImmutableList;
import java.math.BigDecimal;
import java.util.Map;
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

    private static final String SNAPSHOTS_KEY = "snapshots";
    private static final String USER_KEY = "user";

    @Autowired private MockMvc mvc;

    @MockBean private UserService userService;

    @MockBean private SnapshotService snapshotService;

    private User user;

    private Snapshot snapshot;

    @BeforeEach
    public void setUp() throws Exception {
        user = buildUser();
        snapshot = user.getSnapshots().first();
        user.addSnapshot(snapshot);
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
        when(snapshotService.findAllSummariesByUser(eq(user)))
                .thenReturn(
                        ImmutableList.of(
                                new SnapshotSummary(
                                        user.getId(),
                                        snapshot.getId(),
                                        snapshot.getYear(),
                                        snapshot.getMonth(),
                                        snapshot.getBaseCurrencyUnit().getCode(),
                                        BigDecimal.ZERO,
                                        BigDecimal.ZERO)));

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
                ImmutableList.of(
                        new SnapshotSummaryViewModelOutput(
                                snapshot.getId(),
                                snapshot.getYear(),
                                snapshot.getMonth(),
                                new UpdatableTotals(snapshot).getNetWorth())),
                model.get(SNAPSHOTS_KEY));
    }
}
