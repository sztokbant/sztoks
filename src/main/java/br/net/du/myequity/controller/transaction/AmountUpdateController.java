package br.net.du.myequity.controller.transaction;

import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.transaction.TransactionViewModelOutput;
import br.net.du.myequity.model.transaction.Transaction;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AmountUpdateController {

    @Autowired private TransactionUpdater transactionUpdater;

    @PostMapping("/transaction/updateAmount")
    public TransactionViewModelOutput post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Transaction, TransactionViewModelOutput>
                updateAmountFunction =
                        (jsonRequest, transaction) -> {
                            final BigDecimal newAmount = new BigDecimal(jsonRequest.getNewValue());

                            transaction.setAmount(newAmount);

                            return TransactionViewModelOutput.of(transaction, true);
                        };

        return transactionUpdater.updateField(
                model, valueUpdateJsonRequest, Transaction.class, updateAmountFunction, true);
    }
}
