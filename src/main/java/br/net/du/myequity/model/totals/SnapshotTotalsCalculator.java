package br.net.du.myequity.model.totals;

import static br.net.du.myequity.model.util.ModelConstants.DIVISION_SCALE;
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

    public BalanceUpdatableSubtypeTotal getTotalBalance(
            final AccountSubtypeDisplayGroup accountSubtypeDisplayGroup) {
        final CurrencyUnit baseCurrencyUnit = snapshot.getBaseCurrencyUnit();

        BigDecimal totalBalance = BigDecimal.ZERO;

        for (final Account account : snapshot.getAccounts()) {
            if (!accountSubtypeDisplayGroup.accepts(account.getClass())) {
                continue;
            }

            final CurrencyUnit currencyUnit = account.getCurrencyUnit();

            final BigDecimal balance =
                    currencyUnit.equals(baseCurrencyUnit)
                            ? account.getBalance()
                            : snapshot.toBaseCurrency(currencyUnit, account.getBalance());

            totalBalance = totalBalance.add(balance);
        }

        return new BalanceUpdatableSubtypeTotal(baseCurrencyUnit, totalBalance);
    }

    public InvestmentsTotal getInvestmentsTotal() {
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
                                .divide(totalInvested, DIVISION_SCALE, RoundingMode.HALF_UP)
                                .subtract(BigDecimal.ONE)
                                .multiply(ONE_HUNDRED);

        return new InvestmentsTotal(
                baseCurrencyUnit, totalInvested, profitPercentage, totalBalance);
    }

    public Map<CurrencyUnit, CreditCardsTotal> getCreditCardsTotalByCurrency() {
        final Map<CurrencyUnit, CreditCardsTotal> creditCardsTotal = new HashMap<>();

        for (final Account account : snapshot.getAccounts()) {
            if (!(account instanceof CreditCardAccount)) {
                continue;
            }

            final CurrencyUnit currencyUnit = account.getCurrencyUnit();

            if (!creditCardsTotal.containsKey(currencyUnit)) {
                creditCardsTotal.put(
                        currencyUnit,
                        new CreditCardsTotal(
                                currencyUnit, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
            }

            final CreditCardsTotal creditCardsTotalForCurrency = creditCardsTotal.get(currencyUnit);
            updateCreditCardsTotal(creditCardsTotalForCurrency, (CreditCardAccount) account);

            creditCardsTotal.put(currencyUnit, creditCardsTotalForCurrency);
        }

        return creditCardsTotal;
    }

    public CreditCardsTotal getCreditCardsTotalForCurrency(
            @NonNull final CurrencyUnit currencyUnit) {
        final CreditCardsTotal creditCardsTotal =
                new CreditCardsTotal(
                        currencyUnit, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        for (final Account account : snapshot.getAccounts()) {
            if (!(account instanceof CreditCardAccount)
                    || !account.getCurrencyUnit().equals(currencyUnit)) {
                continue;
            }

            updateCreditCardsTotal(creditCardsTotal, (CreditCardAccount) account);
        }

        return creditCardsTotal;
    }

    private void updateCreditCardsTotal(
            final CreditCardsTotal creditCardTotals, final CreditCardAccount creditCardAccount) {
        creditCardTotals.setTotalCredit(
                creditCardTotals.getTotalCredit().add(creditCardAccount.getTotalCredit()));
        creditCardTotals.setAvailableCredit(
                creditCardTotals.getAvailableCredit().add(creditCardAccount.getAvailableCredit()));
        creditCardTotals.setStatement(
                creditCardTotals.getStatement().add(creditCardAccount.getStatement()));
    }
}
