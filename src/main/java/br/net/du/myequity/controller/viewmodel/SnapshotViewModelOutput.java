package br.net.du.myequity.controller.viewmodel;

import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class SnapshotViewModelOutput {
    private final Long id;
    private final String name;
    private final Map<CurrencyUnit, BigDecimal> netWorth;
    private final Map<CurrencyUnit, BigDecimal> assetsBalance;
    private final Map<CurrencyUnit, BigDecimal> liabilitiesBalance;
    private final Map<String, CreditCardTotalsViewModelOutput> creditCardTotals;

    private final Long previousId;
    private final String previousName;

    private final Long nextId;
    private final String nextName;

    public static SnapshotViewModelOutput of(final Snapshot snapshot) {
        final Map<CurrencyUnit, CreditCardSnapshot> creditCardTotals = snapshot.getCreditCardTotals();

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

        return SnapshotViewModelOutput.builder()
                                      .id(snapshot.getId())
                                      .name(snapshot.getName())
                                      .netWorth(snapshot.getNetWorth())
                                      .assetsBalance(snapshot.getTotalForAccountType(AccountType.ASSET))
                                      .liabilitiesBalance(snapshot.getTotalForAccountType(AccountType.LIABILITY))
                                      .creditCardTotals(getCurrencyUnitCreditCardViewModels(creditCardTotals))
                                      .previousId(previousId)
                                      .previousName(previousName)
                                      .nextId(nextId)
                                      .nextName(nextName)
                                      .build();
    }

    public static Map<String, CreditCardTotalsViewModelOutput> getCurrencyUnitCreditCardViewModels(final Map<CurrencyUnit, CreditCardSnapshot> creditCardTotals) {
        final Map<String, CreditCardTotalsViewModelOutput> creditCardTotalsViewModel = new HashMap<>();

        for (final CurrencyUnit currencyUnit : creditCardTotals.keySet()) {
            creditCardTotalsViewModel.put(currencyUnit.getCode(),
                                          CreditCardTotalsViewModelOutput.of(creditCardTotals.get(currencyUnit)));
        }

        return creditCardTotalsViewModel;
    }
}
