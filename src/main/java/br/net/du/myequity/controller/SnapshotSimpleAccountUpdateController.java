package br.net.du.myequity.controller;

import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonRequest;
import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonResponse;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.SimpleAssetSnapshot;
import br.net.du.myequity.model.snapshot.SimpleLiabilitySnapshot;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SnapshotSimpleAccountUpdateController extends SnapshotAccountUpdateControllerBase {

    @PostMapping("/updateAccountBalance")
    public SnapshotAccountUpdateJsonResponse updateAccountBalance(final Model model,
                                                                  @RequestBody final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest) {
        final Snapshot snapshot = getSnapshot(model, snapshotAccountUpdateJsonRequest);
        assert snapshot != null;

        final AccountSnapshot accountSnapshot = getAccountSnapshot(snapshotAccountUpdateJsonRequest,
                                                                   SimpleAssetSnapshot.class,
                                                                   SimpleLiabilitySnapshot.class);

        if (accountSnapshot instanceof SimpleAssetSnapshot) {
            ((SimpleAssetSnapshot) accountSnapshot).setAmount(snapshotAccountUpdateJsonRequest.getNewValue());
        } else {
            ((SimpleLiabilitySnapshot) accountSnapshot).setAmount(snapshotAccountUpdateJsonRequest.getNewValue());
        }

        accountSnapshotRepository.save(accountSnapshot);

        return getDefaultResponseBuilder(snapshot, accountSnapshot).build();
    }
}
