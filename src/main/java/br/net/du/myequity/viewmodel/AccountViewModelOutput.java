package br.net.du.myequity.viewmodel;

import br.net.du.myequity.model.Account;
import br.net.du.myequity.model.AccountSnapshot;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;

// TODO This class should be split into Asset and Liability
@Data
@Builder
public class AccountViewModelOutput implements Comparable<AccountViewModelOutput> {
    private final Long id;
    private final String name;
    private final boolean isClosed;
    private final CurrencyUnit balanceCurrencyUnit;
    private final BigDecimal total;

    public static AccountViewModelOutput of(final AccountSnapshot accountSnapshot) {
        final Account account = accountSnapshot.getAccount();

        return getAccountViewModelBuilderCommon(account).balanceCurrencyUnit(account.getCurrencyUnit())
                                                        .total(accountSnapshot.getTotal())
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

    @Override
    public int compareTo(final AccountViewModelOutput other) {
        return this.balanceCurrencyUnit.equals(other.getBalanceCurrencyUnit()) ?
                this.name.compareTo(other.name) :
                this.balanceCurrencyUnit.compareTo(other.getBalanceCurrencyUnit());
    }
}
