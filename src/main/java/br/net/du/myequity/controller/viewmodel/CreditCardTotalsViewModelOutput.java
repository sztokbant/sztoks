package br.net.du.myequity.controller.viewmodel;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsPercentage;
import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

import br.net.du.myequity.model.account.CreditCardAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;

@AllArgsConstructor
@Data
@Builder
public class CreditCardTotalsViewModelOutput {

    private static final String ZERO = "0.00";
    private static final String ZERO_PERCENT = "0.00%";

    private final String balance;

    private final String totalCredit;
    private final String availableCredit;
    private final String statement;
    private final String remainingBalance;
    private final String usedCreditPercentage;

    public static CreditCardTotalsViewModelOutput of(final CreditCardAccount creditCardSnapshot) {
        final CurrencyUnit currencyUnit = creditCardSnapshot.getCurrencyUnit();

        final String balance = format(currencyUnit, toDecimal(creditCardSnapshot.getBalance()));

        final String totalCredit =
                format(currencyUnit, toDecimal(creditCardSnapshot.getTotalCredit()));
        final String availableCredit =
                format(currencyUnit, toDecimal(creditCardSnapshot.getAvailableCredit()));
        final String statement = format(currencyUnit, toDecimal(creditCardSnapshot.getStatement()));
        final String remainingBalance =
                format(currencyUnit, toDecimal(creditCardSnapshot.getRemainingBalance()));
        final String usedCreditPercentage =
                formatAsPercentage(creditCardSnapshot.getUsedCreditPercentage());

        return new CreditCardTotalsViewModelOutput(
                balance,
                totalCredit,
                availableCredit,
                statement,
                remainingBalance,
                usedCreditPercentage);
    }

    public static CreditCardTotalsViewModelOutput newEmptyInstance() {
        return CreditCardTotalsViewModelOutput.builder()
                .balance(ZERO)
                .totalCredit(ZERO)
                .availableCredit(ZERO)
                .statement(ZERO)
                .remainingBalance(ZERO)
                .usedCreditPercentage(ZERO_PERCENT)
                .availableCredit(ZERO)
                .build();
    }
}
