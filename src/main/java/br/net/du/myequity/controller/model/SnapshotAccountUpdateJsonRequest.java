package br.net.du.myequity.controller.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SnapshotAccountUpdateJsonRequest {
    private Long snapshotId;
    private Long accountId;
    private String newValue;
}
