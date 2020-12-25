package br.net.du.myequity.controller;

import br.net.du.myequity.controller.util.SnapshotUtils;
import br.net.du.myequity.controller.viewmodel.EntityRenameJsonRequest;
import br.net.du.myequity.controller.viewmodel.EntityRenameJsonResponse;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.service.SnapshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SnapshotRenameController {
    @Autowired private SnapshotService snapshotService;

    @Autowired private SnapshotUtils snapshotUtils;

    @PostMapping("/snapshot/updateName")
    public EntityRenameJsonResponse post(
            final Model model, @RequestBody final EntityRenameJsonRequest entityNameJsonRequest) {
        final Snapshot snapshot = snapshotUtils.getSnapshot(model, entityNameJsonRequest.getId());

        snapshot.setName(entityNameJsonRequest.getNewValue().trim());
        snapshotService.save(snapshot);

        return EntityRenameJsonResponse.builder().name(snapshot.getName()).build();
    }
}
