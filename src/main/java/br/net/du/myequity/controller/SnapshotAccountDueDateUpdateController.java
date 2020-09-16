package br.net.du.myequity.controller;

import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonRequest;
import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonResponse;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.PayableSnapshot;
import br.net.du.myequity.model.snapshot.ReceivableSnapshot;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class SnapshotAccountDueDateUpdateController extends SnapshotAccountUpdateControllerBase {

    @PostMapping("/snapshot/updateAccountDueDate")
    public SnapshotAccountUpdateJsonResponse post(final Model model,
                                                  @RequestBody final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest) {
        final Snapshot snapshot = getSnapshot(model, snapshotAccountUpdateJsonRequest);
        assert snapshot != null;

        final Class[] allowedClasses = {ReceivableSnapshot.class, PayableSnapshot.class};

        final AccountSnapshot accountSnapshot = getAccountSnapshot(snapshotAccountUpdateJsonRequest, allowedClasses);

        final LocalDate dueDate = LocalDate.parse(snapshotAccountUpdateJsonRequest.getNewValue());

        if (accountSnapshot instanceof ReceivableSnapshot) {
            ((ReceivableSnapshot) accountSnapshot).setDueDate(dueDate);
        } else if (accountSnapshot instanceof PayableSnapshot) {
            ((PayableSnapshot) accountSnapshot).setDueDate(dueDate);
        }

        accountSnapshotRepository.save(accountSnapshot);

        return getDefaultResponseBuilder(snapshot, accountSnapshot).dueDate(dueDate.toString()).build();
    }
}
