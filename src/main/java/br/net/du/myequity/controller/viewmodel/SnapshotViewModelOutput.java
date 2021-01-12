package br.net.du.myequity.controller.viewmodel;

import static br.net.du.myequity.controller.util.ViewModelOutputUtils.getViewModelOutputFactoryMethod;
import static java.util.stream.Collectors.toList;

import br.net.du.myequity.controller.util.MoneyFormatUtils;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.AccountSnapshotViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.CreditCardViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.InvestmentViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.PayableViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.ReceivableViewModelOutput;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;

@Data
@Builder
public class SnapshotViewModelOutput {
    private final Long id;
    private final String name;
    private final Map<CurrencyUnit, String> netWorth;
    private final Map<CurrencyUnit, String> assetsBalance;
    private final Map<CurrencyUnit, String> liabilitiesBalance;
    private final Map<String, CreditCardTotalsViewModelOutput> creditCardTotals;

    private final Long previousId;
    private final String previousName;

    private final Long nextId;
    private final String nextName;

    private final List<AccountSnapshotViewModelOutput> simpleAssetAccounts;
    private final List<AccountSnapshotViewModelOutput> receivableAccounts;
    private final List<AccountSnapshotViewModelOutput> investmentAccounts;
    private final List<AccountSnapshotViewModelOutput> simpleLiabilityAccounts;
    private final List<AccountSnapshotViewModelOutput> payableAccounts;
    private final List<AccountSnapshotViewModelOutput> creditCardAccounts;

    public static SnapshotViewModelOutput of(final Snapshot snapshot) {
        final Map<CurrencyUnit, CreditCardSnapshot> creditCardTotals =
                snapshot.getCreditCardTotals();

        Long previousId = null;
        String previousName = null;
        final Snapshot previous = snapshot.getPrevious();
        if (previous != null) {
            previousId = previous.getId();
            previousName = previous.getName();
        }

        Long nextId = null;
        String nextName = null;
        final Snapshot next = snapshot.getNext();
        if (next != null) {
            nextId = next.getId();
            nextName = next.getName();
        }

        final SnapshotViewModelOutputBuilder builder =
                SnapshotViewModelOutput.builder()
                        .id(snapshot.getId())
                        .name(snapshot.getName())
                        .netWorth(formatForCurrency(snapshot.getNetWorth()))
                        .assetsBalance(
                                formatForCurrency(
                                        snapshot.getTotalForAccountType(AccountType.ASSET)))
                        .liabilitiesBalance(
                                formatForCurrency(
                                        snapshot.getTotalForAccountType(AccountType.LIABILITY)))
                        .creditCardTotals(getCurrencyUnitCreditCardViewModels(creditCardTotals))
                        .previousId(previousId)
                        .previousName(previousName)
                        .nextId(nextId)
                        .nextName(nextName);

        addAccounts(builder, snapshot);

        return builder.build();
    }

    private static Map<CurrencyUnit, String> formatForCurrency(
            final Map<CurrencyUnit, BigDecimal> input) {
        final Map<CurrencyUnit, String> formattedForCurrency = new HashMap<>();

        for (final CurrencyUnit currencyUnit : input.keySet()) {
            formattedForCurrency.put(
                    currencyUnit, MoneyFormatUtils.format(currencyUnit, input.get(currencyUnit)));
        }

        return formattedForCurrency;
    }

    public static Map<String, CreditCardTotalsViewModelOutput> getCurrencyUnitCreditCardViewModels(
            final Map<CurrencyUnit, CreditCardSnapshot> creditCardTotals) {
        final Map<String, CreditCardTotalsViewModelOutput> creditCardTotalsViewModel =
                new HashMap<>();

        for (final CurrencyUnit currencyUnit : creditCardTotals.keySet()) {
            creditCardTotalsViewModel.put(
                    currencyUnit.getCode(),
                    CreditCardTotalsViewModelOutput.of(creditCardTotals.get(currencyUnit)));
        }

        return creditCardTotalsViewModel;
    }

    private static void addAccounts(
            final SnapshotViewModelOutputBuilder builder, final Snapshot snapshot) {
        final Map<AccountType, List<AccountSnapshotViewModelOutput>> accountSnapshotViewModels =
                getAccountSnapshotViewModelOutputs(snapshot);

        final Map<Class, List<AccountSnapshotViewModelOutput>> assetsByType =
                breakDownAccountsByType(accountSnapshotViewModels.get(AccountType.ASSET));

        builder.simpleAssetAccounts(assetsByType.get(AccountSnapshotViewModelOutput.class));
        builder.receivableAccounts(assetsByType.get(ReceivableViewModelOutput.class));
        builder.investmentAccounts(assetsByType.get(InvestmentViewModelOutput.class));

        final Map<Class, List<AccountSnapshotViewModelOutput>> liabilitiesByType =
                breakDownAccountsByType(accountSnapshotViewModels.get(AccountType.LIABILITY));
        builder.simpleLiabilityAccounts(
                liabilitiesByType.get(AccountSnapshotViewModelOutput.class));
        builder.payableAccounts(liabilitiesByType.get(PayableViewModelOutput.class));
        builder.creditCardAccounts(liabilitiesByType.get(CreditCardViewModelOutput.class));
    }

    private static Map<AccountType, List<AccountSnapshotViewModelOutput>>
            getAccountSnapshotViewModelOutputs(final Snapshot snapshot) {
        final Map<AccountType, SortedSet<AccountSnapshot>> accountSnapshotsByType =
                snapshot.getAccountSnapshotsByType();

        final SortedSet<AccountSnapshot> assetAccountSnapshots =
                accountSnapshotsByType.get(AccountType.ASSET);
        final SortedSet<AccountSnapshot> liabilityAccountSnapshots =
                accountSnapshotsByType.get(AccountType.LIABILITY);

        return ImmutableMap.of(
                AccountType.ASSET,
                (assetAccountSnapshots == null)
                        ? ImmutableList.of()
                        : getViewModelOutputs(assetAccountSnapshots),
                AccountType.LIABILITY,
                (liabilityAccountSnapshots == null)
                        ? ImmutableList.of()
                        : getViewModelOutputs(liabilityAccountSnapshots));
    }

    private static List<AccountSnapshotViewModelOutput> getViewModelOutputs(
            final SortedSet<AccountSnapshot> accountSnapshots) {
        return accountSnapshots.stream()
                .map(
                        accountSnapshot -> {
                            try {
                                final Method factoryMethod =
                                        getViewModelOutputFactoryMethod(accountSnapshot.getClass());
                                return (AccountSnapshotViewModelOutput)
                                        factoryMethod.invoke(null, accountSnapshot);
                            } catch (final Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
                .sorted()
                .collect(toList());
    }

    private static Map<Class, List<AccountSnapshotViewModelOutput>> breakDownAccountsByType(
            final List<AccountSnapshotViewModelOutput> accounts) {
        final Map<Class, List<AccountSnapshotViewModelOutput>> accountsByType = new HashMap<>();

        for (final AccountSnapshotViewModelOutput account : accounts) {
            final Class key = account.getClass();
            if (!accountsByType.containsKey(key)) {
                accountsByType.put(key, new ArrayList<>());
            }
            accountsByType.get(key).add(account);
        }

        return accountsByType;
    }
}
