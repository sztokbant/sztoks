package br.net.du.myequity.controller;

import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonRequest;
import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonResponse;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.function.BiFunction;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;
import static br.net.du.myequity.controller.util.ControllerUtils.formatAsPercentage;

@RestController
public class SnapshotCreditCardAccountUpdateController extends SnapshotAccountUpdateControllerBase {
    @PostMapping("/snapshot/updateCreditCardTotalCredit")
    public SnapshotAccountUpdateJsonResponse updateCreditCardTotalCredit(final Model model,
                                                                         @RequestBody final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest) {

        final BiFunction<SnapshotAccountUpdateJsonRequest, AccountSnapshot, SnapshotAccountUpdateJsonResponse>
                updateCreditCardTotalCreditFunction =
                new BiFunction<SnapshotAccountUpdateJsonRequest, AccountSnapshot, SnapshotAccountUpdateJsonResponse>() {
                    @Override
                    public SnapshotAccountUpdateJsonResponse apply(final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest,
                                                                   final AccountSnapshot accountSnapshot) {
                        final CreditCardSnapshot creditCardSnapshot = (CreditCardSnapshot) accountSnapshot;

                        final BigDecimal newValue = new BigDecimal(snapshotAccountUpdateJsonRequest.getNewValue());
                        creditCardSnapshot.setTotalCredit(newValue);

                        final String totalCredit = formatAsDecimal(creditCardSnapshot.getTotalCredit());
                        final String usedCreditPercentage =
                                formatAsPercentage(creditCardSnapshot.getUsedCreditPercentage());

                        return getDefaultResponseBuilder(creditCardSnapshot).totalCredit(totalCredit)
                                                                            .usedCreditPercentage(usedCreditPercentage)
                                                                            .build();
                    }
                };

        return updateAccountSnapshotField(model,
                                          snapshotAccountUpdateJsonRequest,
                                          CreditCardSnapshot.class,
                                          updateCreditCardTotalCreditFunction);
    }

    @PostMapping("/snapshot/updateCreditCardAvailableCredit")
    public SnapshotAccountUpdateJsonResponse updateCreditCardAvailableCredit(final Model model,
                                                                             @RequestBody final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest) {
        final BiFunction<SnapshotAccountUpdateJsonRequest, AccountSnapshot, SnapshotAccountUpdateJsonResponse>
                updateCreditCardAvailableCreditFunction =
                new BiFunction<SnapshotAccountUpdateJsonRequest, AccountSnapshot, SnapshotAccountUpdateJsonResponse>() {
                    @Override
                    public SnapshotAccountUpdateJsonResponse apply(final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest,
                                                                   final AccountSnapshot accountSnapshot) {
                        final CreditCardSnapshot creditCardSnapshot = (CreditCardSnapshot) accountSnapshot;

                        final BigDecimal newValue = new BigDecimal(snapshotAccountUpdateJsonRequest.getNewValue());
                        creditCardSnapshot.setAvailableCredit(newValue);

                        final String availableCredit = formatAsDecimal(creditCardSnapshot.getAvailableCredit());
                        final String usedCreditPercentage =
                                formatAsPercentage(creditCardSnapshot.getUsedCreditPercentage());

                        return getDefaultResponseBuilder(creditCardSnapshot).availableCredit(availableCredit)
                                                                            .usedCreditPercentage(usedCreditPercentage)
                                                                            .build();
                    }
                };

        return updateAccountSnapshotField(model,
                                          snapshotAccountUpdateJsonRequest,
                                          CreditCardSnapshot.class,
                                          updateCreditCardAvailableCreditFunction);
    }
}
