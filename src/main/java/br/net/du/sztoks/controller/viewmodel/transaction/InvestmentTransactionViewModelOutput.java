package br.net.du.sztoks.controller.viewmodel.transaction;

import br.net.du.sztoks.model.transaction.Transaction;

public class InvestmentTransactionViewModelOutput extends TransactionViewModelOutput {
    public InvestmentTransactionViewModelOutput(final TransactionViewModelOutput other) {
        super(other);
    }

    public static InvestmentTransactionViewModelOutput of(
            final Transaction transaction, final boolean includeTotals) {
        return new InvestmentTransactionViewModelOutput(
                TransactionViewModelOutput.of(transaction, includeTotals));
    }

    public static InvestmentTransactionViewModelOutput of(final Transaction transaction) {
        return of(transaction, false);
    }
}
