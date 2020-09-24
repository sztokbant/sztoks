package br.net.du.myequity.controller.model;

import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import lombok.Getter;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;
import static br.net.du.myequity.controller.util.ControllerUtils.formatAsPercentage;

@Getter
public class CreditCardSnapshotUpdateJsonResponse extends AccountSnapshotUpdateJsonResponse {
    private final String totalCredit;
    private final String availableCredit;
    private final String usedCreditPercentage;

    public CreditCardSnapshotUpdateJsonResponse(final AccountSnapshotUpdateJsonResponse snapshotAccountUpdateJsonResponse,
                                                final String totalCredit,
                                                final String availableCredit,
                                                final String usedCreditPercentage) {
        super(snapshotAccountUpdateJsonResponse);
        this.totalCredit = totalCredit;
        this.availableCredit = availableCredit;
        this.usedCreditPercentage = usedCreditPercentage;
    }

    public static CreditCardSnapshotUpdateJsonResponse of(final CreditCardSnapshot creditCardSnapshot) {
        final String totalCredit = formatAsDecimal(creditCardSnapshot.getTotalCredit());
        final String availableCredit = formatAsDecimal(creditCardSnapshot.getAvailableCredit());
        final String usedCreditPercentage = formatAsPercentage(creditCardSnapshot.getUsedCreditPercentage());

        return new CreditCardSnapshotUpdateJsonResponse(AccountSnapshotUpdateJsonResponse.of(creditCardSnapshot),
                                                        totalCredit,
                                                        availableCredit,
                                                        usedCreditPercentage);
    }
}
