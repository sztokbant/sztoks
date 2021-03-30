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

        // TODO Receive as input
        final LocalDate now = LocalDate.now();
        final String snapshotName =
                String.format("%04d-%02d", now.getYear(), now.getMonth().getValue());

        final Snapshot newSnapshot = snapshotService.newSnapshot(user, snapshotName);

        return String.format(REDIRECT_SNAPSHOT_TEMPLATE, newSnapshot.getId());
    }
}
