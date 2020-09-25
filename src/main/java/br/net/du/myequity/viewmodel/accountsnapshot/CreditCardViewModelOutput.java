package br.net.du.myequity.viewmodel.accountsnapshot;

import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import lombok.Getter;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;
import static br.net.du.myequity.controller.util.ControllerUtils.formatAsPercentage;

@Getter
public class CreditCardViewModelOutput extends AccountSnapshotViewModelOutput {
    private final String totalCredit;
    private final String availableCredit;
    private final String usedCreditPercentage;

    public CreditCardViewModelOutput(final AccountSnapshotViewModelOutput accountSnapshotViewModelOutput,
                                     final String totalCredit,
                                     final String availableCredit,
                                     final String usedCreditPercentage) {
        super(accountSnapshotViewModelOutput);
        this.totalCredit = totalCredit;
        this.availableCredit = availableCredit;
        this.usedCreditPercentage = usedCreditPercentage;
    }

    public static CreditCardViewModelOutput of(final AccountSnapshot accountSnapshot, final boolean includeTotals) {
        final CreditCardSnapshot creditCardSnapshot = (CreditCardSnapshot) accountSnapshot;

        final String totalCredit = formatAsDecimal(creditCardSnapshot.getTotalCredit());
        final String availableCredit = formatAsDecimal(creditCardSnapshot.getAvailableCredit());
        final String usedCreditPercentage = formatAsPercentage(creditCardSnapshot.getUsedCreditPercentage());

        return new CreditCardViewModelOutput(AccountSnapshotViewModelOutput.of(accountSnapshot, includeTotals),
                                             totalCredit,
                                             availableCredit,
                                             usedCreditPercentage);
    }

    public static CreditCardViewModelOutput of(final AccountSnapshot accountSnapshot) {
        return of(accountSnapshot, false);
    }
}
