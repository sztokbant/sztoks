package br.net.du.sztoks.controller.transaction;

import br.net.du.sztoks.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.sztoks.controller.viewmodel.transaction.TransactionViewModelOutput;
import br.net.du.sztoks.model.transaction.RecurrencePolicy;
import br.net.du.sztoks.model.transaction.Transaction;
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
    public Object post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Transaction, Object>
                updateRecurrencePolicyFunction =
                        (jsonRequest, transaction) -> {
                            transaction.setRecurrencePolicy(
                                    RecurrencePolicy.forValue(jsonRequest.getNewValue()));

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
