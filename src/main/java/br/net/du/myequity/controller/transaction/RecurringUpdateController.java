package br.net.du.myequity.controller.transaction;

import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.transaction.TransactionViewModelOutput;
import br.net.du.myequity.model.transaction.Transaction;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecurringUpdateController {

    @Autowired private TransactionUpdater transactionUpdater;

    @PostMapping("/transaction/setRecurring")
    public TransactionViewModelOutput post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Transaction, TransactionViewModelOutput>
                updateRecurringFunction =
                        (jsonRequest, transaction) -> {
                            final boolean newValue = Boolean.valueOf(jsonRequest.getNewValue());

                            transaction.setRecurring(newValue);

                            return TransactionViewModelOutput.of(transaction, false);
                        };

        return transactionUpdater.updateField(
                model, valueUpdateJsonRequest, Transaction.class, updateRecurringFunction, false);
    }
}
