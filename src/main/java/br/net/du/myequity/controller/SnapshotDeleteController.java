package br.net.du.myequity.controller;

import static br.net.du.myequity.controller.util.ControllerConstants.REDIRECT_TO_HOME;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;

import br.net.du.myequity.controller.interceptor.WebController;
import br.net.du.myequity.controller.util.SnapshotUtils;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.service.SnapshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@WebController
public class SnapshotDeleteController {
    @Autowired private SnapshotService snapshotService;

    @Autowired private SnapshotUtils snapshotUtils;

    @PostMapping("/snapshot/delete/{id}")
    public String delete(@PathVariable(value = "id") final Long snapshotId, final Model model) {
        final User user = getLoggedUser(model);
        final Snapshot snapshot = snapshotUtils.getSnapshot(model, snapshotId);

        snapshotService.deleteSnapshot(user, snapshot);

        return REDIRECT_TO_HOME;
    }
}
