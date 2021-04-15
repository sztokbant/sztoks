package br.net.du.myequity.controller.transaction;

import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.transaction.TransactionViewModelOutput;
import br.net.du.myequity.model.transaction.IncomeTransaction;
import br.net.du.myequity.model.transaction.Transaction;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TithingPercentageUpdateController extends TransactionUpdateControllerBase {

    @PostMapping("/transaction/updateTithingPercentage")
    @Transactional
    public TransactionViewModelOutput post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Transaction, TransactionViewModelOutput>
                updateAmountFunction =
                        (jsonRequest, transaction) -> {
                            if (!(transaction instanceof IncomeTransaction)) {
                                throw new IllegalArgumentException(
                                        transaction.getClass().getSimpleName()
                                                + " does not have attribute tithingPercentage");
                            }

                            final BigDecimal newTithingPercentage =
                                    new BigDecimal(jsonRequest.getNewValue());

                            ((IncomeTransaction) transaction)
                                    .setTithingPercentage(newTithingPercentage);

                            return TransactionViewModelOutput.of(transaction, true);
                        };

        return updateTransactionField(
                model, valueUpdateJsonRequest, Transaction.class, updateAmountFunction);
    }
}
