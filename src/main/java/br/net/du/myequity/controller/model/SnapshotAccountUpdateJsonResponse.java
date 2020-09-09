package br.net.du.myequity.controller.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SnapshotAccountUpdateJsonResponse {
    private String balance;
    private String currencyUnit;
    private String netWorth;
    private String accountType;
    private String totalForAccountType;

    // Credit Card only
    // TODO Extract class
    private String totalCredit;
    private String availableCredit;
    private String usedCreditPercentage;

    // Investment only
    // TODO Extract class
    private String shares;
    private String originalShareValue;
    private String currentShareValue;
    private String profitPercentage;
}
