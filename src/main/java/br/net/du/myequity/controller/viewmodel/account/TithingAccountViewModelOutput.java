package br.net.du.myequity.controller.viewmodel.account;

import br.net.du.myequity.model.account.Account;

public class TithingAccountViewModelOutput extends AccountViewModelOutput {
    public TithingAccountViewModelOutput(final AccountViewModelOutput other) {
        super(other);
    }

    public static TithingAccountViewModelOutput of(
            final Account account, final boolean includeTotals) {
        return new TithingAccountViewModelOutput(AccountViewModelOutput.of(account, includeTotals));
    }

    public static TithingAccountViewModelOutput of(final Account account) {
        return of(account, false);
    }
}
