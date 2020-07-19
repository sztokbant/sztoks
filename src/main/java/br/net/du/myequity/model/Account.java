package br.net.du.myequity.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.money.CurrencyUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "accounts")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter // for testing
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    private User user;

    @Column(nullable = false)
    @Getter
    @Setter
    private String name;

    @Column(nullable = false)
    @Getter
    @Setter
    private AccountType accountType;

    @Column(nullable = false)
    private String currency;

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

    public Account(final String name,
                   final AccountType accountType,
                   final CurrencyUnit currencyUnit,
                   final LocalDate createDate) {
        this.name = name;
        this.accountType = accountType;
        this.currency = currencyUnit.getCode();
        this.createDate = createDate;
        this.isClosed = false;
        this.closedDate = null;
    }

    public Account(final String name, final AccountType accountType, final CurrencyUnit currencyUnit) {
        this(name, accountType, currencyUnit, LocalDate.now());
    }

    public CurrencyUnit getCurrencyUnit() {
        return CurrencyUnit.of(currency);
    }

    public void setCurrencyUnit(final CurrencyUnit currencyUnit) {
        this.currency = currencyUnit.getCode();
    }

    public void setUser(final User user) {
        // Prevents infinite loop
        if (sameAsFormer(user)) {
            return;
        }

        final User oldUser = this.user;
        this.user = user;

        if (oldUser != null) {
            oldUser.removeAccount(this);
        }

        if (user != null) {
            user.addAccount(this);
        }
    }

    private boolean sameAsFormer(final User newUser) {
        return user == null ?
                newUser == null :
                user.equals(newUser);
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
