package br.net.du.myequity.controller.transaction;

import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.transaction.TransactionViewModelOutput;
import br.net.du.myequity.model.transaction.Transaction;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AmountUpdateController extends TransactionUpdateControllerBase {

    @PostMapping("/snapshot/updateTransactionAmount")
    public TransactionViewModelOutput post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Transaction, TransactionViewModelOutput>
                updateAmountFunction =
                        (jsonRequest, transaction) -> {
                            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                            transaction.setAmount(newValue);

                            return TransactionViewModelOutput.of(transaction, true);
                        };

        return updateTransactionField(
                model, valueUpdateJsonRequest, Transaction.class, updateAmountFunction);
    }
}
