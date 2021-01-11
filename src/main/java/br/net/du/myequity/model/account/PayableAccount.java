package br.net.du.myequity.model.account;

import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.PayableSnapshot;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.joda.money.CurrencyUnit;

@Entity
@DiscriminatorValue(PayableAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PayableAccount extends Account {

    public static final String ACCOUNT_SUB_TYPE = "PAYABLE";

    public PayableAccount(
            final String name, final CurrencyUnit currencyUnit, final LocalDate createDate) {
        super(name, AccountType.LIABILITY, currencyUnit, createDate);
    }

    public PayableAccount(final String name, final CurrencyUnit currencyUnit) {
        this(name, currencyUnit, LocalDate.now());
    }

    @Override
    public AccountSnapshot newEmptySnapshot() {
        return new PayableSnapshot(this, LocalDate.now(), BigDecimal.ZERO);
    }
}
