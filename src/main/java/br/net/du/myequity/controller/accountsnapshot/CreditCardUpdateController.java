package br.net.du.myequity.controller.accountsnapshot;

import br.net.du.myequity.controller.model.AccountSnapshotUpdateJsonRequest;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import br.net.du.myequity.viewmodel.accountsnapshot.AccountSnapshotViewModelOutput;
import br.net.du.myequity.viewmodel.accountsnapshot.CreditCardViewModelOutput;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.function.BiFunction;

@RestController
public class CreditCardUpdateController extends UpdateControllerBase {
    @PostMapping("/snapshot/updateCreditCardTotalCredit")
    public AccountSnapshotViewModelOutput updateCreditCardTotalCredit(final Model model,
                                                                      @RequestBody final AccountSnapshotUpdateJsonRequest accountSnapshotUpdateJsonRequest) {

        final BiFunction<AccountSnapshotUpdateJsonRequest, AccountSnapshot, AccountSnapshotViewModelOutput>
                updateCreditCardTotalCreditFunction = (jsonRequest, accountSnapshot) -> {
            final CreditCardSnapshot creditCardSnapshot = (CreditCardSnapshot) accountSnapshot;

            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
            creditCardSnapshot.setTotalCredit(newValue);

            return CreditCardViewModelOutput.of(creditCardSnapshot);
        };

        return updateAccountSnapshotField(model,
                                          accountSnapshotUpdateJsonRequest,
                                          CreditCardSnapshot.class,
                                          updateCreditCardTotalCreditFunction);
    }

    @PostMapping("/snapshot/updateCreditCardAvailableCredit")
    public AccountSnapshotViewModelOutput updateCreditCardAvailableCredit(final Model model,
                                                                          @RequestBody final AccountSnapshotUpdateJsonRequest accountSnapshotUpdateJsonRequest) {
        final BiFunction<AccountSnapshotUpdateJsonRequest, AccountSnapshot, AccountSnapshotViewModelOutput>
                updateCreditCardAvailableCreditFunction = (jsonRequest, accountSnapshot) -> {
            final CreditCardSnapshot creditCardSnapshot = (CreditCardSnapshot) accountSnapshot;

            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
            creditCardSnapshot.setAvailableCredit(newValue);

            return CreditCardViewModelOutput.of(creditCardSnapshot);
        };

        return updateAccountSnapshotField(model,
                                          accountSnapshotUpdateJsonRequest,
                                          CreditCardSnapshot.class,
                                          updateCreditCardAvailableCreditFunction);
    }
}
