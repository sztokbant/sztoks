package br.net.du.myequity.controller;

import static br.net.du.myequity.controller.util.ControllerConstants.ID;
import static br.net.du.myequity.controller.util.ControllerConstants.SNAPSHOT_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.TRANSACTION_CATEGORY_TOTALS;
import static br.net.du.myequity.controller.util.ControllerConstants.TWELVE_MONTHS_TOTALS;
import static br.net.du.myequity.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.YTD_TOTALS;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;
import static br.net.du.myequity.controller.util.ControllerUtils.prepareTemplate;

import br.net.du.myequity.controller.interceptor.WebController;
import br.net.du.myequity.controller.util.SnapshotUtils;
import br.net.du.myequity.controller.viewmodel.CumulativeTransactionCategoryTotalsViewModelOutput;
import br.net.du.myequity.controller.viewmodel.CumulativeTransactionTotalsViewModelOutput;
import br.net.du.myequity.controller.viewmodel.SnapshotViewModelOutput;
import br.net.du.myequity.controller.viewmodel.UserViewModelOutput;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.FutureTithingPolicy;
import br.net.du.myequity.model.totals.CumulativeTransactionCategoryTotals;
import br.net.du.myequity.model.totals.CumulativeTransactionTotals;
import br.net.du.myequity.model.totals.CumulativeTransactionTotalsImpl;
import br.net.du.myequity.model.transaction.DonationCategory;
import br.net.du.myequity.model.transaction.IncomeCategory;
import br.net.du.myequity.model.transaction.InvestmentCategory;
import br.net.du.myequity.model.transaction.RecurrencePolicy;
import br.net.du.myequity.service.SnapshotService;
import java.util.List;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@WebController
public class SnapshotController {
    private static final String SNAPSHOT = "snapshot";

    @Autowired private SnapshotService snapshotService;

    @Autowired private SnapshotUtils snapshotUtils;

    @GetMapping("/snapshot/{id}")
    public String get(
            @PathVariable(value = ID) final Long snapshotId,
            final Model model,
            final Device device) {
        final User user = getLoggedUser(model);
        final Snapshot snapshot = snapshotUtils.validateSnapshot(model, snapshotId);

        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));
        model.addAttribute(SNAPSHOT_KEY, SnapshotViewModelOutput.of(snapshot));

        model.addAttribute("futureTithingPolicies", FutureTithingPolicy.shortValues());
        model.addAttribute("recurrencePolicies", RecurrencePolicy.shortValues());
        model.addAttribute("incomeCategories", IncomeCategory.values());
        model.addAttribute("investmentCategories", InvestmentCategory.values());
        model.addAttribute("donationCategories", DonationCategory.values());

        model.addAttribute(
                TWELVE_MONTHS_TOTALS,
                getPastTwelveMonthsTransactionTotalsViewModelOutput(user, snapshot));

        model.addAttribute(
                YTD_TOTALS, getYearToDateTransactionTotalsViewModelOutput(user, snapshot));

        final CumulativeTransactionCategoryTotalsViewModelOutput value =
                getCumulativeTransactionCategoryTotalsViewModelOutput(user, snapshot);
        model.addAttribute(TRANSACTION_CATEGORY_TOTALS, value);

        return prepareTemplate(model, device, SNAPSHOT);
    }

    private CumulativeTransactionTotalsViewModelOutput
            getPastTwelveMonthsTransactionTotalsViewModelOutput(
                    @NonNull final User user, @NonNull final Snapshot snapshot) {
        final CumulativeTransactionTotals twelveMonthsTotals =
                new CumulativeTransactionTotalsImpl(
                        snapshot,
                        snapshotService.findPastTwelveMonthsTransactionTotals(
                                snapshot.getId(), user.getId()));

        return new CumulativeTransactionTotalsViewModelOutput(twelveMonthsTotals);
    }

    private CumulativeTransactionTotalsViewModelOutput
            getYearToDateTransactionTotalsViewModelOutput(
                    @NonNull final User user, @NonNull final Snapshot snapshot) {
        final CumulativeTransactionTotals ytdTotals =
                new CumulativeTransactionTotalsImpl(
                        snapshot,
                        snapshotService.findYearToDateTransactionTotals(
                                snapshot.getYear(), snapshot.getMonth(), user.getId()));

        return new CumulativeTransactionTotalsViewModelOutput(ytdTotals);
    }

    private CumulativeTransactionCategoryTotalsViewModelOutput
            getCumulativeTransactionCategoryTotalsViewModelOutput(
                    @NonNull final User user, @NonNull final Snapshot snapshot) {
        final List<CumulativeTransactionCategoryTotals> yearToDateCategoryTotals =
                snapshotService.findYearToDateTransactionCategoryTotals(
                        snapshot.getYear(), snapshot.getMonth(), user.getId());

        final List<CumulativeTransactionCategoryTotals> twelveMonthsCategoryTotals =
                snapshotService.findPastTwelveMonthsCumulativeTransactionCategoryTotals(
                        snapshot.getId(), user.getId());

        return new CumulativeTransactionCategoryTotalsViewModelOutput(
                yearToDateCategoryTotals, twelveMonthsCategoryTotals);
    }
}
