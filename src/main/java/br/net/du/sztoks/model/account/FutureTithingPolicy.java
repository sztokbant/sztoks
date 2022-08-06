package br.net.du.sztoks.model.account;

import lombok.NonNull;

public enum FutureTithingPolicy {
    NONE,
    PROFITS_ONLY,
    ALL;

    public static FutureTithingPolicy forValue(
            final String value, @NonNull final String accountSubClassName) {
        if (value == null || value.equals(NONE.name())) {
            return NONE;
        } else if (value.equals(PROFITS_ONLY.name())
                && accountSubClassName.equals(InvestmentAccount.class.getSimpleName())) {
            return PROFITS_ONLY;
        } else {
            return ALL;
        }
    }
}
