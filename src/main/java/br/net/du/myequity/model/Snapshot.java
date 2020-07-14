package br.net.du.myequity.model;

import br.net.du.myequity.util.NetWorthUtil;
import com.google.common.collect.ImmutableMap;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.joda.money.CurrencyUnit;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "snapshots")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Snapshot {
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
    private LocalDate date;

    @ElementCollection
    @CollectionTable(name = "account_snapshots")
    @MapKeyJoinColumn(name = "account_id")
    @Column(name = "balance_amount", nullable = false)
    private Map<Account, BigDecimal> accounts = new HashMap<>();

    public Snapshot(final LocalDate date, @NotNull final Set<Account> accounts) {
        this.date = date;

        for (final Account account : accounts) {
            this.accounts.put(account, account.getBalance().getAmount());
        }
    }

    public Map<Account, BigDecimal> getAccounts() {
        return ImmutableMap.copyOf(accounts);
    }

    public Map<AccountType, Map<Account, BigDecimal>> getAccountsByType() {
        final Map<AccountType, ImmutableMap.Builder<Account, BigDecimal>> buildersMap = new HashMap<>();

        for (final Map.Entry<Account, BigDecimal> entry : accounts.entrySet()) {
            final AccountType accountType = entry.getKey().getAccountType();

            if (!buildersMap.containsKey(accountType)) {
                buildersMap.put(accountType, ImmutableMap.builder());
            }

            buildersMap.get(accountType).put(entry.getKey(), entry.getValue());
        }

        final ImmutableMap.Builder<AccountType, Map<Account, BigDecimal>> accountsByTypeBuilder =
                ImmutableMap.builder();
        for (final Map.Entry<AccountType, ImmutableMap.Builder<Account, BigDecimal>> entry : buildersMap.entrySet()) {
            accountsByTypeBuilder.put(entry.getKey(), entry.getValue().build());
        }

        return accountsByTypeBuilder.build();
    }

    public BigDecimal getAccount(@NonNull final Account account) {
        return accounts.get(account);
    }

    public void setAccount(@NonNull final Account account) {
        setAccount(account, account.getBalance().getAmount());
    }

    public void setAccount(@NonNull final Account account, @NonNull final BigDecimal balance) {
        accounts.put(account, balance);
    }

    public void removeAccount(@NonNull final Account account) {
        accounts.remove(account);
    }

    public void setWorkspace(final Workspace workspace) {
        // Prevents infinite loop
        if (sameAsFormer(workspace)) {
            return;
        }

        final Workspace oldWorkspace = this.workspace;
        this.workspace = workspace;

        if (oldWorkspace != null) {
            oldWorkspace.removeSnapshot(this);
        }

        if (workspace != null) {
            workspace.addSnapshot(this);
        }
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

        if (!(other instanceof Snapshot)) {
            return false;
        }

        return id != null && id.equals(((Snapshot) other).getId());
    }

    public Map<CurrencyUnit, BigDecimal> getNetWorth() {
        return NetWorthUtil.computeByCurrency(accounts);
    }

    @Override
    public int hashCode() {
        return 43;
    }
}
