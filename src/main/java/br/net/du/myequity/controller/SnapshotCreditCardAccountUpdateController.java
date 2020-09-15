package br.net.du.myequity.controller;

import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonRequest;
import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonResponse;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;
import static br.net.du.myequity.controller.util.ControllerUtils.formatAsPercentage;

@RestController
public class SnapshotCreditCardAccountUpdateController extends SnapshotAccountUpdateControllerBase {
    @PostMapping("/snapshot/updateCreditCardTotalCredit")
    public SnapshotAccountUpdateJsonResponse updateCreditCardTotalCredit(final Model model,
                                                                         @RequestBody final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest) {
        final Snapshot snapshot = getSnapshot(model, snapshotAccountUpdateJsonRequest);
        assert snapshot != null;

        final CreditCardSnapshot creditCardSnapshot =
                (CreditCardSnapshot) getAccountSnapshot(snapshotAccountUpdateJsonRequest, CreditCardSnapshot.class);

        creditCardSnapshot.setTotalCredit(snapshotAccountUpdateJsonRequest.getNewValue());

        accountSnapshotRepository.save(creditCardSnapshot);

        return getDefaultResponseBuilder(snapshot,
                                         creditCardSnapshot).totalCredit(formatAsDecimal(creditCardSnapshot.getTotalCredit()))
                                                            .usedCreditPercentage(formatAsPercentage(creditCardSnapshot.getUsedCreditPercentage()))
                                                            .build();
    }

    @PostMapping("/snapshot/updateCreditCardAvailableCredit")
    public SnapshotAccountUpdateJsonResponse updateCreditCardAvailableCredit(final Model model,
                                                                             @RequestBody final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest) {
        final Snapshot snapshot = getSnapshot(model, snapshotAccountUpdateJsonRequest);
        assert snapshot != null;

        final CreditCardSnapshot creditCardSnapshot =
                (CreditCardSnapshot) getAccountSnapshot(snapshotAccountUpdateJsonRequest, CreditCardSnapshot.class);

        creditCardSnapshot.setAvailableCredit(snapshotAccountUpdateJsonRequest.getNewValue());

        accountSnapshotRepository.save(creditCardSnapshot);

        return getDefaultResponseBuilder(snapshot, creditCardSnapshot).availableCredit(formatAsDecimal(
                creditCardSnapshot.getAvailableCredit()))
                                                                      .usedCreditPercentage(formatAsPercentage(
                                                                              creditCardSnapshot.getUsedCreditPercentage()))
                                                                      .build();
    }
}
