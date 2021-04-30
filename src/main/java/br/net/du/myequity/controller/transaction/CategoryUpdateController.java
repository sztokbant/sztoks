package br.net.du.myequity.controller.transaction;

import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.transaction.TransactionViewModelOutput;
import br.net.du.myequity.model.transaction.Categorizable;
import br.net.du.myequity.model.transaction.DonationCategory;
import br.net.du.myequity.model.transaction.DonationTransaction;
import br.net.du.myequity.model.transaction.IncomeCategory;
import br.net.du.myequity.model.transaction.IncomeTransaction;
import br.net.du.myequity.model.transaction.InvestmentCategory;
import br.net.du.myequity.model.transaction.InvestmentTransaction;
import br.net.du.myequity.model.transaction.Transaction;
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
    public TransactionViewModelOutput post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Transaction, TransactionViewModelOutput>
                updateCategoryFunction =
                        (jsonRequest, transaction) -> {
                            if (transaction instanceof IncomeTransaction) {
                                final IncomeCategory category =
                                        IncomeCategory.valueOf(jsonRequest.getNewValue());
                                ((IncomeTransaction) transaction).setCategory(category);

                            } else if (transaction instanceof InvestmentTransaction) {
                                final InvestmentCategory category =
                                        InvestmentCategory.valueOf(jsonRequest.getNewValue());
                                ((InvestmentTransaction) transaction).setCategory(category);

                            } else if (transaction instanceof DonationTransaction) {
                                final DonationCategory category =
                                        DonationCategory.valueOf(jsonRequest.getNewValue());
                                ((DonationTransaction) transaction).setCategory(category);
                            }

                            return TransactionViewModelOutput.of(transaction, false);
                        };

        return transactionUpdater.updateField(
                model, valueUpdateJsonRequest, Categorizable.class, updateCategoryFunction, false);
    }
}
