package br.net.du.myequity.model.util;

import br.net.du.myequity.model.Snapshot;

public class SnapshotUtils {
    public static boolean equals(final Snapshot first, final Snapshot second) {
        return (first == null) ? (second == null) : first.equals(second);
    }
}
