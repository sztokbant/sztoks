package br.net.du.sztoks.model.totals;

import static br.net.du.sztoks.model.util.ModelConstants.DIVISION_SCALE;
import static br.net.du.sztoks.model.util.ModelConstants.ONE_HUNDRED;

import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.account.AdditionalCurrencyAccount;
import br.net.du.sztoks.model.account.CreditCardAccount;
import br.net.du.sztoks.model.account.InvestmentAccount;
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
            if (!accountSubtypeDisplayGroup.equals(account.getAccountSubtypeDisplayGroup())) {
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

    public Map<CurrencyUnit, AdditionalCurrenciesTotal> getAdditionalCurrenciesTotalByCurrency() {
        final Map<CurrencyUnit, AdditionalCurrenciesTotal> totals = new HashMap<>();

        for (final Account account : snapshot.getAccounts()) {
            if (!(account instanceof AdditionalCurrencyAccount)) {
                continue;
            }

            final AdditionalCurrencyAccount aca = (AdditionalCurrencyAccount) account;
            final CurrencyUnit currencyUnit = aca.getCurrencyUnit();

            final BigDecimal shares = aca.getShares();
            final BigDecimal amountInvested = aca.getAmountInvested();
            final BigDecimal balanceInBase = aca.getValueInBaseCurrency();

            if (!totals.containsKey(currencyUnit)) {
                totals.put(
                        currencyUnit,
                        new AdditionalCurrenciesTotal(
                                currencyUnit,
                                BigDecimal.ZERO,
                                BigDecimal.ZERO,
                                BigDecimal.ZERO,
                                BigDecimal.ZERO,
                                BigDecimal.ZERO));
            }

            final AdditionalCurrenciesTotal existing = totals.get(currencyUnit);
            final BigDecimal newShares = existing.getShares().add(shares);
            final BigDecimal newInvested = existing.getAmountInvested().add(amountInvested);
            final BigDecimal newBalance = existing.getBalance().add(balanceInBase);
            final BigDecimal profitPercentage =
                    newInvested.compareTo(BigDecimal.ZERO) == 0
                            ? BigDecimal.ZERO
                            : newBalance
                                    .divide(newInvested, DIVISION_SCALE, RoundingMode.HALF_UP)
                                    .subtract(BigDecimal.ONE)
                                    .multiply(ONE_HUNDRED);

            final BigDecimal averagePurchasePrice =
                    newShares.compareTo(BigDecimal.ZERO) == 0
                            ? BigDecimal.ZERO
                            : newInvested.divide(newShares, DIVISION_SCALE, RoundingMode.HALF_UP);

            totals.put(
                    currencyUnit,
                    new AdditionalCurrenciesTotal(
                            currencyUnit,
                            newShares,
                            newInvested,
                            averagePurchasePrice,
                            profitPercentage,
                            newBalance));
        }

        return totals;
    }

    public AdditionalCurrenciesTotal getAdditionalCurrenciesTotalForCurrency(
            @NonNull final CurrencyUnit currencyUnit) {
        final Map<CurrencyUnit, AdditionalCurrenciesTotal> all =
                getAdditionalCurrenciesTotalByCurrency();
        return all.getOrDefault(
                currencyUnit,
                new AdditionalCurrenciesTotal(
                        currencyUnit,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO));
    }
}
