package br.net.du.myequity.service;

import br.net.du.myequity.exception.SztoksException;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.SnapshotSummary;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.totals.CumulativeTransactionTotals;
import br.net.du.myequity.persistence.SnapshotRepository;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SnapshotService {
    private final SnapshotRepository snapshotRepository;

    private final UserService userService;

    @Transactional
    public Snapshot newSnapshot(
            @NonNull final User user, final int snapshotYear, final int snapshotMonth) {
        assert !user.getSnapshots().isEmpty();

        final Snapshot currentSnapshot = user.getSnapshots().first();

        final Snapshot newSnapshot =
                new Snapshot(
                        snapshotYear,
                        snapshotMonth,
                        currentSnapshot.getBaseCurrencyUnit(),
                        currentSnapshot.getDefaultTithingPercentage(),
                        currentSnapshot.getAccounts(),
                        currentSnapshot.getRecurringTransactions(),
                        currentSnapshot.getCurrencyConversionRates());

        user.addSnapshot(newSnapshot);

        snapshotRepository.save(newSnapshot);

        return newSnapshot;
    }

    public Snapshot save(@NonNull final Snapshot snapshot) {
        return snapshotRepository.save(snapshot);
    }

    public void refresh(@NonNull final Snapshot snapshot) {
        snapshotRepository.refresh(snapshot);
    }

    public Optional<Snapshot> findById(@NonNull final Long snapshotId) {
        return snapshotRepository.findById(snapshotId);
    }

    public Optional<Snapshot> findByIdAndUserId(
            @NonNull final Long snapshotId, @NonNull final Long userId) {
        return snapshotRepository.findByIdAndUserId(snapshotId, userId);
    }

    public Snapshot findTopByUser(@NonNull final User user) {
        return snapshotRepository.findTopByUserOrderByYearDescMonthDesc(user);
    }

    public List<SnapshotSummary> findAllSummariesByUser(@NonNull final User user) {
        return snapshotRepository.findAllByUserOrderByYearDescMonthDesc(user);
    }

    public List<CumulativeTransactionTotals> findPastTwelveMonthsCumulativeTransactionTotals(
            @NonNull final Long refSnapshotId, @NonNull final Long userId) {
        return snapshotRepository.findPastTwelveMonthsCumulativeTransactionTotals(
                refSnapshotId, userId);
    }

    public List<CumulativeTransactionTotals> findYearToDateCumulativeTransactionTotals(
            @NonNull final Integer refYear,
            @NonNull final Integer refMonth,
            @NonNull final Long userId) {
        return snapshotRepository.findYearToDateCumulativeTransactionTotals(
                refYear, refMonth, userId);
    }

    @Transactional
    public void deleteSnapshot(@NonNull final User user, @NonNull final Snapshot snapshot) {
        assert user.getSnapshots().contains(snapshot);

        if (user.getSnapshots().size() == 1) {
            throw new SztoksException(
                    "Snapshot cannot be deleted as it is the only remaining snapshot.");
        }

        if (snapshot.getNext() != null) {
            throw new SztoksException("Only the most recent Snapshot can be deleted.");
        }

        snapshot.getAccounts().forEach(account -> snapshot.removeAccount(account));

        snapshot.getTransactions().forEach(transaction -> snapshot.removeTransaction(transaction));

        user.removeSnapshot(snapshot);

        userService.save(user);
    }
}
