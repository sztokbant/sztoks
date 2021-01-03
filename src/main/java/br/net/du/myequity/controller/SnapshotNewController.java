package br.net.du.myequity.controller;

import static br.net.du.myequity.controller.util.ControllerConstants.REDIRECT_SNAPSHOT_TEMPLATE;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;

import br.net.du.myequity.controller.interceptor.WebController;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.service.SnapshotService;
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

        final Snapshot newSnapshot = snapshotService.newSnapshot(user);

        return String.format(REDIRECT_SNAPSHOT_TEMPLATE, newSnapshot.getId());
    }
}
