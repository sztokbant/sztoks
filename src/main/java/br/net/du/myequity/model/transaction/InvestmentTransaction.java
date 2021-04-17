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
            final LocalDate date,
            final String currency,
            final BigDecimal amount,
            final String description,
            final boolean isRecurring,
            final InvestmentCategory category) {
        super(date, currency, amount, description, isRecurring);
        this.category = category.name();
    }

    @Override
    public InvestmentTransaction copy() {
        return new InvestmentTransaction(
                date,
                currency,
                amount,
                description,
                isRecurring,
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
    public void setAmount(final BigDecimal newAmount) {
        final BigDecimal oldAmount = amount;

        amount = newAmount;

        updateSnapshotTransactionTotal(newAmount, oldAmount);
    }
}
