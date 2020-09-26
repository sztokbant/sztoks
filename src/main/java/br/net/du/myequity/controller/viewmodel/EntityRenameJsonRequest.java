package br.net.du.myequity.controller.viewmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntityRenameJsonRequest {
    private Long id;
    private String newValue;
}
