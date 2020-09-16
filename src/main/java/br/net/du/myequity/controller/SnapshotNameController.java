package br.net.du.myequity.controller;

import br.net.du.myequity.controller.model.EntityNameJsonRequest;
import br.net.du.myequity.controller.model.EntityNameJsonResponse;
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
public class SnapshotNameController extends AccountControllerBase {
    @Autowired
    SnapshotRepository snapshotRepository;

    @PostMapping("/snapshot/updateName")
    public EntityNameJsonResponse post(final Model model,
                                       @RequestBody final EntityNameJsonRequest entityNameJsonRequest) {
        final Snapshot snapshot = getSnapshot(model, entityNameJsonRequest.getId());

        snapshot.setName(entityNameJsonRequest.getNewValue().trim());
        snapshotRepository.save(snapshot);

        return EntityNameJsonResponse.builder().name(snapshot.getName()).build();
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
