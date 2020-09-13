package br.net.du.myequity.service;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.persistence.SnapshotRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SnapshotService {
    private final SnapshotRepository snapshotRepository;

    public SnapshotService(final SnapshotRepository snapshotRepository) {
        this.snapshotRepository = snapshotRepository;
    }

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
}
