package br.net.du.myequity.controller;

import br.net.du.myequity.controller.model.AccountSnapshotUpdateJsonResponse;
import br.net.du.myequity.controller.model.CreditCardSnapshotUpdateJsonResponse;
import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonRequest;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.function.BiFunction;

@RestController
public class SnapshotCreditCardAccountUpdateController extends SnapshotAccountUpdateControllerBase {
    @PostMapping("/snapshot/updateCreditCardTotalCredit")
    public AccountSnapshotUpdateJsonResponse updateCreditCardTotalCredit(final Model model,
                                                                         @RequestBody final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest) {

        final BiFunction<SnapshotAccountUpdateJsonRequest, AccountSnapshot, AccountSnapshotUpdateJsonResponse>
                updateCreditCardTotalCreditFunction = (jsonRequest, accountSnapshot) -> {
            final CreditCardSnapshot creditCardSnapshot = (CreditCardSnapshot) accountSnapshot;

            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
            creditCardSnapshot.setTotalCredit(newValue);

            return CreditCardSnapshotUpdateJsonResponse.of(creditCardSnapshot);
        };

        return updateAccountSnapshotField(model,
                                          snapshotAccountUpdateJsonRequest,
                                          CreditCardSnapshot.class,
                                          updateCreditCardTotalCreditFunction);
    }

    @PostMapping("/snapshot/updateCreditCardAvailableCredit")
    public AccountSnapshotUpdateJsonResponse updateCreditCardAvailableCredit(final Model model,
                                                                             @RequestBody final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest) {
        final BiFunction<SnapshotAccountUpdateJsonRequest, AccountSnapshot, AccountSnapshotUpdateJsonResponse>
                updateCreditCardAvailableCreditFunction = (jsonRequest, accountSnapshot) -> {
            final CreditCardSnapshot creditCardSnapshot = (CreditCardSnapshot) accountSnapshot;

            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
            creditCardSnapshot.setAvailableCredit(newValue);

            return CreditCardSnapshotUpdateJsonResponse.of(creditCardSnapshot);
        };

        return updateAccountSnapshotField(model,
                                          snapshotAccountUpdateJsonRequest,
                                          CreditCardSnapshot.class,
                                          updateCreditCardAvailableCreditFunction);
    }
}
