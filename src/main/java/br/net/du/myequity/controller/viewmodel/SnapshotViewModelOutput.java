package br.net.du.myequity.controller.viewmodel;

import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;
import static br.net.du.myequity.controller.util.ViewModelOutputUtils.getAccountViewModelOutputFactoryMethod;
import static br.net.du.myequity.controller.util.ViewModelOutputUtils.getTransactionViewModelOutputFactoryMethod;
import static java.util.stream.Collectors.toList;

import br.net.du.myequity.controller.viewmodel.account.AccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.CreditCardAccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.CreditCardTotalsViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.InvestmentAccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.InvestmentTotalsViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.PayableAccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.ReceivableAccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.transaction.TransactionViewModelOutput;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.totals.BalanceUpdateableSubtype;
import br.net.du.myequity.model.totals.CreditCardsTotal;
import br.net.du.myequity.model.totals.InvestmentsTotal;
import br.net.du.myequity.model.totals.SnapshotTotalsCalculator;
import br.net.du.myequity.model.transaction.Transaction;
import br.net.du.myequity.model.transaction.TransactionType;
import br.net.du.myequity.model.util.SnapshotUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;

@Data
@Builder
public class SnapshotViewModelOutput {
    private final Long id;
    private final String name;
    private final boolean newSnapshotAllowed;

    private final String netWorth;
    private final Map<String, String> currencyConversionRates;

    private final String assetsTotal;
    private final String liabilitiesTotal;

    private final String incomeTransactionsTotal;
    private final String investmentTransactionsTotal;
    private final String donationTransactionsTotal;
    private final String taxDeductibleDonationTransactionsTotal;

    private final Long previousId;
    private final String previousName;

    private final Long nextId;
    private final String nextName;

    private final List<AccountViewModelOutput> simpleAssetAccounts;
    private final String simpleAssetsBalance;

    private final List<AccountViewModelOutput> receivableAccounts;
    private final String receivablesBalance;

    private final List<AccountViewModelOutput> investmentAccounts;
    private final InvestmentTotalsViewModelOutput investmentTotals;

    private final List<AccountViewModelOutput> simpleLiabilityAccounts;
    private final String simpleLiabilitiesBalance;

    private final List<AccountViewModelOutput> payableAccounts;
    private final String payablesBalance;

    private final List<AccountViewModelOutput> creditCardAccounts;
    private final Map<String, CreditCardTotalsViewModelOutput> creditCardTotals;

    private final String tithingBalance;

    private final List<TransactionViewModelOutput> incomes;
    private final List<TransactionViewModelOutput> investments;
    private final List<TransactionViewModelOutput> donations;

