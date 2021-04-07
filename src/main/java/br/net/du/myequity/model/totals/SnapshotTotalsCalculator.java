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

    public Map<CurrencyUnit, CreditCardTotals> getCreditCardTotalsByCurrency() {
        final Map<CurrencyUnit, CreditCardTotals> creditCardTotals = new HashMap<>();

        for (final Account account : snapshot.getAccounts()) {
            if (!(account instanceof CreditCardAccount)) {
                continue;
            }

            final CurrencyUnit currencyUnit = account.getCurrencyUnit();

            if (!creditCardTotals.containsKey(currencyUnit)) {
                creditCardTotals.put(
                        currencyUnit,
                        new CreditCardTotals(
                                currencyUnit, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
            }

            final CreditCardTotals creditCardTotalsForCurrency = creditCardTotals.get(currencyUnit);
            updateCreditCardTotals(creditCardTotalsForCurrency, (CreditCardAccount) account);

            creditCardTotals.put(currencyUnit, creditCardTotalsForCurrency);
        }

        return creditCardTotals;
    }

    public CreditCardTotals getCreditCardTotalsForCurrency(
            @NonNull final CurrencyUnit currencyUnit) {
        final CreditCardTotals creditCardTotals =
                new CreditCardTotals(
                        currencyUnit, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        for (final Account account : snapshot.getAccounts()) {
            if (!(account instanceof CreditCardAccount)
                    || !account.getCurrencyUnit().equals(currencyUnit)) {
                continue;
            }

            updateCreditCardTotals(creditCardTotals, (CreditCardAccount) account);
        }

        return creditCardTotals;
    }

    private void updateCreditCardTotals(
            final CreditCardTotals creditCardTotals, final CreditCardAccount creditCardAccount) {
        creditCardTotals.setTotalCredit(
                creditCardTotals.getTotalCredit().add(creditCardAccount.getTotalCredit()));
        creditCardTotals.setAvailableCredit(
                creditCardTotals.getAvailableCredit().add(creditCardAccount.getAvailableCredit()));
        creditCardTotals.setStatement(
                creditCardTotals.getStatement().add(creditCardAccount.getStatement()));
    }
}
