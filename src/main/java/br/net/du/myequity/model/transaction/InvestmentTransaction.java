package br.net.du.myequity.model.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@DiscriminatorValue(InvestmentTransaction.TRANSACTION_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class InvestmentTransaction extends Transaction
        implements Categorizable<InvestmentCategory> {
    static final String TRANSACTION_TYPE = "INVESTMENT";

    @Transient @Getter
    private final TransactionType transactionType = TransactionType.valueOf(TRANSACTION_TYPE);

    public InvestmentTransaction(
            @NonNull final LocalDate date,
            @NonNull final String currency,
            @NonNull final BigDecimal amount,
            @NonNull final String description,
            final boolean isRecurring,
            final boolean isResettable,
            @NonNull final InvestmentCategory category) {
        super(date, currency, amount, description, isRecurring, isResettable);
        this.category = category.name();
    }

    @Override
    public InvestmentTransaction copy() {
        return new InvestmentTransaction(
                date,
                currency,
                isResettable ? BigDecimal.ZERO : amount,
                description,
                isRecurring,
                isResettable,
                InvestmentCategory.valueOf(category));
    }

    @Override
    public InvestmentCategory getCategory() {
        return InvestmentCategory.valueOf(category);
    }

    @Override
    public void setCategory(@NonNull final InvestmentCategory category) {
        this.category = category.name();
    }

    @Override
    public void setAmount(@NonNull final BigDecimal newAmount) {
        if (amount.compareTo(newAmount) == 0) {
            return;
        }

        final BigDecimal oldAmount = amount;

        amount = newAmount;

        updateSnapshotTransactionTotal(newAmount, oldAmount);
    }
}
