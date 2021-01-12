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
@DiscriminatorValue(Income.TRANSACTION_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Income extends Transaction {
    static final String TRANSACTION_TYPE = "INCOME";

    @Column @Getter @Setter private BigDecimal donationRatio;

    public Income(
            final LocalDate date,
            final String currency,
            final BigDecimal amount,
            final String description,
            final boolean isRecurring,
            final BigDecimal donationRatio) {
        super(date, currency, amount, description, isRecurring);
        this.donationRatio = donationRatio;
    }

    @Override
    public Income copy() {
        return new Income(date, currency, amount, description, isRecurring, donationRatio);
    }

    @Override
    public boolean equalsIgnoreId(final Object other) {
        return super.equalsIgnoreId(other)
                && (other instanceof Income)
                && donationRatio.equals(((Income) other).getDonationRatio());
    }
}
