package br.net.du.sztoks.controller.viewmodel.account;

import static br.net.du.sztoks.controller.util.ControllerUtils.formatAsDecimal;
import static br.net.du.sztoks.controller.util.ControllerUtils.formatAsPercentage;
import static br.net.du.sztoks.controller.util.ControllerUtils.toDecimal;
import static br.net.du.sztoks.controller.util.MoneyFormatUtils.format;

import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.account.AdditionalCurrencyAccount;
import br.net.du.sztoks.model.account.FutureTithingCapable;
import java.math.BigDecimal;
import lombok.Getter;
import org.joda.money.CurrencyUnit;

@Getter
public class AdditionalCurrencyAccountViewModelOutput extends AccountViewModelOutput {
    private final String shares;
    private final String amountInvested;
    private final String averagePurchasePrice;
    private final String valueInBaseCurrency;
    private final String profitPercentage;
    private final String futureTithingPolicy;

    public AdditionalCurrencyAccountViewModelOutput(
            final AccountViewModelOutput other,
            final String shares,
            final String amountInvested,
            final String averagePurchasePrice,
            final String valueInBaseCurrency,
            final String profitPercentage,
            final String futureTithingPolicy) {
        super(other);
        this.shares = shares;
        this.amountInvested = amountInvested;
        this.averagePurchasePrice = averagePurchasePrice;
        this.valueInBaseCurrency = valueInBaseCurrency;
        this.profitPercentage = profitPercentage;
        this.futureTithingPolicy = futureTithingPolicy;
    }

    public static AdditionalCurrencyAccountViewModelOutput of(
            final Account account, final boolean includeTotals) {
        final AdditionalCurrencyAccount aca = (AdditionalCurrencyAccount) account;
        final CurrencyUnit base = account.getSnapshot().getBaseCurrencyUnit();
        final CurrencyUnit unit = account.getCurrencyUnit();

        final String shares = new BigDecimal(formatAsDecimal(aca.getShares())).toString();
        final String amountInvested = format(base, toDecimal(aca.getAmountInvested()));
        final String averagePurchasePrice = format(base, toDecimal(aca.getAveragePurchasePrice()));
        final String valueInBaseCurrency = format(base, toDecimal(aca.getValueInBaseCurrency()));
        final String profitPercentage = formatAsPercentage(aca.getProfitPercentage());
        final String futureTithingPolicy =
                ((FutureTithingCapable) account).getFutureTithingPolicy().name();

        return new AdditionalCurrencyAccountViewModelOutput(
                AccountViewModelOutput.of(account, includeTotals),
                shares,
                amountInvested,
                averagePurchasePrice,
                valueInBaseCurrency,
                profitPercentage,
                futureTithingPolicy);
    }

    public static AdditionalCurrencyAccountViewModelOutput of(final Account account) {
        return of(account, false);
    }
}
