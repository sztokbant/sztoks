package br.net.du.sztoks.controller.snapshot;

import static br.net.du.sztoks.controller.util.ControllerConstants.ID;
import static br.net.du.sztoks.controller.util.ControllerConstants.REDIRECT_TO_HOME;
import static br.net.du.sztoks.controller.util.ControllerUtils.getLoggedUser;

import br.net.du.sztoks.controller.interceptor.WebController;
import br.net.du.sztoks.controller.util.SnapshotValidations;
import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.User;
import br.net.du.sztoks.service.SnapshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@WebController
public class SnapshotDeleteController {
    @Autowired private SnapshotService snapshotService;

    @Autowired private SnapshotValidations snapshotValidations;

    @PostMapping("/snapshot/delete/{id}")
    @Transactional
    public String delete(@PathVariable(value = ID) final Long snapshotId, final Model model) {
        final User user = getLoggedUser(model);
        final Snapshot snapshot = snapshotValidations.validateSnapshot(model, snapshotId);

        snapshotService.deleteSnapshot(user, snapshot);

        return REDIRECT_TO_HOME;
    }
}
