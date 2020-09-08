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
    private String totalCredit;
    private String availableCredit;
}
