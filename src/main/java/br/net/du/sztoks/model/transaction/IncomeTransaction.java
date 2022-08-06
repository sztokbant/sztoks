package br.net.du.sztoks.model.transaction;

import static br.net.du.sztoks.model.util.ModelConstants.DIVISION_SCALE;
import static br.net.du.sztoks.model.util.ModelConstants.ONE_HUNDRED;

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
import lombok.NonNull;

@Entity
@DiscriminatorValue(IncomeTransaction.TRANSACTION_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class IncomeTransaction extends Transaction implements Categorizable<IncomeCategory> {
    public static final String TRANSACTION_TYPE = "INCOME";

    @Transient @Getter
    private final TransactionType transactionType = TransactionType.valueOf(TRANSACTION_TYPE);

    @Column @Getter private BigDecimal tithingPercentage;

    public IncomeTransaction(
            @NonNull final LocalDate date,
            @NonNull final String currency,
            @NonNull final BigDecimal amount,
            @NonNull final String description,
            @NonNull final RecurrencePolicy recurrencePolicy,
            @NonNull final BigDecimal tithingPercentage,
            @NonNull final IncomeCategory incomeCategory) {
        super(date, currency, amount, description, recurrencePolicy);
        this.tithingPercentage = tithingPercentage;
        category = incomeCategory.name();
    }

    public BigDecimal getTithingAmount() {
        return getTithingPercentage()
                .multiply(amount)
                .divide(ONE_HUNDRED, DIVISION_SCALE, RoundingMode.HALF_UP);
    }

    @Override
    public IncomeTransaction copy() {
        return new IncomeTransaction(
                date,
                currency,
                recurrencePolicy.equals(RecurrencePolicy.RESETTABLE) ? BigDecimal.ZERO : amount,
                description,
                recurrencePolicy,
                tithingPercentage,
                IncomeCategory.valueOf(category));
    }

    @Override
    public void setAmount(@NonNull final BigDecimal newAmount) {
        if (amount.compareTo(newAmount) == 0) {
            return;
        }

        final BigDecimal oldAmount = getAmount();
        final BigDecimal oldTithingAmount = getTithingAmount();

        amount = newAmount;

        final BigDecimal newTithingAmount = getTithingAmount();

        final BigDecimal diffTithingAmount = newTithingAmount.subtract(oldTithingAmount);
        getSnapshot().updateTithingAmount(getCurrencyUnit(), diffTithingAmount);

        updateSnapshotTransactionTotal(newAmount, oldAmount);
    }

    public void setTithingPercentage(@NonNull final BigDecimal tithingPercentage) {
        if (this.tithingPercentage.compareTo(tithingPercentage) == 0) {
            return;
        }

        if (tithingPercentage.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "tithingPercentage must be greater than or equal to zero");
        }

        final BigDecimal oldTithingAmount = getTithingAmount();

        this.tithingPercentage = tithingPercentage;

        final BigDecimal newTithingAmount = getTithingAmount();

        final BigDecimal diffTithingAmount = newTithingAmount.subtract(oldTithingAmount);
        getSnapshot().updateTithingAmount(getCurrencyUnit(), diffTithingAmount);
    }

    @Override
    public IncomeCategory getCategory() {
        return IncomeCategory.valueOf(category);
    }

    @Override
    public void setCategory(@NonNull final IncomeCategory category) {
        this.category = category.name();
    }
}
