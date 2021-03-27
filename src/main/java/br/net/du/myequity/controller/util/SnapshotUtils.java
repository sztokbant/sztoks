package br.net.du.myequity.controller.util;

import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.service.SnapshotService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class SnapshotUtils {
    @Autowired private SnapshotService snapshotService;

    public Snapshot validateSnapshot(final Model model, final Long snapshotId) {
        final User user = getLoggedUser(model);

        final Optional<Snapshot> snapshotOpt = snapshotService.findById(snapshotId);
        if (!snapshotBelongsToUser(user, snapshotOpt)) {
            throw new IllegalArgumentException();
        }

        return snapshotOpt.get();
    }

    private boolean snapshotBelongsToUser(final User user, final Optional<Snapshot> snapshotOpt) {
        return snapshotOpt.isPresent() && snapshotOpt.get().getUser().equals(user);
    }
}
