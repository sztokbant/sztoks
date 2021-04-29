package br.net.du.myequity.model.transaction;

import lombok.Getter;
import lombok.NonNull;

public enum RecurrencePolicy {
    NONE("NO"),
    RECURRING("REC"),
    RESETTABLE("RES");

    @Getter private final String shortValue;

    RecurrencePolicy(@NonNull final String shortValue) {
        this.shortValue = shortValue;
    }

    public static String[] shortValues() {
        return new String[] {
            NONE.getShortValue(), RECURRING.getShortValue(), RESETTABLE.getShortValue()
        };
    }

    public static RecurrencePolicy forShortValue(@NonNull final String shortValue) {
        if (NONE.getShortValue().equals(shortValue)) {
            return NONE;
        } else if (RECURRING.getShortValue().equals(shortValue)) {
            return RECURRING;
        } else if (RESETTABLE.getShortValue().equals(shortValue)) {
            return RESETTABLE;
        }

        throw new IllegalArgumentException("Unknown shortValue: " + shortValue);
    }
}
