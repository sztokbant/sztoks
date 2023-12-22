package br.net.du.sztoks.controller.transaction;

import static br.net.du.sztoks.test.ControllerTestUtils.verifyRedirect;
import static br.net.du.sztoks.test.ModelTestUtils.SNAPSHOT_ID;
import static br.net.du.sztoks.test.TestConstants.AMOUNT_KEY;
import static br.net.du.sztoks.test.TestConstants.CATEGORY_KEY;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT_KEY;
import static br.net.du.sztoks.test.TestConstants.DATE_KEY;
import static br.net.du.sztoks.test.TestConstants.DESCRIPTION_KEY;
import static br.net.du.sztoks.test.TestConstants.IS_TAX_DEDUCTIBLE_KEY;
import static br.net.du.sztoks.test.TestConstants.RECURRENCE_POLICY_KEY;
import static br.net.du.sztoks.test.TestConstants.TITHING_PERCENTAGE_KEY;
import static br.net.du.sztoks.test.TestConstants.TYPE_NAME_KEY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import br.net.du.sztoks.controller.PostControllerTestBase;
import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.transaction.DonationCategory;
import br.net.du.sztoks.model.transaction.DonationTransaction;
import br.net.du.sztoks.model.transaction.IncomeCategory;
import br.net.du.sztoks.model.transaction.IncomeTransaction;
import br.net.du.sztoks.model.transaction.InvestmentCategory;
import br.net.du.sztoks.model.transaction.InvestmentTransaction;
import br.net.du.sztoks.model.transaction.RecurrencePolicy;
import br.net.du.sztoks.model.transaction.Transaction;
import br.net.du.sztoks.service.AccountService;
import br.net.du.sztoks.service.SnapshotService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionNewControllerTest extends PostControllerTestBase {

    private static final String POST_URL = "/snapshot/" + SNAPSHOT_ID + "/newTransaction";
    private static final String SNAPSHOT_URL = String.format("/snapshot/%d", SNAPSHOT_ID);

    @MockBean protected AccountService accountService;
    @MockBean protected SnapshotService snapshotService;

    private Snapshot snapshot;

    public TransactionNewControllerTest() {
        super(POST_URL);
    }

    @BeforeEach
    public void setUp() {
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        snapshot = user.getSnapshots().first();
        when(snapshotService.findByIdAndUserId(SNAPSHOT_ID, user.getId()))
                .thenReturn(Optional.of(snapshot));
    }

    @Test
    public void post_IncomeTransaction_happy() throws Exception {
        // GIVEN
        assertThat(snapshot.getTransactions().size(), is(0));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .param(TYPE_NAME_KEY, "INCOME")
                                .param(DESCRIPTION_KEY, "Income Transaction")
                                .param(DATE_KEY, "2023-12-22")
                                .param(CURRENCY_UNIT_KEY, CURRENCY_UNIT.getCode())
                                .param(AMOUNT_KEY, "108.00")
                                .param(RECURRENCE_POLICY_KEY, "RECURRING")
                                .param(TITHING_PERCENTAGE_KEY, "10.00")
                                .param(CATEGORY_KEY, "JOB")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // THEN
        assertThat(snapshot.getTransactions().size(), is(1));

        final Transaction transaction = snapshot.getTransactions().first();
        assertTrue(transaction instanceof IncomeTransaction);

        final IncomeTransaction incomeTransaction = (IncomeTransaction) transaction;
        assertThat(incomeTransaction.getDescription(), is("Income Transaction"));
        assertThat(incomeTransaction.getDate(), is(LocalDate.of(2023, 12, 22)));
        assertThat(incomeTransaction.getCurrencyUnit(), is(CURRENCY_UNIT));
        assertThat(incomeTransaction.getAmount(), is(new BigDecimal("108.00")));
        assertThat(incomeTransaction.getRecurrencePolicy(), is(RecurrencePolicy.RECURRING));
        assertThat(incomeTransaction.getTithingPercentage(), is(new BigDecimal("10.00")));
        assertThat(incomeTransaction.getCategory(), is(IncomeCategory.JOB));

        verify(accountService).save(ArgumentMatchers.any(Account.class));
        verifyRedirect(resultActions, SNAPSHOT_URL);
    }

    @Test
    public void post_InvestmentTransaction_happy() throws Exception {
        // GIVEN
        assertThat(snapshot.getTransactions().size(), is(0));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .param(TYPE_NAME_KEY, "INVESTMENT")
                                .param(DESCRIPTION_KEY, "Investment Transaction")
                                .param(DATE_KEY, "2023-12-22")
                                .param(CURRENCY_UNIT_KEY, CURRENCY_UNIT.getCode())
                                .param(AMOUNT_KEY, "108.00")
                                .param(RECURRENCE_POLICY_KEY, "RECURRING")
                                .param(TITHING_PERCENTAGE_KEY, "10.00")
                                .param(CATEGORY_KEY, "MID_TERM")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // THEN
        assertThat(snapshot.getTransactions().size(), is(1));

        final Transaction transaction = snapshot.getTransactions().first();
        assertTrue(transaction instanceof InvestmentTransaction);

        final InvestmentTransaction investmentTransaction = (InvestmentTransaction) transaction;
        assertThat(investmentTransaction.getDescription(), is("Investment Transaction"));
        assertThat(investmentTransaction.getDate(), is(LocalDate.of(2023, 12, 22)));
        assertThat(investmentTransaction.getCurrencyUnit(), is(CURRENCY_UNIT));
        assertThat(investmentTransaction.getAmount(), is(new BigDecimal("108.00")));
        assertThat(investmentTransaction.getRecurrencePolicy(), is(RecurrencePolicy.RECURRING));
        assertThat(investmentTransaction.getCategory(), is(InvestmentCategory.MID_TERM));

        verify(accountService, times(0)).save(ArgumentMatchers.any(Account.class));
        verifyRedirect(resultActions, SNAPSHOT_URL);
    }

    @Test
    public void post_DonationTransaction_happy() throws Exception {
        // GIVEN
        assertThat(snapshot.getTransactions().size(), is(0));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .param(TYPE_NAME_KEY, "DONATION")
                                .param(DESCRIPTION_KEY, "Donation Transaction")
                                .param(DATE_KEY, "2023-12-22")
                                .param(CURRENCY_UNIT_KEY, CURRENCY_UNIT.getCode())
                                .param(AMOUNT_KEY, "108.00")
                                .param(RECURRENCE_POLICY_KEY, "RECURRING")
                                .param(IS_TAX_DEDUCTIBLE_KEY, "true")
                                .param(CATEGORY_KEY, "FAMILY")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // THEN
        assertThat(snapshot.getTransactions().size(), is(1));

        final Transaction transaction = snapshot.getTransactions().first();
        assertTrue(transaction instanceof DonationTransaction);

        final DonationTransaction donationTransaction = (DonationTransaction) transaction;
        assertThat(donationTransaction.getDescription(), is("Donation Transaction"));
        assertThat(donationTransaction.getDate(), is(LocalDate.of(2023, 12, 22)));
        assertThat(donationTransaction.getCurrencyUnit(), is(CURRENCY_UNIT));
        assertThat(donationTransaction.getAmount(), is(new BigDecimal("108.00")));
        assertThat(donationTransaction.getRecurrencePolicy(), is(RecurrencePolicy.RECURRING));
        assertThat(donationTransaction.isTaxDeductible(), is(true));
        assertThat(donationTransaction.getCategory(), is(DonationCategory.FAMILY));

        verify(accountService).save(ArgumentMatchers.any(Account.class));
        verifyRedirect(resultActions, SNAPSHOT_URL);
    }
}
