package br.net.du.myequity.controller;

import static br.net.du.myequity.controller.util.ControllerConstants.ID;
import static br.net.du.myequity.controller.util.ControllerConstants.SNAPSHOT_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.TWELVE_MONTHS_TOTALS;
import static br.net.du.myequity.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.YTD_TOTALS;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;

import br.net.du.myequity.controller.interceptor.WebController;
import br.net.du.myequity.controller.util.SnapshotUtils;
import br.net.du.myequity.controller.viewmodel.CumulativeTransactionTotalsViewModelOutput;
import br.net.du.myequity.controller.viewmodel.SnapshotViewModelOutput;
import br.net.du.myequity.controller.viewmodel.UserViewModelOutput;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.totals.CumulativeTransactionTotals;
import br.net.du.myequity.model.totals.CumulativeTransactionTotalsImpl;
import br.net.du.myequity.model.transaction.DonationCategory;
import br.net.du.myequity.model.transaction.IncomeCategory;
import br.net.du.myequity.model.transaction.InvestmentCategory;
import br.net.du.myequity.model.transaction.RecurrencePolicy;
import br.net.du.myequity.service.SnapshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@WebController
public class SnapshotController {
    private static final String SNAPSHOT = "snapshot";

    @Autowired private SnapshotService snapshotService;

    @Autowired private SnapshotUtils snapshotUtils;

    @GetMapping("/snapshot/{id}")
    public String get(@PathVariable(value = ID) final Long snapshotId, final Model model) {
        final User user = getLoggedUser(model);
        final Snapshot snapshot = snapshotUtils.validateSnapshot(model, snapshotId);

        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));
        model.addAttribute(SNAPSHOT_KEY, SnapshotViewModelOutput.of(snapshot));

        final CumulativeTransactionTotals twelveMonthsTotals =
                new CumulativeTransactionTotalsImpl(
                        snapshot,
                        snapshotService.findPastTwelveMonthsCumulativeTransactionTotals(
                                snapshotId, user.getId()));
        model.addAttribute(
                TWELVE_MONTHS_TOTALS,
                new CumulativeTransactionTotalsViewModelOutput(twelveMonthsTotals));

        model.addAttribute("recurrencePolicies", RecurrencePolicy.shortValues());

        model.addAttribute("incomeCategories", IncomeCategory.values());
        model.addAttribute("investmentCategories", InvestmentCategory.values());
        model.addAttribute("donationCategories", DonationCategory.values());

        final CumulativeTransactionTotals ytdTotals =
                new CumulativeTransactionTotalsImpl(
                        snapshot,
                        snapshotService.findYearToDateCumulativeTransactionTotals(
                                snapshot.getYear(), snapshot.getMonth(), user.getId()));
        model.addAttribute(YTD_TOTALS, new CumulativeTransactionTotalsViewModelOutput(ytdTotals));

        return SNAPSHOT;
    }
}
