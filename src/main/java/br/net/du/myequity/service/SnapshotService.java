package br.net.du.myequity.service;

import br.net.du.myequity.exception.MyEquityException;
import br.net.du.myequity.model.Snapshot;
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

    public Snapshot newSnapshot(@NonNull final User user, @NonNull final String snapshotName) {
        assert !user.getSnapshots().isEmpty();

        final Snapshot currentSnapshot = user.getSnapshots().first();

        final Snapshot newSnapshot =
                new Snapshot(
                        currentSnapshot.getIndex() + 1,
                        snapshotName,
                        currentSnapshot.getAccounts(),
                        currentSnapshot.getRecurringTransactions());

        currentSnapshot.setNext(newSnapshot);
        newSnapshot.setPrevious(currentSnapshot);

        user.addSnapshot(newSnapshot);

        snapshotRepository.save(currentSnapshot);
        snapshotRepository.save(newSnapshot);

        return newSnapshot;
    }

    public Snapshot save(@NonNull final Snapshot snapshot) {
        return snapshotRepository.save(snapshot);
    }

    public Optional<Snapshot> findById(@NonNull final Long snapshotId) {
        return snapshotRepository.findById(snapshotId);
    }

    public List<Snapshot> findAllByUser(@NonNull final User user) {
        return snapshotRepository.findAllByUser(user);
    }

    public void deleteSnapshot(@NonNull final User user, @NonNull final Snapshot snapshot) {
        assert user.getSnapshots().contains(snapshot);

        if (user.getSnapshots().size() == 1) {
            throw new MyEquityException(
                    "Snapshot cannot be deleted as it is the only remaining snapshot.");
        }

        snapshot.getAccounts().forEach(account -> snapshot.removeAccount(account));

        snapshot.getTransactions().forEach(transaction -> snapshot.removeTransaction(transaction));

        final Snapshot next = snapshot.getNext();
        final Snapshot previous = snapshot.getPrevious();

        if (next != null) {
            next.setPrevious(previous);
            save(next);
        }

        if (previous != null) {
            previous.setNext(next);
            save(previous);
        }

        snapshot.setNext(null);
        snapshot.setPrevious(null);

        user.removeSnapshot(snapshot);

        userService.save(user);
    }
}
