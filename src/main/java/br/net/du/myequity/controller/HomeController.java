package br.net.du.myequity.controller;

import static br.net.du.myequity.controller.util.ControllerConstants.SNAPSHOTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;
import static br.net.du.myequity.controller.util.ControllerUtils.prepareTemplate;
import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

import br.net.du.myequity.controller.interceptor.WebController;
import br.net.du.myequity.controller.viewmodel.SnapshotSummaryViewModelOutput;
import br.net.du.myequity.controller.viewmodel.UserViewModelOutput;
import br.net.du.myequity.model.User;
import br.net.du.myequity.service.SnapshotService;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@WebController
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Autowired private SnapshotService snapshotService;

    @GetMapping("/")
    public String get(final Model model, final Device device) {
        LOGGER.info("*** INSIDE HomeController: " + device.toString() + " ***");

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

        return prepareTemplate(model, device, "home");
    }
}
