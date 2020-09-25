package br.net.du.myequity.controller;

import br.net.du.myequity.controller.model.EntityRenameJsonRequest;
import br.net.du.myequity.controller.model.EntityRenameJsonResponse;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.persistence.SnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;
import static br.net.du.myequity.controller.util.ControllerUtils.snapshotBelongsToUser;

@RestController
public class SnapshotRenameController extends AccountControllerBase {
    @Autowired
    SnapshotRepository snapshotRepository;

    @PostMapping("/snapshot/updateName")
    public EntityRenameJsonResponse post(final Model model,
                                         @RequestBody final EntityRenameJsonRequest entityNameJsonRequest) {
        final Snapshot snapshot = getSnapshot(model, entityNameJsonRequest.getId());

        snapshot.setName(entityNameJsonRequest.getNewValue().trim());
        snapshotRepository.save(snapshot);

        return EntityRenameJsonResponse.builder().name(snapshot.getName()).build();
    }

    Snapshot getSnapshot(final Model model, final Long snapshotId) {
        final User user = getLoggedUser(model);

        final Optional<Snapshot> snapshotOpt = snapshotRepository.findById(snapshotId);
        if (!snapshotBelongsToUser(user, snapshotOpt)) {
            throw new IllegalArgumentException();
        }

        return snapshotOpt.get();
    }
}
