package br.net.du.myequity.viewmodel;

import br.net.du.myequity.model.Account;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class AccountViewModelOutput {
    private final Long id;
    private final String name;
    private final boolean isClosed;
    private final CurrencyUnit balanceCurrencyUnit;
    private final BigDecimal balanceAmount;

    public static AccountViewModelOutput of(final Map.Entry<Account, BigDecimal> entry) {
        final Account account = entry.getKey();
        final BigDecimal amount = entry.getValue();

        return getAccountViewModelBuilderCommon(account).balanceCurrencyUnit(account.getCurrencyUnit())
                                                        .balanceAmount(amount)
                                                        .build();
    }

    public static AccountViewModelOutput of(final Account account) {
        return getAccountViewModelBuilderCommon(account).build();
    }

    private static AccountViewModelOutputBuilder getAccountViewModelBuilderCommon(final Account account) {
        return AccountViewModelOutput.builder()
                                     .id(account.getId())
                                     .name(account.getName())
                                     .isClosed(account.isClosed());
    }
}
