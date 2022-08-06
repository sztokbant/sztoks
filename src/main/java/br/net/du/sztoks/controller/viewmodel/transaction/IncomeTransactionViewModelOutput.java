package br.net.du.sztoks.controller.viewmodel.transaction;

import static br.net.du.sztoks.controller.util.ControllerUtils.formatAsPercentage;

import br.net.du.sztoks.model.transaction.IncomeTransaction;
import br.net.du.sztoks.model.transaction.Transaction;
import lombok.Getter;

public class IncomeTransactionViewModelOutput extends TransactionViewModelOutput {
    @Getter private final String tithingPercentage;

    public IncomeTransactionViewModelOutput(
            final TransactionViewModelOutput other, final String tithingPercentage) {
        super(other);
        this.tithingPercentage = tithingPercentage;
    }

    public static IncomeTransactionViewModelOutput of(
            final Transaction transaction, final boolean includeTotals) {
        final IncomeTransaction income = (IncomeTransaction) transaction;

        return new IncomeTransactionViewModelOutput(
                TransactionViewModelOutput.of(transaction, includeTotals),
                formatAsPercentage(income.getTithingPercentage()));
    }

    public static IncomeTransactionViewModelOutput of(final Transaction transaction) {
        return of(transaction, false);
    }
}
