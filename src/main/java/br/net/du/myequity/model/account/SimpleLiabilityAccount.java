package br.net.du.myequity.model.account;

import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.SimpleLiabilitySnapshot;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.joda.money.CurrencyUnit;

@Entity
@DiscriminatorValue(SimpleLiabilityAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SimpleLiabilityAccount extends Account {

    public static final String ACCOUNT_SUB_TYPE = "SIMPLE_LIABILITY";

    public SimpleLiabilityAccount(
            final String name, final CurrencyUnit currencyUnit, final LocalDate createDate) {
        super(name, AccountType.LIABILITY, currencyUnit, createDate);
    }

    public SimpleLiabilityAccount(final String name, final CurrencyUnit currencyUnit) {
        this(name, currencyUnit, LocalDate.now());
    }

    @Override
    public AccountSnapshot newEmptySnapshot() {
        return new SimpleLiabilitySnapshot(this, BigDecimal.ZERO);
    }
}
