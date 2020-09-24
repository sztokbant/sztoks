package br.net.du.myequity.controller.model;

import br.net.du.myequity.model.snapshot.InvestmentSnapshot;
import lombok.Getter;

import java.math.BigDecimal;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;
import static br.net.du.myequity.controller.util.ControllerUtils.formatAsPercentage;

@Getter
public class InvestmentSnapshotUpdateJsonResponse extends AccountSnapshotUpdateJsonResponse {
    private final String shares;
    private final String originalShareValue;
    private final String currentShareValue;
    private final String profitPercentage;

    public InvestmentSnapshotUpdateJsonResponse(final AccountSnapshotUpdateJsonResponse snapshotAccountUpdateJsonResponse,
                                                final String shares,
                                                final String originalShareValue,
                                                final String currentShareValue,
                                                final String profitPercentage) {
        super(snapshotAccountUpdateJsonResponse);
        this.shares = shares;
        this.originalShareValue = originalShareValue;
        this.currentShareValue = currentShareValue;
        this.profitPercentage = profitPercentage;
    }

    public static InvestmentSnapshotUpdateJsonResponse of(final InvestmentSnapshot investmentSnapshot) {
        final String shares = new BigDecimal(formatAsDecimal(investmentSnapshot.getShares())).toString();
        final String originalShareValue =
                new BigDecimal(formatAsDecimal(investmentSnapshot.getOriginalShareValue())).toString();
        final String currentShareValue =
                new BigDecimal(formatAsDecimal(investmentSnapshot.getCurrentShareValue())).toString();
        final String profitPercentage = formatAsPercentage(investmentSnapshot.getProfitPercentage());

        return new InvestmentSnapshotUpdateJsonResponse(AccountSnapshotUpdateJsonResponse.of(investmentSnapshot),
                                                        shares,
                                                        originalShareValue,
                                                        currentShareValue,
                                                        profitPercentage);
    }
}
