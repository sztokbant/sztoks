package br.net.du.myequity.controller.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SnapshotAccountUpdateJsonRequest {
    private Long snapshotId;
    private Long accountId;
    private BigDecimal newValue;
}
