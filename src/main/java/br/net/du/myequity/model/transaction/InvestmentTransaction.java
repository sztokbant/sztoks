package br.net.du.myequity.model.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue(InvestmentTransaction.TRANSACTION_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class InvestmentTransaction extends Transaction {
    static final String TRANSACTION_TYPE = "INVESTMENT";

    @Transient @Getter
    private final TransactionType transactionType = TransactionType.valueOf(TRANSACTION_TYPE);

    @Column
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private InvestmentCategory investmentCategory;

    public InvestmentTransaction(
            final LocalDate date,
            final String currency,
            final BigDecimal amount,
            final String description,
            final boolean isRecurring,
            final InvestmentCategory investmentCategory) {
        super(date, currency, amount, description, isRecurring);
        this.investmentCategory = investmentCategory;
    }

    @Override
    public InvestmentTransaction copy() {
        return new InvestmentTransaction(
                date, currency, amount, description, isRecurring, investmentCategory);
    }
}
