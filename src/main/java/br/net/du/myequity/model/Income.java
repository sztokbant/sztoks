package br.net.du.myequity.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "incomes")
public class Income implements Comparable<Income> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Snapshot snapshot;

    @Column(nullable = false)
    @Getter
    @Setter
    private String description;

    @Column @Getter @Setter private LocalDate date;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    @Getter
    @Setter
    private BigDecimal amount;

    @Column @Getter @Setter private boolean isRecurring;

    @Column @Getter @Setter private BigDecimal donationRatio;

    public Income(
            final String description,
            final LocalDate date,
            final String currency,
            final BigDecimal amount,
            final boolean isRecurring,
            final BigDecimal donationRatio) {
        this.description = description;
        this.date = date;
        this.currency = currency;
        this.amount = amount;
        this.isRecurring = isRecurring;
        this.donationRatio = donationRatio;
    }

    public Income copy() {
        return new Income(description, date, currency, amount, isRecurring, donationRatio);
    }

    @Override
    public int compareTo(final Income other) {
        if (currency.equals(other.currency)) {
            if (date.equals(other.date)) {
                return description.compareTo(other.description);
            }
            return date.compareTo(other.date);
        }
        return currency.compareTo(other.currency);
    }

    public boolean equalsIgnoreId(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Income)) {
            return false;
        }

        final Income otherIncome = (Income) other;
        return description.equals(otherIncome.description)
                && date.equals(otherIncome.date)
                && currency.equals(otherIncome.currency)
                && amount.equals(otherIncome.amount)
                && isRecurring == otherIncome.isRecurring
                && donationRatio.equals(otherIncome.donationRatio);
    }
}
