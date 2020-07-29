package br.net.du.myequity.viewmodel;

import br.net.du.myequity.model.Account;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class AccountViewModel {
    private final Long id;
    private final String name;
    private final boolean isClosed;
    private final CurrencyUnit balanceCurrencyUnit;
    private final BigDecimal balanceAmount;

    public static AccountViewModel of(final Map.Entry<Account, BigDecimal> entry) {
        final Account account = entry.getKey();
        final BigDecimal amount = entry.getValue();

        return getAccountViewModelBuilderCommon(account).balanceCurrencyUnit(account.getCurrencyUnit())
                                                        .balanceAmount(amount)
                                                        .build();
    }

    public static AccountViewModel of(final Account account) {
        return getAccountViewModelBuilderCommon(account).build();
    }

    private static AccountViewModelBuilder getAccountViewModelBuilderCommon(final Account account) {
        return AccountViewModel.builder().id(account.getId()).name(account.getName()).isClosed(account.isClosed());
    }
}