package br.net.du.myequity.controller.viewmodel.account;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsPercentage;
import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.CreditCardAccount;
import br.net.du.myequity.model.totals.SnapshotTotalsCalculator;
import lombok.Getter;
import org.joda.money.CurrencyUnit;

@Getter
public class CreditCardAccountViewModelOutput extends AccountViewModelOutput {
    private final String totalCredit;
    private final String availableCredit;
    private final String statement;
    private final String remainingBalance;
    private final String usedCreditPercentage;

    private final CreditCardTotalsViewModelOutput creditCardTotalsForCurrencyUnit;

    public CreditCardAccountViewModelOutput(
            final AccountViewModelOutput other,
            final String totalCredit,
            final String availableCredit,
            final String statement,
            final String remainingBalance,
            final String usedCreditPercentage,
            final CreditCardTotalsViewModelOutput creditCardTotalsForCurrencyUnit) {
        super(other);
        this.totalCredit = totalCredit;
        this.availableCredit = availableCredit;
        this.statement = statement;
        this.remainingBalance = remainingBalance;
        this.usedCreditPercentage = usedCreditPercentage;
        this.creditCardTotalsForCurrencyUnit = creditCardTotalsForCurrencyUnit;
    }

    public static CreditCardAccountViewModelOutput of(
            final Account account, final boolean includeTotals) {
        final CreditCardAccount creditCardAccount = (CreditCardAccount) account;

        final CurrencyUnit currencyUnit = account.getCurrencyUnit();

        final String totalCredit =
                format(currencyUnit, toDecimal(creditCardAccount.getTotalCredit()));
        final String availableCredit =
                format(currencyUnit, toDecimal(creditCardAccount.getAvailableCredit()));
        final String statement = format(currencyUnit, toDecimal(creditCardAccount.getStatement()));
        final String remainingBalance =
                format(currencyUnit, toDecimal(creditCardAccount.getRemainingBalance()));
        final String usedCreditPercentage =
                formatAsPercentage(creditCardAccount.getUsedCreditPercentage());

        final CreditCardTotalsViewModelOutput creditCardTotalsForCurrencyUnit;
        if (includeTotals) {
            final SnapshotTotalsCalculator snapshotTotalsCalculator =
                    new SnapshotTotalsCalculator(account.getSnapshot());
            creditCardTotalsForCurrencyUnit =
                    CreditCardTotalsViewModelOutput.of(
                            snapshotTotalsCalculator.getCreditCardsTotalForCurrency(currencyUnit));
        } else {
            creditCardTotalsForCurrencyUnit = null;
        }

        return new CreditCardAccountViewModelOutput(
                AccountViewModelOutput.of(account, includeTotals),
                totalCredit,
                availableCredit,
                statement,
                remainingBalance,
                usedCreditPercentage,
                creditCardTotalsForCurrencyUnit);
    }

    public static CreditCardAccountViewModelOutput of(final Account account) {
        return of(account, false);
    }
}
