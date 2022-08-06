package br.net.du.sztoks.controller.viewmodel.account;

import static br.net.du.sztoks.controller.util.ControllerUtils.formatAsDecimal;
import static br.net.du.sztoks.controller.util.ControllerUtils.toDecimal;
import static br.net.du.sztoks.controller.util.MoneyFormatUtils.format;

import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.account.FutureTithingCapable;
import br.net.du.sztoks.model.account.GiftCertificateAccount;
import java.math.BigDecimal;
import lombok.Getter;
import org.joda.money.CurrencyUnit;

@Getter
public class GiftCertificateAccountViewModelOutput extends AccountViewModelOutput {
    private final String shares;
    private final String currentShareValue;
    private final String futureTithingPolicy;

    public GiftCertificateAccountViewModelOutput(
            final AccountViewModelOutput other,
            final String shares,
            final String currentShareValue,
            final String futureTithingPolicy) {
        super(other);
        this.shares = shares;
        this.currentShareValue = currentShareValue;
        this.futureTithingPolicy = futureTithingPolicy;
    }

    public static GiftCertificateAccountViewModelOutput of(
            final Account account, final boolean includeTotals) {
        final GiftCertificateAccount giftCertificateAccount = (GiftCertificateAccount) account;

        final String shares =
                new BigDecimal(formatAsDecimal(giftCertificateAccount.getShares())).toString();

        final CurrencyUnit currencyUnit = account.getCurrencyUnit();

        final String currentShareValue =
                format(currencyUnit, toDecimal((giftCertificateAccount.getCurrentShareValue())));

        final String futureTithingPolicy =
                ((FutureTithingCapable) account).getFutureTithingPolicy().name();

        return new GiftCertificateAccountViewModelOutput(
                AccountViewModelOutput.of(account, includeTotals),
                shares,
                currentShareValue,
                futureTithingPolicy);
    }

    public static GiftCertificateAccountViewModelOutput of(final Account account) {
        return of(account, false);
    }
}
