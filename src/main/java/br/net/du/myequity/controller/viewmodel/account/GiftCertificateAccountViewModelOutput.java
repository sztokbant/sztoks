package br.net.du.myequity.controller.viewmodel.account;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;
import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.GiftCertificateAccount;
import java.math.BigDecimal;
import lombok.Getter;
import org.joda.money.CurrencyUnit;

@Getter
public class GiftCertificateAccountViewModelOutput extends AccountViewModelOutput {
    private final String shares;
    private final String currentShareValue;

    public GiftCertificateAccountViewModelOutput(
            final AccountViewModelOutput other,
            final String shares,
            final String currentShareValue) {
        super(other);
        this.shares = shares;
        this.currentShareValue = currentShareValue;
    }

    public static GiftCertificateAccountViewModelOutput of(
            final Account account, final boolean includeTotals) {
        final GiftCertificateAccount giftCertificateAccount = (GiftCertificateAccount) account;

        final String shares =
                new BigDecimal(formatAsDecimal(giftCertificateAccount.getShares())).toString();

        final CurrencyUnit currencyUnit = account.getCurrencyUnit();

        final String currentShareValue =
                format(currencyUnit, toDecimal((giftCertificateAccount.getCurrentShareValue())));

        return new GiftCertificateAccountViewModelOutput(
                AccountViewModelOutput.of(account, includeTotals), shares, currentShareValue);
    }

    public static GiftCertificateAccountViewModelOutput of(final Account account) {
        return of(account, false);
    }
}
