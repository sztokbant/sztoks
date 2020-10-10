package br.net.du.myequity.controller.viewmodel.accountsnapshot;

import br.net.du.myequity.controller.viewmodel.CreditCardTotalsViewModelOutput;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import lombok.Getter;
import org.joda.money.CurrencyUnit;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;
import static br.net.du.myequity.controller.util.ControllerUtils.formatAsPercentage;

@Getter
public class CreditCardViewModelOutput extends AccountSnapshotViewModelOutput {
    private final String totalCredit;
    private final String availableCredit;
    private final String statement;
    private final String remainingBalance;
    private final String usedCreditPercentage;

    private final CreditCardTotalsViewModelOutput creditCardTotalsForCurrencyUnit;

    public CreditCardViewModelOutput(final AccountSnapshotViewModelOutput accountSnapshotViewModelOutput,
                                     final String totalCredit,
                                     final String availableCredit,
                                     final String statement,
                                     final String remainingBalance,
                                     final String usedCreditPercentage,
                                     final CreditCardTotalsViewModelOutput creditCardTotalsForCurrencyUnit) {
        super(accountSnapshotViewModelOutput);
        this.totalCredit = totalCredit;
        this.availableCredit = availableCredit;
        this.statement = statement;
        this.remainingBalance = remainingBalance;
        this.usedCreditPercentage = usedCreditPercentage;
        this.creditCardTotalsForCurrencyUnit = creditCardTotalsForCurrencyUnit;
    }

    public static CreditCardViewModelOutput of(final AccountSnapshot accountSnapshot, final boolean includeTotals) {
        final CreditCardSnapshot creditCardSnapshot = (CreditCardSnapshot) accountSnapshot;

        final String totalCredit = formatAsDecimal(creditCardSnapshot.getTotalCredit());
        final String availableCredit = formatAsDecimal(creditCardSnapshot.getAvailableCredit());
        final String statement = formatAsDecimal(creditCardSnapshot.getStatement());
        final String remainingBalance = formatAsDecimal(creditCardSnapshot.getRemainingBalance());
        final String usedCreditPercentage = formatAsPercentage(creditCardSnapshot.getUsedCreditPercentage());

        final CreditCardTotalsViewModelOutput creditCardTotalsForCurrencyUnit;
        if (includeTotals) {
            final Snapshot snapshot = accountSnapshot.getSnapshot();
            final CurrencyUnit currencyUnit = accountSnapshot.getAccount().getCurrencyUnit();
            creditCardTotalsForCurrencyUnit =
                    CreditCardTotalsViewModelOutput.of(snapshot.getCreditCardTotalsForCurrencyUnit(currencyUnit));
        } else {
            creditCardTotalsForCurrencyUnit = null;
        }

        return new CreditCardViewModelOutput(AccountSnapshotViewModelOutput.of(accountSnapshot, includeTotals),
                                             totalCredit,
                                             availableCredit,
                                             statement,
                                             remainingBalance,
                                             usedCreditPercentage,
                                             creditCardTotalsForCurrencyUnit);
    }

    public static CreditCardViewModelOutput of(final AccountSnapshot accountSnapshot) {
        return of(accountSnapshot, false);
    }
}
