package br.net.du.myequity.controller.viewmodel.account;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsPercentage;
import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

import br.net.du.myequity.model.totals.CreditCardTotals;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;

@AllArgsConstructor
@Data
@Builder
public class CreditCardTotalsViewModelOutput {

    private final String balance;

    private final String totalCredit;
    private final String availableCredit;
    private final String statement;
    private final String remainingBalance;
    private final String usedCreditPercentage;

    public static CreditCardTotalsViewModelOutput of(final CreditCardTotals creditCardTotals) {
        final CurrencyUnit currencyUnit = creditCardTotals.getCurrencyUnit();

        final String balance = format(currencyUnit, toDecimal(creditCardTotals.getBalance()));

        final String totalCredit =
                format(currencyUnit, toDecimal(creditCardTotals.getTotalCredit()));
        final String availableCredit =
                format(currencyUnit, toDecimal(creditCardTotals.getAvailableCredit()));
        final String statement = format(currencyUnit, toDecimal(creditCardTotals.getStatement()));
        final String remainingBalance =
                format(currencyUnit, toDecimal(creditCardTotals.getRemainingBalance()));
        final String usedCreditPercentage =
                formatAsPercentage(creditCardTotals.getUsedCreditPercentage());

        return new CreditCardTotalsViewModelOutput(
                balance,
                totalCredit,
                availableCredit,
                statement,
                remainingBalance,
                usedCreditPercentage);
    }
}
