package br.net.du.myequity.service;

import br.net.du.myequity.exception.MyEquityException;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.SnapshotSummary;
import br.net.du.myequity.model.User;
import br.net.du.myequity.persistence.SnapshotRepository;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SnapshotService {
    private final SnapshotRepository snapshotRepository;

    private final UserService userService;

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

    public Optional<Snapshot> findById(@NonNull final Long snapshotId) {
        return snapshotRepository.findById(snapshotId);
    }

    public List<SnapshotSummary> findAllSummariesByUser(@NonNull final User user) {
        return snapshotRepository.findAllByUserOrderByYearDescMonthDesc(user);
    }

    // Used only by automated tests
    public Snapshot findTopBy(@NonNull final User user) {
        return snapshotRepository.findTopByUserOrderByYearDescMonthDesc(user);
    }

    public void deleteSnapshot(@NonNull final User user, @NonNull final Snapshot snapshot) {
        assert user.getSnapshots().contains(snapshot);

        if (user.getSnapshots().size() == 1) {
            throw new MyEquityException(
                    "Snapshot cannot be deleted as it is the only remaining snapshot.");
        }

        snapshot.getAccounts().forEach(account -> snapshot.removeAccount(account));

        snapshot.getTransactions().forEach(transaction -> snapshot.removeTransaction(transaction));

        user.removeSnapshot(snapshot);

        userService.save(user);
    }
}
