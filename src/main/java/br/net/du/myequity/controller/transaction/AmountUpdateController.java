package br.net.du.myequity.controller.transaction;

import static br.net.du.myequity.controller.transaction.TransactionUpdateUtil.updateTithingAmount;

import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.transaction.TransactionViewModelOutput;
import br.net.du.myequity.model.transaction.DonationTransaction;
import br.net.du.myequity.model.transaction.IncomeTransaction;
import br.net.du.myequity.model.transaction.Transaction;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AmountUpdateController extends TransactionUpdateControllerBase {

    @PostMapping("/transaction/updateAmount")
    public TransactionViewModelOutput post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Transaction, TransactionViewModelOutput>
                updateAmountFunction =
                        (jsonRequest, transaction) -> {
                            final BigDecimal newAmount = new BigDecimal(jsonRequest.getNewValue());

                            if (transaction instanceof IncomeTransaction) {
                                final IncomeTransaction incomeTransaction =
                                        (IncomeTransaction) transaction;
                                final BigDecimal oldTithingAmount =
                                        incomeTransaction.getTithingAmount();

                                incomeTransaction.setAmount(newAmount);
                                final BigDecimal newTithingAmount =
                                        incomeTransaction.getTithingAmount();

                                final BigDecimal diffTithingAmount =
                                        newTithingAmount.subtract(oldTithingAmount);

                                updateTithingAmount(incomeTransaction, diffTithingAmount);
                            } else if (transaction instanceof DonationTransaction) {
                                final BigDecimal oldAmount = transaction.getAmount();

                                transaction.setAmount(newAmount);

                                final BigDecimal diffTithingAmount = oldAmount.subtract(newAmount);
                                updateTithingAmount(transaction, diffTithingAmount);
                            } else {
                                transaction.setAmount(newAmount);
                            }

                            return TransactionViewModelOutput.of(transaction, true);
                        };

        return updateTransactionField(
                model, valueUpdateJsonRequest, Transaction.class, updateAmountFunction);
    }
}
