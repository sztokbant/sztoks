package br.net.du.myequity.viewmodel;

import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CreditCardViewModelOutput extends SimpleAccountViewModelOutput {
    private final BigDecimal totalCredit;
    private final BigDecimal availableCredit;

    public static CreditCardViewModelOutput of(final AccountSnapshot accountSnapshot) {
        final CreditCardSnapshot creditCardSnapshot = (CreditCardSnapshot) accountSnapshot;
        return new CreditCardViewModelOutput(SimpleAccountViewModelOutput.of(accountSnapshot),
                                             creditCardSnapshot.getTotalCredit(),
                                             creditCardSnapshot.getAvailableCredit());
    }

    public CreditCardViewModelOutput(final SimpleAccountViewModelOutput simpleAccountViewModelOutput,
                                     final BigDecimal totalCredit,
                                     final BigDecimal availableCredit) {
        super(simpleAccountViewModelOutput);
        this.totalCredit = totalCredit;
        this.availableCredit = availableCredit;
    }
}
