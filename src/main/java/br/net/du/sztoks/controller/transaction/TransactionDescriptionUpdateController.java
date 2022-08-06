package br.net.du.sztoks.controller.transaction;

import br.net.du.sztoks.controller.viewmodel.EntityUpdateDescriptionJsonResponse;
import br.net.du.sztoks.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.sztoks.model.transaction.Transaction;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionDescriptionUpdateController {

    @Autowired private TransactionUpdater transactionUpdater;

    @PostMapping("/transaction/updateDescription")
    @Transactional
    public Object post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Transaction, Object> updateTransactionFunction =
                (jsonRequest, transaction) -> {
                    if (StringUtils.isEmpty(jsonRequest.getNewValue())) {
                        throw new IllegalArgumentException(
                                "Transaction description can't be empty");
                    }

                    transaction.setDescription(jsonRequest.getNewValue().trim());

                    return EntityUpdateDescriptionJsonResponse.builder()
                            .description(transaction.getDescription())
                            .build();
                };

        return transactionUpdater.updateField(
                model, valueUpdateJsonRequest, Transaction.class, updateTransactionFunction, false);
    }
}
