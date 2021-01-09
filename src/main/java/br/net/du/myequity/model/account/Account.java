package br.net.du.myequity.model.account;

import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.money.CurrencyUnit;

@Entity
@Table(name = "accounts", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"}))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = Account.DISCRIMINATOR_COLUMN)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class Account implements Comparable<Account> {

    public static final String DISCRIMINATOR_COLUMN = "account_sub_type";

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

    Account(
            final String name,
            final AccountType accountType,
            final CurrencyUnit currencyUnit,
            final LocalDate createDate) {
        this.name = name;
        this.accountType = accountType;
        this.currency = currencyUnit.getCode();
        this.createDate = createDate;
    }

    Account(final String name, final AccountType accountType, final CurrencyUnit currencyUnit) {
        this(name, accountType, currencyUnit, LocalDate.now());
    }

    public abstract AccountSnapshot newEmptySnapshot();

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
        return (user == null) ? (newUser == null) : user.equals(newUser);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Account)) {
            return false;
        }

        return (id != null) && id.equals(((Account) other).getId());
    }

    @Override
    public int hashCode() {
        return 41;
    }

    @Override
    public int compareTo(final Account other) {
        return name.compareTo(other.name);
    }
}
