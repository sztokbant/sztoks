package br.net.du.sztoks.controller.transaction;

import br.net.du.sztoks.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.sztoks.controller.viewmodel.transaction.TransactionViewModelOutput;
import br.net.du.sztoks.model.transaction.Categorizable;
import br.net.du.sztoks.model.transaction.DonationCategory;
import br.net.du.sztoks.model.transaction.DonationTransaction;
import br.net.du.sztoks.model.transaction.IncomeCategory;
import br.net.du.sztoks.model.transaction.IncomeTransaction;
import br.net.du.sztoks.model.transaction.InvestmentCategory;
import br.net.du.sztoks.model.transaction.InvestmentTransaction;
import br.net.du.sztoks.model.transaction.Transaction;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryUpdateController {

    @Autowired private TransactionUpdater transactionUpdater;

    @PostMapping("/transaction/updateCategory")
    public Object post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Transaction, Object> updateCategoryFunction =
                (jsonRequest, transaction) -> {
                    if (transaction instanceof IncomeTransaction) {
                        ((IncomeTransaction) transaction)
                                .setCategory(IncomeCategory.valueOf(jsonRequest.getNewValue()));
                    } else if (transaction instanceof InvestmentTransaction) {
                        ((InvestmentTransaction) transaction)
                                .setCategory(InvestmentCategory.valueOf(jsonRequest.getNewValue()));
                    } else if (transaction instanceof DonationTransaction) {
                        ((DonationTransaction) transaction)
                                .setCategory(DonationCategory.valueOf(jsonRequest.getNewValue()));
                    }

                    return TransactionViewModelOutput.of(transaction, false);
                };

        return transactionUpdater.updateField(
                model, valueUpdateJsonRequest, Categorizable.class, updateCategoryFunction, false);
    }
}
