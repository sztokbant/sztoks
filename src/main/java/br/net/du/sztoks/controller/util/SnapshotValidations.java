package br.net.du.sztoks.controller.util;

import static br.net.du.sztoks.controller.util.ControllerUtils.getLoggedUser;

import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.User;
import br.net.du.sztoks.service.SnapshotService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

@Component
public class SnapshotValidations {
    @Autowired private SnapshotService snapshotService;

    @Transactional
    public Snapshot validateSnapshot(final Model model, final Long snapshotId) {
        final User user = getLoggedUser(model);

        if (snapshotId == 0) {
            return snapshotService.findTopByUser(user);
        }

        final Optional<Snapshot> snapshotOpt = snapshotService.findById(snapshotId);
        if (!snapshotBelongsToUser(user, snapshotOpt)) {
            throw new IllegalArgumentException();
        }

        return snapshotOpt.get();
    }

    private boolean snapshotBelongsToUser(final User user, final Optional<Snapshot> snapshotOpt) {
        return snapshotOpt.isPresent() && snapshotOpt.get().getUser().equals(user);
    }

    public Snapshot validateLockAndRefreshSnapshot(final Model model, final Long snapshotId) {
        final User user = getLoggedUser(model);

        final Optional<Snapshot> snapshotOpt =
                snapshotService.findByIdAndUserId(snapshotId, user.getId());
        if (!snapshotOpt.isPresent()) {
            throw new IllegalArgumentException();
        }

        final Snapshot snapshot = snapshotOpt.get();
        snapshotService.refresh(snapshot);

        return snapshot;
    }
}
