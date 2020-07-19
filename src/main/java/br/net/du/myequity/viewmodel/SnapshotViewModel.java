package br.net.du.myequity.viewmodel;

import br.net.du.myequity.model.Snapshot;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class SnapshotViewModel {
    private final Long id;
    private final LocalDate date;
    private final Map<CurrencyUnit, BigDecimal> netWorth;
    private final Map<CurrencyUnit, BigDecimal> assetsBalance;
    private final Map<CurrencyUnit, BigDecimal> liabilitiesBalance;

    public static SnapshotViewModel of(final Snapshot snapshot) {
        return SnapshotViewModel.builder()
                                .id(snapshot.getId())
                                .date(snapshot.getDate())
                                .netWorth(snapshot.getNetWorth())
                                .assetsBalance(snapshot.getAssetsTotal())
                                .liabilitiesBalance(snapshot.getLiabilitiesTotal())
                                .build();
    }
}
