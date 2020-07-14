package br.net.du.myequity.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "accounts")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter(AccessLevel.PACKAGE) // for testing
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    private Workspace workspace;

    @Column(nullable = false)
    @Getter
    @Setter
    private String name;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private BigDecimal amount;

    @Transient
    private Money balance;

    @Column(nullable = false)
    @Getter
    @Setter
    private AccountType accountType;

    @Column(nullable = false)
    @Getter
    @Setter
    private LocalDate createDate;

    @Column(nullable = false)
    @Getter
    @Setter
    private boolean isClosed;

    @Getter
    @Setter
    private LocalDate closedDate;

    @Getter
    @Setter
    private String category;

    public Account(final String name, final AccountType accountType, final Money balance, final LocalDate createDate) {
        this.name = name;
        this.accountType = accountType;
        setBalance(balance);
        this.createDate = createDate;
        this.isClosed = false;
        this.closedDate = null;
        this.category = StringUtils.EMPTY;
    }

    public Account(final String name, final AccountType accountType, final Money balance) {
        this(name, accountType, balance, LocalDate.now());
    }

    public Account(final String name, final AccountType accountType, final CurrencyUnit currency) {
        this(name, accountType, Money.of(currency, BigDecimal.ZERO));
    }

    public void setWorkspace(final Workspace workspace) {
        // Prevents infinite loop
        if (sameAsFormer(workspace)) {
            return;
        }

        final Workspace oldWorkspace = this.workspace;
        this.workspace = workspace;

        if (oldWorkspace != null) {
            oldWorkspace.removeAccount(this);
        }

        if (workspace != null) {
            workspace.addAccount(this);
        }
    }

    public Money getBalance() {
        if (balance == null) {
            balance = Money.of(CurrencyUnit.of(currency), amount);
        }
        return balance;
    }

    public void setBalance(@NonNull final Money balance) {
        this.currency = balance.getCurrencyUnit().getCode();
        this.amount = balance.getAmount();
    }

    public void setBalanceAmount(@NonNull final BigDecimal amount) {
        this.amount = amount;
    }

    private boolean sameAsFormer(final Workspace newWorkspace) {
        return workspace == null ?
                newWorkspace == null :
                workspace.equals(newWorkspace);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Account)) {
            return false;
        }

        return id != null && id.equals(((Account) other).getId());
    }

    @Override
    public int hashCode() {
        return 41;
    }
}
