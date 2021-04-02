package br.net.du.myequity.model.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue(IncomeTransaction.TRANSACTION_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class IncomeTransaction extends Transaction {
    static final String TRANSACTION_TYPE = "INCOME";

    @Transient @Getter
    private final TransactionType transactionType = TransactionType.valueOf(TRANSACTION_TYPE);

    @Column @Getter @Setter private BigDecimal donationRatio;

    public IncomeTransaction(
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
    public IncomeTransaction copy() {
        return new IncomeTransaction(
                date, currency, amount, description, isRecurring, donationRatio);
    }
}
