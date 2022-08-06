package br.net.du.sztoks.controller.viewmodel.account;

import br.net.du.sztoks.model.account.Account;

public class FutureTithingAccountViewModelOutput extends AccountViewModelOutput {
    public FutureTithingAccountViewModelOutput(final AccountViewModelOutput other) {
        super(other);
    }

    public static FutureTithingAccountViewModelOutput of(
            final Account account, final boolean includeTotals) {
        return new FutureTithingAccountViewModelOutput(
                AccountViewModelOutput.of(account, includeTotals));
    }

    public static FutureTithingAccountViewModelOutput of(final Account account) {
        return of(account, false);
    }
}
