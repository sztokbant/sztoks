package br.net.du.myequity.controller.viewmodel.account;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;
import static br.net.du.myequity.controller.util.ControllerUtils.formatAsPercentage;
import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.InvestmentAccount;
import br.net.du.myequity.model.totals.SnapshotTotalsCalculator;
import java.math.BigDecimal;
import lombok.Getter;
import org.joda.money.CurrencyUnit;

@Getter
public class InvestmentAccountViewModelOutput extends AccountViewModelOutput {
    private final String shares;
    private final String amountInvested;
    private final String averagePurchasePrice;
    private final String currentShareValue;
    private final String profitPercentage;

    private final InvestmentTotalsViewModelOutput investmentTotals;

    public InvestmentAccountViewModelOutput(
            final AccountViewModelOutput other,
            final String shares,
            final String amountInvested,
            final String averagePurchasePrice,
            final String currentShareValue,
            final String profitPercentage,
            final InvestmentTotalsViewModelOutput investmentTotals) {
        super(other);
        this.shares = shares;
        this.amountInvested = amountInvested;
        this.averagePurchasePrice = averagePurchasePrice;
        this.currentShareValue = currentShareValue;
        this.profitPercentage = profitPercentage;
        this.investmentTotals = investmentTotals;
    }

    public static InvestmentAccountViewModelOutput of(
            final Account account, final boolean includeTotals) {
        final InvestmentAccount investmentAccount = (InvestmentAccount) account;

        final String shares =
                new BigDecimal(formatAsDecimal(investmentAccount.getShares())).toString();

        final CurrencyUnit currencyUnit = account.getCurrencyUnit();

        final String amountInvested =
                format(currencyUnit, toDecimal(investmentAccount.getAmountInvested()));

        final String averagePurchasePrice =
                format(currencyUnit, toDecimal(investmentAccount.getAveragePurchasePrice()));

        final String currentShareValue =
                format(currencyUnit, toDecimal((investmentAccount.getCurrentShareValue())));

        final String profitPercentage = formatAsPercentage(investmentAccount.getProfitPercentage());

        final InvestmentTotalsViewModelOutput investmentTotalsViewModelOutput =
                includeTotals
                        ? InvestmentTotalsViewModelOutput.of(
                                new SnapshotTotalsCalculator(account.getSnapshot())
                                        .getInvestmentsTotal())
                        : null;

        return new InvestmentAccountViewModelOutput(
                AccountViewModelOutput.of(account, includeTotals),
                shares,
                amountInvested,
                averagePurchasePrice,
                currentShareValue,
                profitPercentage,
                investmentTotalsViewModelOutput);
    }

    public static InvestmentAccountViewModelOutput of(final Account account) {
        return of(account, false);
    }
}
