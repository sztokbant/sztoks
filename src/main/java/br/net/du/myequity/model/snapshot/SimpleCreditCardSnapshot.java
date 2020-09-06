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

@Entity
@Table(name = "credit_card_snapshots")
@PrimaryKeyJoinColumn(name = "id")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SimpleCreditCardSnapshot extends AccountSnapshot {

    @Column(nullable = false)
    @Getter
    @Setter
    private BigDecimal totalCredit;

    @Column(nullable = false)
    @Getter
    @Setter
    private BigDecimal availableCredit;

    public SimpleCreditCardSnapshot(final Account account,
                                    final BigDecimal totalCredit,
                                    final BigDecimal availableCredit) {
        super(account);
        this.totalCredit = totalCredit;
        this.availableCredit = availableCredit;
    }

    @Override
    public BigDecimal getTotal() {
        return totalCredit.subtract(availableCredit);
    }
}
