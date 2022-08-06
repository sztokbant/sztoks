package br.net.du.sztoks.controller.viewmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntityRenameJsonResponse {
    private String name;
}
