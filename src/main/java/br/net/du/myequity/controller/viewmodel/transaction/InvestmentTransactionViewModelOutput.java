package br.net.du.myequity.controller.viewmodel.transaction;

import br.net.du.myequity.model.transaction.InvestmentTransaction;
import br.net.du.myequity.model.transaction.Transaction;
import lombok.Getter;

public class InvestmentTransactionViewModelOutput extends TransactionViewModelOutput {
    @Getter private final String category;

    public InvestmentTransactionViewModelOutput(
            final TransactionViewModelOutput other, final String category) {
        super(other);
        this.category = category;
    }

    public static InvestmentTransactionViewModelOutput of(
            final Transaction transaction, final boolean includeTotals) {
        final InvestmentTransaction investment = (InvestmentTransaction) transaction;

        return new InvestmentTransactionViewModelOutput(
                TransactionViewModelOutput.of(transaction, includeTotals),
                investment.getCategory().name());
    }

    public static InvestmentTransactionViewModelOutput of(final Transaction transaction) {
        return of(transaction, false);
    }
}
