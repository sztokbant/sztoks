package br.net.du.myequity.viewmodel;

import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.InvestmentSnapshot;
import lombok.Getter;

import java.math.BigDecimal;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;

@Getter
public class InvestmentViewModelOutput extends AccountViewModelOutputBase {
    private final BigDecimal shares;
    private final BigDecimal originalShareValue;
    private final BigDecimal currentShareValue;
    private final BigDecimal profitPercentage;

    public static InvestmentViewModelOutput of(final AccountSnapshot accountSnapshot) {
        final InvestmentSnapshot investmentSnapshot = (InvestmentSnapshot) accountSnapshot;
        final BigDecimal profitPercentage = new BigDecimal(formatAsDecimal(investmentSnapshot.getProfitPercentage()));

        return new InvestmentViewModelOutput(AccountViewModelOutputBase.of(accountSnapshot),
                                             investmentSnapshot.getShares(),
                                             investmentSnapshot.getOriginalShareValue(),
                                             investmentSnapshot.getCurrentShareValue(),
                                             profitPercentage);
    }

    public InvestmentViewModelOutput(final AccountViewModelOutputBase simpleAccountViewModelOutput,
                                     final BigDecimal shares,
                                     final BigDecimal originalShareValue,
                                     final BigDecimal currentShareValue,
                                     final BigDecimal profitPercentage) {
        super(simpleAccountViewModelOutput);
        this.shares = shares;
        this.originalShareValue = originalShareValue;
        this.currentShareValue = currentShareValue;
        this.profitPercentage = profitPercentage;
    }
}
