package br.net.du.myequity.viewmodel.accountsnapshot;

import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.InvestmentSnapshot;
import lombok.Getter;

import java.math.BigDecimal;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;
import static br.net.du.myequity.controller.util.ControllerUtils.formatAsPercentage;

@Getter
public class InvestmentViewModelOutput extends AccountSnapshotViewModelOutput {
    private final String shares;
    private final String originalShareValue;
    private final String currentShareValue;
    private final String profitPercentage;

    public InvestmentViewModelOutput(final AccountSnapshotViewModelOutput accountSnapshotViewModelOutput,
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

    public static InvestmentViewModelOutput of(final AccountSnapshot accountSnapshot) {
        final InvestmentSnapshot investmentSnapshot = (InvestmentSnapshot) accountSnapshot;

        final String shares = new BigDecimal(formatAsDecimal(investmentSnapshot.getShares())).toString();
        final String originalShareValue =
                new BigDecimal(formatAsDecimal(investmentSnapshot.getOriginalShareValue())).toString();
        final String currentShareValue =
                new BigDecimal(formatAsDecimal(investmentSnapshot.getCurrentShareValue())).toString();
        final String profitPercentage = formatAsPercentage(investmentSnapshot.getProfitPercentage());

        return new InvestmentViewModelOutput(AccountSnapshotViewModelOutput.of(accountSnapshot),
                                             shares,
                                             originalShareValue,
                                             currentShareValue,
                                             profitPercentage);
    }

}
