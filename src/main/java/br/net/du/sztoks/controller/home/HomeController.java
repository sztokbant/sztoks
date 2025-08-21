package br.net.du.sztoks.controller.home;

import static br.net.du.sztoks.controller.util.ControllerConstants.SNAPSHOTS_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.SNAPSHOT_ID_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.USER_AGENT_REQUEST_HEADER_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.sztoks.controller.util.ControllerUtils.getLoggedUser;
import static br.net.du.sztoks.controller.util.ControllerUtils.prepareTemplate;
import static br.net.du.sztoks.controller.util.ControllerUtils.toDecimal;
import static br.net.du.sztoks.controller.util.MoneyFormatUtils.format;

import br.net.du.sztoks.controller.interceptor.WebController;
import br.net.du.sztoks.controller.viewmodel.SnapshotSummaryViewModelOutput;
import br.net.du.sztoks.controller.viewmodel.UserViewModelOutput;
import br.net.du.sztoks.model.User;
import br.net.du.sztoks.service.SnapshotService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@WebController
public class HomeController {

    @Autowired private SnapshotService snapshotService;

    @GetMapping("/")
    public String get(
            @RequestHeader(value = USER_AGENT_REQUEST_HEADER_KEY, required = false)
                    final String userAgent,
            final Model model) {
        final User user = getLoggedUser(model);

        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));

        final List<SnapshotSummaryViewModelOutput> homeSnapshotViewModelOutputs =
                snapshotService.findAllSummariesByUser(user).stream()
                        .map(
                                snapshotSummary ->
                                        new SnapshotSummaryViewModelOutput(
                                                snapshotSummary.getId(),
                                                snapshotSummary.getYear(),
                                                snapshotSummary.getMonth(),
                                                format(
                                                        snapshotSummary.getBaseCurrencyUnit(),
                                                        toDecimal(snapshotSummary.getNetWorth()))))
                        .collect(Collectors.toList());

        model.addAttribute(SNAPSHOTS_KEY, homeSnapshotViewModelOutputs);

        // For navbar
        model.addAttribute(SNAPSHOT_ID_KEY, homeSnapshotViewModelOutputs.get(0).getId());

        return prepareTemplate(userAgent, model, "home");
    }
}
