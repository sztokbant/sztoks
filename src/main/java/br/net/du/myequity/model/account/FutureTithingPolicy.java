package br.net.du.myequity.model.account;

import lombok.Getter;
import lombok.NonNull;

public enum FutureTithingPolicy {
    NONE("NO"),
    PROFITS_ONLY("PROFIT"),
    ALL("ALL");

    @Getter private final String shortValue;

    FutureTithingPolicy(@NonNull final String shortValue) {
        this.shortValue = shortValue;
    }

    public static String[] shortValues() {
        return new String[] {
            NONE.getShortValue(), PROFITS_ONLY.getShortValue(), ALL.getShortValue()
        };
    }

    public static FutureTithingPolicy forShortValue(@NonNull final String shortValue) {
        if (NONE.getShortValue().equals(shortValue)) {
            return NONE;
        } else if (PROFITS_ONLY.getShortValue().equals(shortValue)) {
            return PROFITS_ONLY;
        } else if (ALL.getShortValue().equals(shortValue)) {
            return ALL;
        }

        throw new IllegalArgumentException("Unknown shortValue: " + shortValue);
    }
}
