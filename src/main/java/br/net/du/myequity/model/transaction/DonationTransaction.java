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
import lombok.NonNull;

@Entity
@DiscriminatorValue(DonationTransaction.TRANSACTION_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class DonationTransaction extends Transaction implements Categorizable<DonationCategory> {
    static final String TRANSACTION_TYPE = "DONATION";

    @Transient @Getter
    private final TransactionType transactionType = TransactionType.valueOf(TRANSACTION_TYPE);

    @Column @Getter private boolean isTaxDeductible;

    public DonationTransaction(
            @NonNull final LocalDate date,
            @NonNull final String currency,
            @NonNull final BigDecimal amount,
            @NonNull final String description,
            final boolean isRecurring,
            final boolean isResettable,
            final boolean isTaxDeductible,
            @NonNull final DonationCategory category) {
        super(date, currency, amount, description, isRecurring, isResettable);
        this.isTaxDeductible = isTaxDeductible;
        this.category = category.name();
    }

    @Override
    public DonationTransaction copy() {
        return new DonationTransaction(
                date,
                currency,
                isResettable ? BigDecimal.ZERO : amount,
                description,
                isRecurring,
                isResettable,
                isTaxDeductible,
                DonationCategory.valueOf(category));
    }

    @Override
    public void setAmount(@NonNull final BigDecimal newAmount) {
        if (amount.compareTo(newAmount) == 0) {
            return;
        }

        final BigDecimal oldAmount = amount;

        amount = newAmount;

        final BigDecimal diffTithingAmount = oldAmount.subtract(newAmount);
        getSnapshot().updateTithingAmount(getCurrencyUnit(), diffTithingAmount);

        updateSnapshotTransactionTotal(newAmount, oldAmount, isTaxDeductible);
    }

    public void setTaxDeductible(final boolean isTaxDeductible) {
        if (this.isTaxDeductible == isTaxDeductible) {
            return;
        }

        this.isTaxDeductible = isTaxDeductible;

        final BigDecimal diffTaxDeductibleAmount = isTaxDeductible ? amount : amount.negate();
        getSnapshot().updateTaxDeductibleDonationsTotal(getCurrencyUnit(), diffTaxDeductibleAmount);
    }

    @Override
    public DonationCategory getCategory() {
        return DonationCategory.valueOf(category);
    }

    @Override
    public void setCategory(@NonNull final DonationCategory category) {
        this.category = category.name();
    }
}
