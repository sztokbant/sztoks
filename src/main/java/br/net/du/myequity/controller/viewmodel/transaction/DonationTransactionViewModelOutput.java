package br.net.du.myequity.controller.viewmodel.transaction;

import br.net.du.myequity.model.transaction.DonationTransaction;
import br.net.du.myequity.model.transaction.Transaction;
import lombok.Getter;

public class DonationTransactionViewModelOutput extends TransactionViewModelOutput {
    @Getter private final boolean isTaxDeductible;

    public DonationTransactionViewModelOutput(
            final TransactionViewModelOutput other, final boolean isTaxDeductible) {
        super(other);
        this.isTaxDeductible = isTaxDeductible;
    }

    public static DonationTransactionViewModelOutput of(final Transaction transaction) {
        final DonationTransaction donation = (DonationTransaction) transaction;

        return new DonationTransactionViewModelOutput(
                TransactionViewModelOutput.of(transaction), donation.isTaxDeductible());
    }
}
