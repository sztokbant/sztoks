package br.net.du.myequity.model.transaction;

import lombok.NonNull;

public enum RecurrencePolicy {
    NONE,
    RECURRING,
    RESETTABLE;

    public static RecurrencePolicy forValue(@NonNull final String value) {
        if (NONE.name().equals(value)) {
            return NONE;
        } else if (RECURRING.name().equals(value)) {
            return RECURRING;
        } else if (RESETTABLE.name().equals(value)) {
            return RESETTABLE;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
