package br.net.du.myequity.controller.viewmodel.accountsnapshot;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;
import static br.net.du.myequity.controller.util.ControllerUtils.formatAsPercentage;
import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;

import br.net.du.myequity.controller.util.MoneyFormatUtils;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.InvestmentSnapshot;
import java.math.BigDecimal;
import lombok.Getter;
import org.joda.money.CurrencyUnit;

@Getter
public class InvestmentViewModelOutput extends AccountSnapshotViewModelOutput {
    private final String shares;
    private final String originalShareValue;
    private final String currentShareValue;
    private final String profitPercentage;

    public InvestmentViewModelOutput(
            final AccountSnapshotViewModelOutput accountSnapshotViewModelOutput,
            final String shares,
            final String originalShareValue,
            final String currentShareValue,
            final String profitPercentage) {
        super(accountSnapshotViewModelOutput);
        this.shares = shares;
        this.originalShareValue = originalShareValue;
        this.currentShareValue = currentShareValue;
        this.profitPercentage = profitPercentage;
    }

    public static InvestmentViewModelOutput of(
            final AccountSnapshot accountSnapshot, final boolean includeTotals) {
        final InvestmentSnapshot investmentSnapshot = (InvestmentSnapshot) accountSnapshot;

        final String shares =
                new BigDecimal(formatAsDecimal(investmentSnapshot.getShares())).toString();

        final CurrencyUnit currencyUnit = accountSnapshot.getAccount().getCurrencyUnit();

        final String originalShareValue =
                MoneyFormatUtils.format(
                        currencyUnit, toDecimal(investmentSnapshot.getOriginalShareValue()));

        final String currentShareValue =
                MoneyFormatUtils.format(
                        currencyUnit, toDecimal((investmentSnapshot.getCurrentShareValue())));

        final String profitPercentage =
                formatAsPercentage(investmentSnapshot.getProfitPercentage());

        return new InvestmentViewModelOutput(
                AccountSnapshotViewModelOutput.of(accountSnapshot, includeTotals),
                shares,
                originalShareValue,
                currentShareValue,
                profitPercentage);
    }

    public static InvestmentViewModelOutput of(final AccountSnapshot accountSnapshot) {
        return of(accountSnapshot, false);
    }
}
