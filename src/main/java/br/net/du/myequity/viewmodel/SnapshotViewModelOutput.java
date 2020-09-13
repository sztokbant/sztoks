package br.net.du.myequity.viewmodel;

import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class SnapshotViewModelOutput {
    private final Long id;
    private final String name;
    private final Map<CurrencyUnit, BigDecimal> netWorth;
    private final Map<CurrencyUnit, BigDecimal> assetsBalance;
    private final Map<CurrencyUnit, BigDecimal> liabilitiesBalance;

    public static SnapshotViewModelOutput of(final Snapshot snapshot) {
        return SnapshotViewModelOutput.builder()
                                      .id(snapshot.getId())
                                      .name(snapshot.getName())
                                      .netWorth(snapshot.getNetWorth())
                                      .assetsBalance(snapshot.getTotalForAccountType(AccountType.ASSET))
                                      .liabilitiesBalance(snapshot.getTotalForAccountType(AccountType.LIABILITY))
                                      .build();
    }
}
