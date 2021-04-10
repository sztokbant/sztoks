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

@Entity
@DiscriminatorValue(DonationTransaction.TRANSACTION_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class DonationTransaction extends Transaction {
    static final String TRANSACTION_TYPE = "DONATION";

    @Transient @Getter
    private final TransactionType transactionType = TransactionType.valueOf(TRANSACTION_TYPE);

    @Column @Getter private boolean isTaxDeductible;

    public DonationTransaction(
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
    public DonationTransaction copy() {
        return new DonationTransaction(
                date, currency, amount, description, isRecurring, isTaxDeductible);
    }

    @Override
    public void setAmount(final BigDecimal newAmount) {
        final BigDecimal oldAmount = amount;

        amount = newAmount;

        final BigDecimal diffTithingAmount = oldAmount.subtract(newAmount);
        getSnapshot().updateTithingAmount(getCurrencyUnit(), diffTithingAmount);

        updateSnapshotTransactionTotal(newAmount, oldAmount, isTaxDeductible);
    }

    public void setTaxDeductible(final boolean isTaxDeductible) {
        this.isTaxDeductible = isTaxDeductible;

        final BigDecimal diffTaxDeductibleAmount = isTaxDeductible ? amount : amount.negate();
        getSnapshot().updateTaxDeductibleDonationsTotal(getCurrencyUnit(), diffTaxDeductibleAmount);
    }
}
