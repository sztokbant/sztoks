package br.net.du.myequity.controller.transaction;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.transaction.Transaction;
import java.math.BigDecimal;

public class TransactionUpdateUtil {
    public static void updateTithingAmount(
            final Transaction transaction, final BigDecimal diffTithingAmount) {
        if (diffTithingAmount.compareTo(BigDecimal.ZERO) != 0) {
            final Snapshot snapshot = transaction.getSnapshot();
            snapshot.updateTithingAmount(transaction.getCurrencyUnit(), diffTithingAmount);
        }
    }
}
