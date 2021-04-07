package br.net.du.myequity.model.totals;

import br.net.du.myequity.model.account.CreditCardAccount;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.joda.money.CurrencyUnit;

@AllArgsConstructor
@Data
public class CreditCardTotals {

    private final CurrencyUnit currencyUnit;

    private BigDecimal totalCredit;
    private BigDecimal availableCredit;
    private BigDecimal statement;

    public BigDecimal getBalance() {
        return CreditCardAccount.getBalance(totalCredit, availableCredit);
    }

    public BigDecimal getRemainingBalance() {
        return CreditCardAccount.getRemainingBalance(getBalance(), statement);
    }

    public BigDecimal getUsedCreditPercentage() {
        return CreditCardAccount.getUsedCreditPercentage(totalCredit, availableCredit);
    }
}
