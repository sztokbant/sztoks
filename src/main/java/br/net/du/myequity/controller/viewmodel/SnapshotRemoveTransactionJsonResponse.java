package br.net.du.myequity.controller.viewmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SnapshotRemoveTransactionJsonResponse {
    private Long entityId;
    private String currencyUnit;
    private String currencyUnitSymbol;
    private String type;
    private String totalForTransactionType;
    private String tithingBalance;
    private String netWorth;
    private String totalLiability;
}
