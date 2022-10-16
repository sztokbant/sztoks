package br.net.du.sztoks.controller.transaction;

import br.net.du.sztoks.controller.viewmodel.EntityUpdateDateJsonResponse;
import br.net.du.sztoks.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.sztoks.model.transaction.Transaction;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionDateUpdateController {

    @Autowired private TransactionUpdater transactionUpdater;

    @PostMapping("/transaction/updateDate")
    @Transactional
    public Object post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Transaction, Object> updateTransactionFunction =
                (jsonRequest, transaction) -> {
                    transaction.setDate(LocalDate.parse(jsonRequest.getNewValue()));

                    return EntityUpdateDateJsonResponse.builder()
                            .date(transaction.getDate())
                            .build();
                };

        return transactionUpdater.updateField(
                model, valueUpdateJsonRequest, Transaction.class, updateTransactionFunction, false);
    }
}
