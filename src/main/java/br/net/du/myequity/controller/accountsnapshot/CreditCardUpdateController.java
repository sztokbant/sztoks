package br.net.du.myequity.controller.accountsnapshot;

import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.AccountSnapshotViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.CreditCardViewModelOutput;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreditCardUpdateController extends UpdateControllerBase {
    @PostMapping("/snapshot/updateCreditCardTotalCredit")
    public AccountSnapshotViewModelOutput updateCreditCardTotalCredit(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, AccountSnapshot, AccountSnapshotViewModelOutput>
                updateCreditCardTotalCreditFunction =
                        (jsonRequest, accountSnapshot) -> {
                            final CreditCardSnapshot creditCardSnapshot =
                                    (CreditCardSnapshot) accountSnapshot;

                            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                            creditCardSnapshot.setTotalCredit(newValue);

                            return CreditCardViewModelOutput.of(creditCardSnapshot, true);
                        };

        return updateAccountSnapshotField(
                model,
                valueUpdateJsonRequest,
                CreditCardSnapshot.class,
                updateCreditCardTotalCreditFunction);
    }

    @PostMapping("/snapshot/updateCreditCardAvailableCredit")
    public AccountSnapshotViewModelOutput updateCreditCardAvailableCredit(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {
        final BiFunction<ValueUpdateJsonRequest, AccountSnapshot, AccountSnapshotViewModelOutput>
                updateCreditCardAvailableCreditFunction =
                        (jsonRequest, accountSnapshot) -> {
                            final CreditCardSnapshot creditCardSnapshot =
                                    (CreditCardSnapshot) accountSnapshot;

                            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                            creditCardSnapshot.setAvailableCredit(newValue);

                            return CreditCardViewModelOutput.of(creditCardSnapshot, true);
                        };

        return updateAccountSnapshotField(
                model,
                valueUpdateJsonRequest,
                CreditCardSnapshot.class,
                updateCreditCardAvailableCreditFunction);
    }

    @PostMapping("/snapshot/updateCreditCardStatement")
    public AccountSnapshotViewModelOutput updateCreditCardStatement(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {
        final BiFunction<ValueUpdateJsonRequest, AccountSnapshot, AccountSnapshotViewModelOutput>
                updateCreditCardStatementFunction =
                        (jsonRequest, accountSnapshot) -> {
                            final CreditCardSnapshot creditCardSnapshot =
                                    (CreditCardSnapshot) accountSnapshot;

                            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                            creditCardSnapshot.setStatement(newValue);

                            return CreditCardViewModelOutput.of(creditCardSnapshot, true);
                        };

        return updateAccountSnapshotField(
                model,
                valueUpdateJsonRequest,
                CreditCardSnapshot.class,
                updateCreditCardStatementFunction);
    }
}
