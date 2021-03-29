package br.net.du.myequity.controller.viewmodel.account;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsPercentage;
import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;

import br.net.du.myequity.controller.util.MoneyFormatUtils;
import br.net.du.myequity.controller.viewmodel.CreditCardTotalsViewModelOutput;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.CreditCardAccount;
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
        final CreditCardAccount creditCardSnapshot = (CreditCardAccount) account;

        final CurrencyUnit currencyUnit = account.getCurrencyUnit();

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

        final CreditCardTotalsViewModelOutput creditCardTotalsForCurrencyUnit;
        if (includeTotals) {
            final Snapshot snapshot = account.getSnapshot();
            creditCardTotalsForCurrencyUnit =
                    CreditCardTotalsViewModelOutput.of(
                            snapshot.getCreditCardTotalsForCurrencyUnit(currencyUnit));
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
