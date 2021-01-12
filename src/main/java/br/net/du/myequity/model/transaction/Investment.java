package br.net.du.myequity.model.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue(Investment.TRANSACTION_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Investment extends Transaction {
    static final String TRANSACTION_TYPE = "INVESTMENT";

    @Column
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private InvestmentCategory investmentCategory;

    public Investment(
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
    public Investment copy() {
        return new Investment(date, currency, amount, description, isRecurring, investmentCategory);
    }

    @Override
    public boolean equalsIgnoreId(final Object other) {
        return super.equalsIgnoreId(other)
                && (other instanceof Investment)
                && investmentCategory.equals(((Investment) other).getInvestmentCategory());
    }
}
