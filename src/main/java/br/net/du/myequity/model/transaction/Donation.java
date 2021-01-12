package br.net.du.myequity.model.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue(Donation.TRANSACTION_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Donation extends Transaction {
    static final String TRANSACTION_TYPE = "DONATION";

    @Column @Getter @Setter private boolean isTaxDeductible;

    public Donation(
            final LocalDate date,
            final String currency,
            final BigDecimal amount,
            final String description,
            final boolean isRecurring,
            final boolean isTaxDeductible) {
        super(date, currency, amount, description, isRecurring);
        this.isTaxDeductible = isTaxDeductible;
    }

    @Override
    public Donation copy() {
        return new Donation(date, currency, amount, description, isRecurring, isTaxDeductible);
    }

    @Override
    public boolean equalsIgnoreId(final Object other) {
        return super.equalsIgnoreId(other)
                && (other instanceof Donation)
                && isTaxDeductible == ((Donation) other).isTaxDeductible();
    }
}
