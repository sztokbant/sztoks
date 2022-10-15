package br.net.du.sztoks.controller.account;

import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_ACCOUNT_TYPE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_DEFAULT_TITHING_PERCENTAGE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_FUTURE_TITHING_BALANCE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_NET_WORTH;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_TOTAL_FOR_ACCOUNT_TYPE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_TOTAL_TITHING_BALANCE;
import static br.net.du.sztoks.test.ModelTestUtils.SNAPSHOT_ID;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.sztoks.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.sztoks.model.account.AccountType;
import br.net.du.sztoks.model.account.FutureTithingPolicy;
import br.net.du.sztoks.model.account.SimpleAssetAccount;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class SnapshotUpdateDefaultTithingPercentageControllerTest extends AccountAjaxControllerTestBase {

    private static final BigDecimal CURRENT_BALANCE = new BigDecimal("99.00");

    SnapshotUpdateDefaultTithingPercentageControllerTest() {
        super("/snapshot/updateDefaultTithingPercentage", "30.00");
    }

    @BeforeEach
    public void setUp() throws Exception {
        account =
                new SimpleAssetAccount(
                        "Savings",
                        CURRENCY_UNIT,
                        FutureTithingPolicy.ALL,
                        LocalDate.now(),
                        CURRENT_BALANCE);
        account.setId(ACCOUNT_ID);

        final ValueUpdateJsonRequest valueUpdateJsonRequest =
                ValueUpdateJsonRequest.builder().snapshotId(SNAPSHOT_ID).newValue(newValue).build();
        requestContent = new ObjectMapper().writeValueAsString(valueUpdateJsonRequest);
    }

    @Test
    public void updateDefaultTithingPercentage_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        snapshot.addAccount(account);

        assertThat(snapshot.getDefaultTithingPercentage(), is(new BigDecimal("15.00")));
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("84.15")));

        when(snapshotService.findByIdAndUserId(SNAPSHOT_ID, user.getId()))
                .thenReturn(Optional.of(snapshot));

        when(accountService.findByIdAndSnapshotId(ACCOUNT_ID, SNAPSHOT_ID))
                .thenReturn(Optional.of(account));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(user.getEmail()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestContent));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        final String resultContentAsString = mvcResult.getResponse().getContentAsString();
        assertNotNull(resultContentAsString);

        final JsonNode jsonNode = new ObjectMapper().readTree(resultContentAsString);

        // Only checking fields relevant to the AJAX callback
        assertThat(jsonNode.get(JSON_DEFAULT_TITHING_PERCENTAGE).asText(), is("30.00%"));

        assertThat(jsonNode.get(JSON_ACCOUNT_TYPE).asText(), is(AccountType.LIABILITY.toString()));
        assertThat(jsonNode.get(JSON_TOTAL_FOR_ACCOUNT_TYPE).asText(), is("$29.70"));

        assertThat(jsonNode.get(JSON_FUTURE_TITHING_BALANCE).asText(), is("$29.70"));
        assertThat(jsonNode.get(JSON_TOTAL_TITHING_BALANCE).asText(), is("$29.70"));

        assertThat(jsonNode.get(JSON_NET_WORTH).asText(), is("$69.30"));

        verify(snapshotService).findByIdAndUserId(eq(SNAPSHOT_ID), eq(user.getId()));
        verify(snapshotService).save(snapshot);
    }
}
