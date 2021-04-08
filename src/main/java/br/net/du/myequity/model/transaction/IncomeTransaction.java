package br.net.du.myequity.model.transaction;

import static br.net.du.myequity.model.util.ModelConstants.DIVISION_SCALE;
import static br.net.du.myequity.model.util.ModelConstants.ONE_HUNDRED;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @Column @Getter @Setter private BigDecimal tithingPercentage;

    public IncomeTransaction(
            final LocalDate date,
            final String currency,
            final BigDecimal amount,
            final String description,
            final boolean isRecurring,
            final BigDecimal tithingPercentage) {
        super(date, currency, amount, description, isRecurring);
        this.tithingPercentage = tithingPercentage;
    }

    public BigDecimal getTithingAmount() {
        return getTithingPercentage()
                .multiply(amount)
                .divide(ONE_HUNDRED, DIVISION_SCALE, RoundingMode.HALF_UP);
    }

    @Override
    public IncomeTransaction copy() {
        return new IncomeTransaction(
                date, currency, amount, description, isRecurring, tithingPercentage);
    }
}
