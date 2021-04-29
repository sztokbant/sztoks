package br.net.du.myequity.controller.transaction;

import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.transaction.TransactionViewModelOutput;
import br.net.du.myequity.model.transaction.RecurrencePolicy;
import br.net.du.myequity.model.transaction.Transaction;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecurrenceUpdateController {

    @Autowired private TransactionUpdater transactionUpdater;

    @PostMapping("/transaction/updateRecurrencePolicy")
    public TransactionViewModelOutput post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Transaction, TransactionViewModelOutput>
                updateRecurrencePolicyFunction =
                        (jsonRequest, transaction) -> {
                            final RecurrencePolicy newValue =
                                    RecurrencePolicy.forShortValue(jsonRequest.getNewValue());

                            transaction.setRecurrencePolicy(newValue);

                            return TransactionViewModelOutput.of(transaction, false);
                        };

        return transactionUpdater.updateField(
                model,
                valueUpdateJsonRequest,
                Transaction.class,
                updateRecurrencePolicyFunction,
                false);
    }
}
