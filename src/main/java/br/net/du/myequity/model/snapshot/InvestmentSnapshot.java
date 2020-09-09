package br.net.du.myequity.model.snapshot;

import br.net.du.myequity.model.account.Account;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "investment_snapshots")
@PrimaryKeyJoinColumn(name = "id")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class InvestmentSnapshot extends AccountSnapshot {

    @Column(nullable = false)
    @Getter
    @Setter
    private BigDecimal shares;

    @Column(nullable = false)
    @Getter
    @Setter
    private BigDecimal originalShareValue;

    @Column(nullable = false)
    @Getter
    @Setter
    private BigDecimal currentShareValue;

    public InvestmentSnapshot(final Account account,
                              final BigDecimal shares,
                              final BigDecimal originalShareValue,
                              final BigDecimal currentShareValue) {
        super(account);
        this.shares = shares;
        this.originalShareValue = originalShareValue;
        this.currentShareValue = currentShareValue;
    }

    @Override
    public BigDecimal getTotal() {
        return shares.multiply(currentShareValue);
    }

    public BigDecimal getProfitPercentage() {
        if (originalShareValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        final BigDecimal oneHundred = new BigDecimal("100.00");
        return (currentShareValue.multiply(oneHundred).divide(originalShareValue, RoundingMode.HALF_UP)).subtract(
                oneHundred);
    }
}
