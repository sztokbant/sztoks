package br.net.du.myequity.controller.viewmodel.account;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;
import static br.net.du.myequity.controller.util.ControllerUtils.formatAsPercentage;
import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;

import br.net.du.myequity.controller.util.MoneyFormatUtils;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.InvestmentAccount;
import java.math.BigDecimal;
import lombok.Getter;
import org.joda.money.CurrencyUnit;

@Getter
public class InvestmentAccountViewModelOutput extends AccountViewModelOutput {
    private final String shares;
    private final String originalShareValue;
    private final String currentShareValue;
    private final String profitPercentage;

    public InvestmentAccountViewModelOutput(
            final AccountViewModelOutput other,
            final String shares,
            final String originalShareValue,
            final String currentShareValue,
            final String profitPercentage) {
        super(other);
        this.shares = shares;
        this.originalShareValue = originalShareValue;
        this.currentShareValue = currentShareValue;
        this.profitPercentage = profitPercentage;
    }

    public static InvestmentAccountViewModelOutput of(
            final Account account, final boolean includeTotals) {
        final InvestmentAccount investmentSnapshot = (InvestmentAccount) account;

        final String shares =
                new BigDecimal(formatAsDecimal(investmentSnapshot.getShares())).toString();

        final CurrencyUnit currencyUnit = account.getCurrencyUnit();

        final String originalShareValue =
                MoneyFormatUtils.format(
                        currencyUnit, toDecimal(investmentSnapshot.getOriginalShareValue()));

        final String currentShareValue =
                MoneyFormatUtils.format(
                        currencyUnit, toDecimal((investmentSnapshot.getCurrentShareValue())));

        final String profitPercentage =
                formatAsPercentage(investmentSnapshot.getProfitPercentage());

        return new InvestmentAccountViewModelOutput(
                AccountViewModelOutput.of(account, includeTotals),
                shares,
                originalShareValue,
                currentShareValue,
                profitPercentage);
    }

    public static InvestmentAccountViewModelOutput of(final Account account) {
        return of(account, false);
    }
}
