package br.net.du.myequity.controller.transaction;

import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.transaction.TransactionViewModelOutput;
import br.net.du.myequity.model.transaction.DonationTransaction;
import br.net.du.myequity.model.transaction.Transaction;
import java.util.function.BiFunction;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaxDeductibleUpdateController extends TransactionUpdateControllerBase {

    @PostMapping("/transaction/setTaxDeductible")
    @Transactional
    public TransactionViewModelOutput post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Transaction, TransactionViewModelOutput>
                updateTaxDeductibleFunction =
                        (jsonRequest, transaction) -> {
                            final DonationTransaction donationTransaction =
                                    (DonationTransaction) transaction;

                            final boolean newValue = Boolean.valueOf(jsonRequest.getNewValue());
                            donationTransaction.setTaxDeductible(newValue);

                            return TransactionViewModelOutput.of(transaction, true);
                        };

        return updateTransactionField(
                model,
                valueUpdateJsonRequest,
                DonationTransaction.class,
                updateTaxDeductibleFunction);
    }
}
