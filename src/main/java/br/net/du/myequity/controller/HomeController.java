package br.net.du.myequity.controller;

import static br.net.du.myequity.controller.util.ControllerConstants.SNAPSHOTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;
import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

import br.net.du.myequity.controller.viewmodel.SnapshotSummaryViewModelOutput;
import br.net.du.myequity.controller.viewmodel.UserViewModelOutput;
import br.net.du.myequity.model.User;
import br.net.du.myequity.service.SnapshotService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired private SnapshotService snapshotService;

    @GetMapping("/")
    public String get(final Model model) {
        final User user = getLoggedUser(model);

        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));

        final List<SnapshotSummaryViewModelOutput> homeSnapshotViewModelOutputs =
                snapshotService.findAllSummariesByUser(user).stream()
                        .map(
                                snapshotSummary ->
                                        new SnapshotSummaryViewModelOutput(
                                                snapshotSummary.getId(),
                                                snapshotSummary.getName(),
                                                format(
                                                        snapshotSummary.getBaseCurrencyUnit(),
                                                        toDecimal(snapshotSummary.getNetWorth()))))
                        .collect(Collectors.toList());

        model.addAttribute(SNAPSHOTS_KEY, homeSnapshotViewModelOutputs);

        return "home";
    }
}
