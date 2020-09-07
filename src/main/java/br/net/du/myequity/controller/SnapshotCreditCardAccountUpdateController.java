package br.net.du.myequity.controller;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static br.net.du.myequity.controller.util.ControllerConstants.DECIMAL_FORMAT;

@RestController
public class SnapshotCreditCardAccountUpdateController extends SnapshotAccountUpdateControllerBase {
    @PostMapping("/updateCreditCardTotalCredit")
    public SnapshotAccountUpdateResponse updateCreditCardTotalCredit(@RequestBody final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest) {
        final Snapshot snapshot;
        try {
            snapshot = loadSnapshot(snapshotAccountUpdateJsonRequest);
        } catch (final IllegalAccessException e) {
            return SnapshotAccountUpdateResponse.builder().hasError(true).build();
        }

        final AccountSnapshot accountSnapshot =
                accountSnapshotRepository.findByAccountId(snapshotAccountUpdateJsonRequest.getAccountId()).get();

        if (!(accountSnapshot instanceof CreditCardSnapshot)) {
            assert false : "accountSnapshot not an instance of " + CreditCardSnapshot.class.getSimpleName();
        }

        final CreditCardSnapshot creditCardSnapshot = (CreditCardSnapshot) accountSnapshot;

        creditCardSnapshot.setTotalCredit(snapshotAccountUpdateJsonRequest.getNewValue());

        accountSnapshotRepository.save(accountSnapshot);

        return getDefaultResponseBuilder(snapshot, accountSnapshot).totalCredit(DECIMAL_FORMAT.format(creditCardSnapshot
                                                                                                              .getTotalCredit()
                                                                                                              .setScale(
                                                                                                                      2)))
                                                                   .build();
    }

    @PostMapping("/updateCreditCardAvailableCredit")
    public SnapshotAccountUpdateResponse updateCreditCardAvailableCredit(@RequestBody final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest) {
        final Snapshot snapshot;
        try {
            snapshot = loadSnapshot(snapshotAccountUpdateJsonRequest);
        } catch (final IllegalAccessException e) {
            return SnapshotAccountUpdateResponse.builder().hasError(true).build();
        }

        final AccountSnapshot accountSnapshot =
                accountSnapshotRepository.findByAccountId(snapshotAccountUpdateJsonRequest.getAccountId()).get();

        if (!(accountSnapshot instanceof CreditCardSnapshot)) {
            assert false : "accountSnapshot not an instance of " + CreditCardSnapshot.class.getSimpleName();
        }

        final CreditCardSnapshot creditCardSnapshot = (CreditCardSnapshot) accountSnapshot;

        creditCardSnapshot.setAvailableCredit(snapshotAccountUpdateJsonRequest.getNewValue());

        accountSnapshotRepository.save(accountSnapshot);

        return getDefaultResponseBuilder(snapshot, accountSnapshot).availableCredit(DECIMAL_FORMAT.format(
                creditCardSnapshot.getAvailableCredit().setScale(2))).build();
    }
}
