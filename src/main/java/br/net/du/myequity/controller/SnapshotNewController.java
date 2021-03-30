package br.net.du.myequity.controller;

import static br.net.du.myequity.controller.util.ControllerConstants.REDIRECT_SNAPSHOT_TEMPLATE;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;

import br.net.du.myequity.controller.interceptor.WebController;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.service.SnapshotService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@WebController
public class SnapshotNewController {

    @Autowired private SnapshotService snapshotService;

    @PostMapping("/snapshot/new")
    public String newSnapshot(final Model model) {
        final User user = getLoggedUser(model);

        // TODO Receive snapshotName as input
        final String snapshotName = getNextSnapshotName(user);

        final Snapshot newSnapshot = snapshotService.newSnapshot(user, snapshotName);

        return String.format(REDIRECT_SNAPSHOT_TEMPLATE, newSnapshot.getId());
    }

    private String getNextSnapshotName(final User user) {
        final Snapshot latestSnapshot = user.getSnapshots().first();

        final String[] latestSnapshotNameParts = latestSnapshot.getName().split("-");
        final int latestSnapshotYear = Integer.valueOf(latestSnapshotNameParts[0]);
        final int latestSnapshotMonth = Integer.valueOf(latestSnapshotNameParts[1]);

        final LocalDate latestSnapshotDate =
                LocalDate.of(latestSnapshotYear, latestSnapshotMonth, 1);

        final LocalDate newSnapshotDate = latestSnapshotDate.plusMonths(1);

        final String snapshotName =
                String.format(
                        "%04d-%02d",
                        newSnapshotDate.getYear(), newSnapshotDate.getMonth().getValue());

        return snapshotName;
    }
}
