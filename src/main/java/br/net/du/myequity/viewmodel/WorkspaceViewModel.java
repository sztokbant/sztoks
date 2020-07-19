package br.net.du.myequity.viewmodel;

import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Workspace;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class WorkspaceViewModel {
    private final Long id;
    private final String name;
    private final Map<CurrencyUnit, BigDecimal> netWorth;
    private final Map<CurrencyUnit, BigDecimal> assetsBalance;
    private final Map<CurrencyUnit, BigDecimal> liabilitiesBalance;

    public static WorkspaceViewModel of(final Workspace workspace) {
        return WorkspaceViewModel.builder()
                                 .id(workspace.getId())
                                 .name(workspace.getName())
                                 .netWorth(workspace.getNetWorth())
                                 .assetsBalance(workspace.getTotalForAccountType(AccountType.ASSET))
                                 .liabilitiesBalance(workspace.getTotalForAccountType(AccountType.LIABILITY))
                                 .build();
    }
}
