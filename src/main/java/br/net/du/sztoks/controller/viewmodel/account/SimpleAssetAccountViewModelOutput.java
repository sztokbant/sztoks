package br.net.du.sztoks.controller.viewmodel.account;

import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.account.FutureTithingCapable;
import lombok.Getter;

@Getter
public class SimpleAssetAccountViewModelOutput extends AccountViewModelOutput {
    private final String futureTithingPolicy;

    public SimpleAssetAccountViewModelOutput(
            final AccountViewModelOutput other, final String futureTithingPolicy) {
        super(other);
        this.futureTithingPolicy = futureTithingPolicy;
    }

    public static SimpleAssetAccountViewModelOutput of(
            final Account account, final boolean includeTotals) {
        final String futureTithingPolicy =
                ((FutureTithingCapable) account).getFutureTithingPolicy().name();

        return new SimpleAssetAccountViewModelOutput(
                AccountViewModelOutput.of(account, includeTotals), futureTithingPolicy);
    }

    public static SimpleAssetAccountViewModelOutput of(final Account account) {
        return of(account, false);
    }
}
