package br.net.du.sztoks.controller.viewmodel.transaction;

import br.net.du.sztoks.model.transaction.DonationTransaction;
import br.net.du.sztoks.model.transaction.Transaction;
import lombok.Getter;

public class DonationTransactionViewModelOutput extends TransactionViewModelOutput {
    @Getter private final boolean isTaxDeductible;

    public DonationTransactionViewModelOutput(
            final TransactionViewModelOutput other, final boolean isTaxDeductible) {
        super(other);
        this.isTaxDeductible = isTaxDeductible;
    }

    public static DonationTransactionViewModelOutput of(
            final Transaction transaction, final boolean includeTotals) {
        final DonationTransaction donation = (DonationTransaction) transaction;

        return new DonationTransactionViewModelOutput(
                TransactionViewModelOutput.of(transaction, includeTotals),
                donation.isTaxDeductible());
    }

    public static DonationTransactionViewModelOutput of(final Transaction transaction) {
        return of(transaction, false);
    }
}
