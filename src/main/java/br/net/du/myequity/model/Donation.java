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
@Table(name = "donations")
public class Donation implements Comparable<Donation> {

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

    @Column @Getter @Setter private boolean isTaxDeductible;

    public Donation(
            final String description,
            final LocalDate date,
            final String currency,
            final BigDecimal amount,
            final boolean isRecurring,
            final boolean isTaxDeductible) {
        this.description = description;
        this.date = date;
        this.currency = currency;
        this.amount = amount;
        this.isRecurring = isRecurring;
        this.isTaxDeductible = isTaxDeductible;
    }

    public Donation copy() {
        return new Donation(description, date, currency, amount, isRecurring, isTaxDeductible);
    }

    @Override
    public int compareTo(final Donation other) {
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

        if (!(other instanceof Donation)) {
            return false;
        }

        final Donation otherDonation = (Donation) other;
        return description.equals(otherDonation.description)
                && date.equals(otherDonation.date)
                && currency.equals(otherDonation.currency)
                && amount.equals(otherDonation.amount)
                && isRecurring == otherDonation.isRecurring
                && isTaxDeductible == otherDonation.isTaxDeductible;
    }
}
