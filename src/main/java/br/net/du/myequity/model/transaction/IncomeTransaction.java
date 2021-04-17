package br.net.du.myequity.model.transaction;

import static br.net.du.myequity.model.util.ModelConstants.DIVISION_SCALE;
import static br.net.du.myequity.model.util.ModelConstants.ONE_HUNDRED;

import br.net.du.myequity.model.account.TithingAccount;
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
    static final String TRANSACTION_TYPE = "INCOME";

    @Transient @Getter
    private final TransactionType transactionType = TransactionType.valueOf(TRANSACTION_TYPE);

    @Column @Getter private BigDecimal tithingPercentage;

    public IncomeTransaction(
            final LocalDate date,
            final String currency,
            final BigDecimal amount,
            final String description,
            final boolean isRecurring,
            final BigDecimal tithingPercentage,
            final IncomeCategory incomeCategory) {
        super(date, currency, amount, description, isRecurring);
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
                amount,
                description,
                isRecurring,
                tithingPercentage,
                IncomeCategory.valueOf(category));
    }

    @Override
    public void setAmount(final BigDecimal newAmount) {
        final BigDecimal oldAmount = getAmount();
        final BigDecimal oldTithingAmount = getTithingAmount();

        amount = newAmount;

        final BigDecimal newTithingAmount = getTithingAmount();

        final BigDecimal diffTithingAmount = newTithingAmount.subtract(oldTithingAmount);
        getSnapshot()
                .updateTithingAmount(getCurrencyUnit(), diffTithingAmount, TithingAccount.class);

        updateSnapshotTransactionTotal(newAmount, oldAmount);
    }

    public void setTithingPercentage(final BigDecimal tithingPercentage) {
        if (tithingPercentage.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "tithingPercentage must be greater than or equal to zero");
        }

        final BigDecimal oldTithingAmount = getTithingAmount();

        this.tithingPercentage = tithingPercentage;

        final BigDecimal newTithingAmount = getTithingAmount();

        final BigDecimal diffTithingAmount = newTithingAmount.subtract(oldTithingAmount);
        getSnapshot()
                .updateTithingAmount(getCurrencyUnit(), diffTithingAmount, TithingAccount.class);
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
