package br.net.du.myequity.controller;

import static br.net.du.myequity.controller.util.ControllerConstants.REDIRECT_SNAPSHOT_TEMPLATE;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;
import static br.net.du.myequity.model.util.SnapshotUtils.computeNextSnapshotPeriod;

import br.net.du.myequity.controller.interceptor.WebController;
import br.net.du.myequity.exception.SztoksException;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.service.SnapshotService;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

@WebController
public class SnapshotNewController {

    @Autowired private SnapshotService snapshotService;

    @PostMapping("/snapshot/new")
    @Transactional
    public String newSnapshot(final Model model) {
        final User user = getLoggedUser(model);
        final Snapshot snapshot = snapshotService.findTopByUser(user);

        final Optional<LocalDate> nextSnapshotPeriodOpt = computeNextSnapshotPeriod(snapshot);

        if (!nextSnapshotPeriodOpt.isPresent()) {
            throw new SztoksException(String.format("Too early to create a new Snapshot"));
        }

        final LocalDate nextSnapshotPeriod = nextSnapshotPeriodOpt.get();

        final Snapshot newSnapshot =
                snapshotService.newSnapshot(
                        user, nextSnapshotPeriod.getYear(), nextSnapshotPeriod.getMonthValue());

        return String.format(REDIRECT_SNAPSHOT_TEMPLATE, newSnapshot.getId());
    }
}
