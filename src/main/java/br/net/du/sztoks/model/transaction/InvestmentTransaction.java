package br.net.du.sztoks.model.transaction;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@DiscriminatorValue(InvestmentTransaction.TRANSACTION_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class InvestmentTransaction extends Transaction
        implements Categorizable<InvestmentCategory> {
    public static final String TRANSACTION_TYPE = "INVESTMENT";

    @Transient @Getter
    private final TransactionType transactionType = TransactionType.valueOf(TRANSACTION_TYPE);

    public InvestmentTransaction(
            @NonNull final LocalDate date,
            @NonNull final String currency,
            @NonNull final BigDecimal amount,
            @NonNull final String description,
            @NonNull final RecurrencePolicy recurrencePolicy,
            @NonNull final InvestmentCategory category) {
        super(date, currency, amount, description, recurrencePolicy);
        this.category = category.name();
    }

    @Override
    public InvestmentTransaction copy() {
        return new InvestmentTransaction(
                date,
                currency,
                recurrencePolicy.equals(RecurrencePolicy.RESETTABLE) ? BigDecimal.ZERO : amount,
                description,
                recurrencePolicy,
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
