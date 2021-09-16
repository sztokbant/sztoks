package br.net.du.myequity.controller.viewmodel.account;

import lombok.NonNull;

public class SharedBillReceivableAccountViewModelOutput extends SharedBillAccountViewModelOutput {
    public SharedBillReceivableAccountViewModelOutput(
            @NonNull final AccountViewModelOutput other,
            @NonNull final String billAmount,
            final boolean isPaid,
            final int numberOfPartners,
            final int dueDay,
            final String futureTithingPolicy) {
        super(other, billAmount, isPaid, numberOfPartners, dueDay, futureTithingPolicy);
    }
}
