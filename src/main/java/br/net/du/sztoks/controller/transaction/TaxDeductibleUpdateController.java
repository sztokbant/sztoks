package br.net.du.sztoks.controller.transaction;

import br.net.du.sztoks.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.sztoks.controller.viewmodel.transaction.TransactionViewModelOutput;
import br.net.du.sztoks.model.transaction.DonationTransaction;
import br.net.du.sztoks.model.transaction.Transaction;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaxDeductibleUpdateController {

    @Autowired private TransactionUpdater transactionUpdater;

    @PostMapping("/transaction/setTaxDeductible")
    public Object post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Transaction, Object> updateTaxDeductibleFunction =
                (jsonRequest, transaction) -> {
                    ((DonationTransaction) transaction)
                            .setTaxDeductible(Boolean.valueOf(jsonRequest.getNewValue()));

                    return TransactionViewModelOutput.of(transaction, true);
                };

        return transactionUpdater.updateField(
                model,
                valueUpdateJsonRequest,
                DonationTransaction.class,
                updateTaxDeductibleFunction,
                true);
    }
}
