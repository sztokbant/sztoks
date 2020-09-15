package br.net.du.myequity.service;

import br.net.du.myequity.exception.MyEquityException;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.persistence.SnapshotRepository;
import br.net.du.myequity.persistence.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SnapshotService {
    private final SnapshotRepository snapshotRepository;

    private final UserRepository userRepository;

    public Snapshot newSnapshot(@NonNull final User user) {
        assert !user.getSnapshots().isEmpty();

        final Snapshot currentSnapshot = user.getSnapshots().first();

        final Snapshot newSnapshot = new Snapshot(currentSnapshot.getIndex() + 1,
                                                  LocalDate.now().toString(),
                                                  currentSnapshot.getAccountSnapshots());
        user.addSnapshot(newSnapshot);

        snapshotRepository.save(newSnapshot);

        return newSnapshot;
    }

    public void deleteSnapshot(@NonNull final User user, @NonNull final Snapshot snapshot) {
        assert user.getSnapshots().contains(snapshot);

        if (user.getSnapshots().size() == 1) {
            throw new MyEquityException("Snapshot cannot be deleted as it is the only remaining snapshot.");
        }

        for (final AccountSnapshot accountSnapshot : snapshot.getAccountSnapshots()) {
            snapshot.removeAccountSnapshot(accountSnapshot);
        }

        user.removeSnapshot(snapshot);

        userRepository.save(user);
    }
}
