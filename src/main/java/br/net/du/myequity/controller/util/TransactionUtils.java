package br.net.du.myequity.controller.util;

import br.net.du.myequity.model.transaction.Transaction;
import br.net.du.myequity.model.transaction.TransactionType;
import br.net.du.myequity.service.AccountService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionUtils {
    @Autowired private AccountService accountService;

    public static boolean hasTithingImpact(@NonNull final Transaction transaction) {
        return transaction.getTransactionType().equals(TransactionType.INCOME)
                || transaction.getTransactionType().equals(TransactionType.DONATION);
    }
}
