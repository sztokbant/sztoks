package br.net.du.myequity.model.totals;

import static br.net.du.myequity.model.util.ModelConstants.ONE_HUNDRED;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.CreditCardAccount;
import br.net.du.myequity.model.account.InvestmentAccount;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.joda.money.CurrencyUnit;

@AllArgsConstructor
@Data
public class SnapshotTotalsCalculator {
    private final Snapshot snapshot;

    public InvestmentTotals getInvestmentTotals() {
        final CurrencyUnit baseCurrencyUnit = snapshot.getBaseCurrencyUnit();

        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal totalBalance = BigDecimal.ZERO;

        for (final Account account : snapshot.getAccounts()) {
            if (!(account instanceof InvestmentAccount)) {
                continue;
            }

            final InvestmentAccount investmentAccount = (InvestmentAccount) account;
            final CurrencyUnit currencyUnit = investmentAccount.getCurrencyUnit();

            final BigDecimal amountInvested =
                    currencyUnit.equals(baseCurrencyUnit)
                            ? investmentAccount.getAmountInvested()
                            : snapshot.toBaseCurrency(
                                    currencyUnit, investmentAccount.getAmountInvested());

            final BigDecimal balance =
                    currencyUnit.equals(baseCurrencyUnit)
                            ? investmentAccount.getBalance()
                            : snapshot.toBaseCurrency(currencyUnit, investmentAccount.getBalance());

            totalInvested = totalInvested.add(amountInvested);
            totalBalance = totalBalance.add(balance);
        }

        final BigDecimal profitPercentage =
                totalInvested.compareTo(BigDecimal.ZERO) == 0
                        ? BigDecimal.ZERO
                        : totalBalance
                                .divide(totalInvested, RoundingMode.HALF_UP)
                                .subtract(BigDecimal.ONE)
                                .multiply(ONE_HUNDRED);

        return new InvestmentTotals(
                baseCurrencyUnit, totalInvested, profitPercentage, totalBalance);
    }

    /** Create transient CreditCardAccount objects aggregated by currency unit. */
    public Map<CurrencyUnit, CreditCardAccount> getCreditCardTotals() {
        final Map<CurrencyUnit, CreditCardAccount> creditCardTotals = new HashMap<>();

        for (final Account account : snapshot.getAccounts()) {
            if (!(account instanceof CreditCardAccount)) {
                continue;
            }

            final CreditCardAccount creditCardAccount = (CreditCardAccount) account;
            final CurrencyUnit currencyUnit = creditCardAccount.getCurrencyUnit();

            if (!creditCardTotals.containsKey(currencyUnit)) {
                creditCardTotals.put(currencyUnit, creditCardAccount.copy());
            } else {
                final CreditCardAccount creditCardTotalForCurrency =
                        creditCardTotals.get(currencyUnit);
                updateCreditCardTotalsForCurrency(creditCardAccount, creditCardTotalForCurrency);
                creditCardTotals.put(currencyUnit, creditCardTotalForCurrency);
            }
        }

        return creditCardTotals;
    }

    /** Create transient CreditCardAccount object for specified currency unit. */
    public CreditCardAccount getCreditCardTotalsForCurrencyUnit(
            @NonNull final CurrencyUnit currencyUnit) {
        CreditCardAccount creditCardTotalsForCurrencyUnit = null;

        for (final Account account : snapshot.getAccounts()) {
            if (!(account instanceof CreditCardAccount)
                    || !account.getCurrencyUnit().equals(currencyUnit)) {
                continue;
            }

            final CreditCardAccount creditCardSnapshot = (CreditCardAccount) account;

            if (creditCardTotalsForCurrencyUnit == null) {
                creditCardTotalsForCurrencyUnit = creditCardSnapshot.copy();
            } else {
                updateCreditCardTotalsForCurrency(
                        creditCardSnapshot, creditCardTotalsForCurrencyUnit);
            }
        }

        return creditCardTotalsForCurrencyUnit;
    }

    private void updateCreditCardTotalsForCurrency(
            final CreditCardAccount creditCardAccount,
            final CreditCardAccount creditCardSnapshotForCurrency) {
        creditCardSnapshotForCurrency.setAvailableCredit(
                creditCardSnapshotForCurrency
                        .getAvailableCredit()
                        .add(creditCardAccount.getAvailableCredit()));
        creditCardSnapshotForCurrency.setTotalCredit(
                creditCardSnapshotForCurrency
                        .getTotalCredit()
                        .add(creditCardAccount.getTotalCredit()));
        creditCardSnapshotForCurrency.setStatement(
                creditCardSnapshotForCurrency.getStatement().add(creditCardAccount.getStatement()));
    }
}
