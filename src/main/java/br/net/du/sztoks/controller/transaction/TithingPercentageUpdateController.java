package br.net.du.sztoks.controller.transaction;

import br.net.du.sztoks.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.sztoks.controller.viewmodel.transaction.TransactionViewModelOutput;
import br.net.du.sztoks.model.transaction.IncomeTransaction;
import br.net.du.sztoks.model.transaction.Transaction;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TithingPercentageUpdateController {

    @Autowired private TransactionUpdater transactionUpdater;

    @PostMapping("/transaction/updateTithingPercentage")
    public Object post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Transaction, Object> updateAmountFunction =
                (jsonRequest, transaction) -> {
                    if (!(transaction instanceof IncomeTransaction)) {
                        throw new IllegalArgumentException(
                                transaction.getClass().getSimpleName()
                                        + " does not have attribute tithingPercentage");
                    }

                    final BigDecimal newTithingPercentage =
                            new BigDecimal(jsonRequest.getNewValue());

                    ((IncomeTransaction) transaction).setTithingPercentage(newTithingPercentage);

                    return TransactionViewModelOutput.of(transaction, true);
                };

        return transactionUpdater.updateField(
                model, valueUpdateJsonRequest, Transaction.class, updateAmountFunction, true);
    }
}
