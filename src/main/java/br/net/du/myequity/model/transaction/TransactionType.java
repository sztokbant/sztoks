package br.net.du.myequity.model.transaction;

public enum TransactionType {
    INCOME("Income"),
    INVESTMENT("Investment"),
    DONATION("Donation");

    private final String displayName;

    TransactionType(final String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
