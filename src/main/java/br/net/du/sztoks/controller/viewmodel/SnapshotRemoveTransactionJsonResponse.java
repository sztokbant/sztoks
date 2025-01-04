package br.net.du.sztoks.controller.viewmodel;

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
    private String taxDeductibleDonationsTotal;
    private String tithingBalance;
    private String totalTithingBalance;
    private String netWorth;
    private String netWorthIncrease;
    private String netWorthIncreasePercentage;
    private String totalLiability;
}
