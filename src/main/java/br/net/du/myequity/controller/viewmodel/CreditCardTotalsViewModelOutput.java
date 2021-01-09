package br.net.du.myequity.controller.viewmodel;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsPercentage;
import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;

import br.net.du.myequity.controller.util.MoneyFormatUtils;
import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
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

    public static CreditCardTotalsViewModelOutput of(final CreditCardSnapshot creditCardSnapshot) {
        final CurrencyUnit currencyUnit = creditCardSnapshot.getAccount().getCurrencyUnit();

        final String balance =
                MoneyFormatUtils.format(currencyUnit, toDecimal(creditCardSnapshot.getTotal()));

        final String totalCredit =
                MoneyFormatUtils.format(
                        currencyUnit, toDecimal(creditCardSnapshot.getTotalCredit()));
        final String availableCredit =
                MoneyFormatUtils.format(
                        currencyUnit, toDecimal(creditCardSnapshot.getAvailableCredit()));
        final String statement =
                MoneyFormatUtils.format(currencyUnit, toDecimal(creditCardSnapshot.getStatement()));
        final String remainingBalance =
                MoneyFormatUtils.format(
                        currencyUnit, toDecimal(creditCardSnapshot.getRemainingBalance()));
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
