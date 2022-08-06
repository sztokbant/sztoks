package br.net.du.sztoks.controller.viewmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValueUpdateJsonRequest {
    private Long snapshotId;
    private Long entityId;
    private String newValue;
}
