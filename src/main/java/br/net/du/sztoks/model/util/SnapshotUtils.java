package br.net.du.sztoks.model.util;

import br.net.du.sztoks.model.Snapshot;
import java.time.LocalDate;
import java.util.Optional;
import lombok.NonNull;

public class SnapshotUtils {
    public static boolean equals(final Snapshot first, final Snapshot second) {
        return (first == null) ? (second == null) : first.equals(second);
    }

    public static Optional<LocalDate> computeNextSnapshotPeriod(@NonNull final Snapshot snapshot) {
        if (snapshot.getNext() != null) {
            return Optional.empty();
        }

        final LocalDate newSnapshotPeriod =
                LocalDate.of(snapshot.getYear(), snapshot.getMonth(), 15).plusMonths(1);

        final LocalDate now = LocalDate.now();
        if (now.isBefore(newSnapshotPeriod.minusMonths(1))) {
            return Optional.empty();
        }

        return Optional.of(newSnapshotPeriod);
    }
}
