package br.net.du.myequity.viewmodel;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;

@AllArgsConstructor
@Data
@Builder
public class SimpleAccountViewModelOutput implements Comparable<SimpleAccountViewModelOutput> {
    private final Long id;
    private final String name;
    private final boolean isClosed;
    private final String currencyUnit;
    private final String currencyUnitSymbol;
    private final BigDecimal total;

    public SimpleAccountViewModelOutput(final SimpleAccountViewModelOutput other) {
        this.id = other.getId();
        this.name = other.getName();
        this.isClosed = other.isClosed();
        this.currencyUnit = other.getCurrencyUnit();
        this.currencyUnitSymbol = other.getCurrencyUnitSymbol();
        this.total = other.getTotal();
    }

    public static SimpleAccountViewModelOutput of(final AccountSnapshot accountSnapshot) {
        final Account account = accountSnapshot.getAccount();

        final BigDecimal total = new BigDecimal(formatAsDecimal(accountSnapshot.getTotal()));
        return getAccountViewModelBuilderCommon(account).currencyUnit(account.getCurrencyUnit().getCode())
                                                        .currencyUnitSymbol(account.getCurrencyUnit().getSymbol())
                                                        .total(total)
                                                        .build();
    }

    public static SimpleAccountViewModelOutput of(final Account account) {
        return getAccountViewModelBuilderCommon(account).build();
    }

    static SimpleAccountViewModelOutputBuilder getAccountViewModelBuilderCommon(final Account account) {
        return SimpleAccountViewModelOutput.builder()
                                           .id(account.getId())
                                           .name(account.getName())
                                           .isClosed(account.isClosed());
    }

    @Override
    public int compareTo(final SimpleAccountViewModelOutput other) {
        return this.currencyUnit.equals(other.getCurrencyUnit()) ?
                this.name.compareTo(other.name) :
                this.currencyUnit.compareTo(other.getCurrencyUnit());
    }
}