    public static SnapshotViewModelOutput of(final Snapshot snapshot) {
        final SnapshotTotalsCalculator snapshotTotalsCalculator =
                new SnapshotTotalsCalculator(snapshot);

        final Map<CurrencyUnit, CreditCardsTotal> creditCardTotals =
                snapshotTotalsCalculator.getCreditCardsTotalByCurrency();
        final InvestmentsTotal investmentTotals = snapshotTotalsCalculator.getInvestmentsTotal();

        Long previousId = null;
        String previousName = null;
        final Snapshot previous = snapshot.getPrevious();
        if (previous != null) {
            previousId = previous.getId();
            previousName = getDisplayTitle(previous.getYear(), previous.getMonth());
        }

        Long nextId = null;
        String nextName = null;
        final Snapshot next = snapshot.getNext();
        if (next != null) {
            nextId = next.getId();
            nextName = getDisplayTitle(next.getYear(), next.getMonth());
        }

        final UpdateableTotals updateableTotals = new UpdateableTotals(snapshot);

        final SnapshotViewModelOutputBuilder builder =
                SnapshotViewModelOutput.builder()
                        .id(snapshot.getId())
                        .name(getDisplayTitle(snapshot.getYear(), snapshot.getMonth()))
                        .newSnapshotAllowed(
                                SnapshotUtils.computeNextSnapshotPeriod(snapshot).isPresent())
                        .netWorth(updateableTotals.getNetWorth())
                        .currencyConversionRates(
                                toStringStringMap(snapshot.getCurrencyConversionRates()))
                        .assetsTotal(updateableTotals.getTotalFor(AccountType.ASSET))
                        .simpleAssetsBalance(
                                updateableTotals.getTotalForAccountSubtype(
                                        BalanceUpdateableSubtype.SIMPLE_ASSET))
                        .receivablesBalance(
                                updateableTotals.getTotalForAccountSubtype(
                                        BalanceUpdateableSubtype.RECEIVABLE))
                        .investmentTotals(InvestmentTotalsViewModelOutput.of(investmentTotals))
                        .liabilitiesTotal(updateableTotals.getTotalFor(AccountType.LIABILITY))
                        .simpleLiabilitiesBalance(
                                updateableTotals.getTotalForAccountSubtype(
                                        BalanceUpdateableSubtype.SIMPLE_LIABILITY))
                        .payablesBalance(
                                updateableTotals.getTotalForAccountSubtype(
                                        BalanceUpdateableSubtype.PAYABLE))
                        .creditCardTotals(getCurrencyUnitCreditCardViewModels(creditCardTotals))
                        .incomeTransactionsTotal(
                                updateableTotals.getTotalFor(TransactionType.INCOME))
                        .investmentTransactionsTotal(
                                updateableTotals.getTotalFor(TransactionType.INVESTMENT))
                        .donationTransactionsTotal(
                                updateableTotals.getTotalFor(TransactionType.DONATION))
                        .taxDeductibleDonationTransactionsTotal(
                                updateableTotals.getTaxDeductibleDonationsTotal())
                        .previousId(previousId)
                        .previousName(previousName)
                        .nextId(nextId)
                        .nextName(nextName);

        addAccounts(builder, snapshot);
        addTransactions(builder, snapshot);

        return builder.build();
    }

    public static String getDisplayTitle(final int year, final int month) {
        return String.format("%04d-%02d", year, month);
    }

    private static Map<String, String> toStringStringMap(
            final Map<String, BigDecimal> currencyConversionRates) {
        return currencyConversionRates.entrySet().stream()
                .collect(
                        Collectors.toMap(
                                e -> e.getKey(),
                                e -> format(CurrencyUnit.of(e.getKey()), e.getValue())));
    }

    private static Map<String, CreditCardTotalsViewModelOutput> getCurrencyUnitCreditCardViewModels(
            final Map<CurrencyUnit, CreditCardsTotal> creditCardTotalsByCurrency) {
        final Map<String, CreditCardTotalsViewModelOutput> creditCardTotalsViewModel =
                new HashMap<>();

        for (final CurrencyUnit currencyUnit : creditCardTotalsByCurrency.keySet()) {
            creditCardTotalsViewModel.put(
                    currencyUnit.getCode(),
                    CreditCardTotalsViewModelOutput.of(
                            creditCardTotalsByCurrency.get(currencyUnit)));
        }

        return creditCardTotalsViewModel;
    }

    private static void addAccounts(
            final SnapshotViewModelOutputBuilder builder, final Snapshot snapshot) {
        final Map<AccountType, List<AccountViewModelOutput>> accountViewModels =
                getAccountViewModelOutputs(snapshot);

        final Map<Class, List<AccountViewModelOutput>> assetsByType =
                breakDownAccountsByType(accountViewModels.get(AccountType.ASSET));

        builder.simpleAssetAccounts(assetsByType.get(AccountViewModelOutput.class));
        builder.receivableAccounts(assetsByType.get(ReceivableAccountViewModelOutput.class));
        builder.investmentAccounts(assetsByType.get(InvestmentAccountViewModelOutput.class));

        final Map<Class, List<AccountViewModelOutput>> liabilitiesByType =
                breakDownAccountsByType(accountViewModels.get(AccountType.LIABILITY));
        builder.simpleLiabilityAccounts(liabilitiesByType.get(AccountViewModelOutput.class));
        builder.payableAccounts(liabilitiesByType.get(PayableAccountViewModelOutput.class));
        builder.creditCardAccounts(liabilitiesByType.get(CreditCardAccountViewModelOutput.class));

        builder.tithingBalance(
                format(snapshot.getBaseCurrencyUnit(), snapshot.getTithingBalance()));
    }

