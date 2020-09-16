package br.net.du.myequity.controller;

import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonRequest;
import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonResponse;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.PayableSnapshot;
import br.net.du.myequity.model.snapshot.ReceivableSnapshot;
import br.net.du.myequity.model.snapshot.SimpleAssetSnapshot;
import br.net.du.myequity.model.snapshot.SimpleLiabilitySnapshot;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class SnapshotAccountBalanceUpdateController extends SnapshotAccountUpdateControllerBase {

    @PostMapping("/snapshot/updateAccountBalance")
    public SnapshotAccountUpdateJsonResponse post(final Model model,
                                                  @RequestBody final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest) {
        final Snapshot snapshot = getSnapshot(model, snapshotAccountUpdateJsonRequest);
        assert snapshot != null;

        final Class[] allowedClasses =
                {SimpleAssetSnapshot.class, SimpleLiabilitySnapshot.class, ReceivableSnapshot.class,
                        PayableSnapshot.class};

        final AccountSnapshot accountSnapshot = getAccountSnapshot(snapshotAccountUpdateJsonRequest, allowedClasses);

        final BigDecimal newValue = new BigDecimal(snapshotAccountUpdateJsonRequest.getNewValue());

        if (accountSnapshot instanceof SimpleAssetSnapshot) {
            ((SimpleAssetSnapshot) accountSnapshot).setAmount(newValue);
        } else if (accountSnapshot instanceof SimpleLiabilitySnapshot) {
            ((SimpleLiabilitySnapshot) accountSnapshot).setAmount(newValue);
        } else if (accountSnapshot instanceof ReceivableSnapshot) {
            ((ReceivableSnapshot) accountSnapshot).setAmount(newValue);
        } else if (accountSnapshot instanceof PayableSnapshot) {
            ((PayableSnapshot) accountSnapshot).setAmount(newValue);
        }

        accountSnapshotRepository.save(accountSnapshot);

        return getDefaultResponseBuilder(snapshot, accountSnapshot).build();
    }
}
