package br.net.du.myequity.controller.viewmodel;

import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.joda.money.CurrencyUnit;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;
import static br.net.du.myequity.controller.util.ControllerUtils.formatAsPercentage;

@AllArgsConstructor
@Data
@Builder
public class CreditCardTotalsViewModelOutput {

    private static final String ZERO = "0.00";
    private static final String ZERO_PERCENT = "0.00%";

    private final String name;

    private final String balance;
    private final String currencyUnit;
    private final String currencyUnitSymbol;

    private final String totalCredit;
    private final String availableCredit;
    private final String statement;
    private final String remainingBalance;
    private final String usedCreditPercentage;

    public static CreditCardTotalsViewModelOutput of(final CreditCardSnapshot creditCardSnapshot) {
        final String name = creditCardSnapshot.getAccount().getName();

        final String balance = formatAsDecimal(creditCardSnapshot.getTotal());
        final String currencyUnit = creditCardSnapshot.getAccount().getCurrencyUnit().getCode();
        final String currencyUnitSymbol = creditCardSnapshot.getAccount().getCurrencyUnit().getSymbol();

        final String totalCredit = formatAsDecimal(creditCardSnapshot.getTotalCredit());
        final String availableCredit = formatAsDecimal(creditCardSnapshot.getAvailableCredit());
        final String statement = formatAsDecimal(creditCardSnapshot.getStatement());
        final String remainingBalance = formatAsDecimal(creditCardSnapshot.getRemainingBalance());
        final String usedCreditPercentage = formatAsPercentage(creditCardSnapshot.getUsedCreditPercentage());

        return new CreditCardTotalsViewModelOutput(name,
                                                   balance,
                                                   currencyUnit,
                                                   currencyUnitSymbol,
                                                   totalCredit,
                                                   availableCredit,
                                                   statement,
                                                   remainingBalance,
                                                   usedCreditPercentage);
    }

    public static CreditCardTotalsViewModelOutput newEmptyInstance(final CurrencyUnit currencyUnit) {
        return CreditCardTotalsViewModelOutput.builder()
                                              .name(StringUtils.EMPTY)
                                              .balance(ZERO)
                                              .currencyUnit(currencyUnit.getCode())
                                              .currencyUnitSymbol(currencyUnit.getSymbol())
                                              .totalCredit(ZERO)
                                              .availableCredit(ZERO)
                                              .statement(ZERO)
                                              .remainingBalance(ZERO)
                                              .usedCreditPercentage(ZERO_PERCENT)
                                              .availableCredit(ZERO)
                                              .build();
    }
}