    private static Map<AccountType, List<AccountViewModelOutput>> getAccountViewModelOutputs(
            final Snapshot snapshot) {
        final Map<AccountType, SortedSet<Account>> accountsByType = snapshot.getAccountsByType();

        final SortedSet<Account> assetAccounts = accountsByType.get(AccountType.ASSET);
        final SortedSet<Account> liabilityAccounts = accountsByType.get(AccountType.LIABILITY);

        return ImmutableMap.of(
                AccountType.ASSET,
                (assetAccounts == null)
                        ? ImmutableList.of()
                        : getAccountViewModelOutputs(assetAccounts),
                AccountType.LIABILITY,
                (liabilityAccounts == null)
                        ? ImmutableList.of()
                        : getAccountViewModelOutputs(liabilityAccounts));
    }

    private static List<AccountViewModelOutput> getAccountViewModelOutputs(
            final SortedSet<Account> accounts) {
        return accounts.stream()
                .map(
                        account -> {
                            try {
                                final Method factoryMethod =
                                        getAccountViewModelOutputFactoryMethod(account.getClass());
                                return (AccountViewModelOutput) factoryMethod.invoke(null, account);
                            } catch (final Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
                .sorted()
                .collect(toList());
    }

    private static Map<Class, List<AccountViewModelOutput>> breakDownAccountsByType(
            final List<AccountViewModelOutput> accounts) {
        final Map<Class, List<AccountViewModelOutput>> accountsByType = new HashMap<>();

        for (final AccountViewModelOutput account : accounts) {
            final Class key = account.getClass();
            if (!accountsByType.containsKey(key)) {
                accountsByType.put(key, new ArrayList<>());
            }
            accountsByType.get(key).add(account);
        }

        return accountsByType;
    }

    private static void addTransactions(
            final SnapshotViewModelOutputBuilder builder, final Snapshot snapshot) {
        final Map<TransactionType, List<TransactionViewModelOutput>> transactionViewModels =
                getTransactionViewModelOutputs(snapshot);

        builder.incomes(transactionViewModels.get(TransactionType.INCOME));
        builder.investments(transactionViewModels.get(TransactionType.INVESTMENT));
        builder.donations(transactionViewModels.get(TransactionType.DONATION));
    }

    private static Map<TransactionType, List<TransactionViewModelOutput>>
            getTransactionViewModelOutputs(final Snapshot snapshot) {
        final Map<TransactionType, SortedSet<Transaction>> transactionsByType =
                snapshot.getTransactionsByType();

        final SortedSet<Transaction> incomes = transactionsByType.get(TransactionType.INCOME);
        final SortedSet<Transaction> investments =
                transactionsByType.get(TransactionType.INVESTMENT);
        final SortedSet<Transaction> donations = transactionsByType.get(TransactionType.DONATION);

        return ImmutableMap.of(
                TransactionType.INCOME,
                (incomes == null) ? ImmutableList.of() : getTransactionViewModelOutputs(incomes),
                TransactionType.INVESTMENT,
                (investments == null)
                        ? ImmutableList.of()
                        : getTransactionViewModelOutputs(investments),
                TransactionType.DONATION,
                (donations == null)
                        ? ImmutableList.of()
                        : getTransactionViewModelOutputs(donations));
    }

    private static List<TransactionViewModelOutput> getTransactionViewModelOutputs(
            final SortedSet<Transaction> transactions) {

        return transactions.stream()
                .map(
                        transaction -> {
                            try {
                                final Method factoryMethod =
                                        getTransactionViewModelOutputFactoryMethod(
                                                transaction.getClass());
                                return (TransactionViewModelOutput)
                                        factoryMethod.invoke(null, transaction);
                            } catch (final Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
                .sorted()
                .collect(toList());
    }
}
