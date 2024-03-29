package br.net.du.sztoks.controller.viewmodel.account;

import lombok.NonNull;

public class SharedBillPayableAccountViewModelOutput extends SharedBillAccountViewModelOutput {
    public SharedBillPayableAccountViewModelOutput(
            @NonNull final AccountViewModelOutput other,
            @NonNull final String billAmount,
            final boolean isPaid,
            final int numberOfPartners,
            final int dueDay) {
        super(other, billAmount, isPaid, numberOfPartners, dueDay, null);
    }
}
