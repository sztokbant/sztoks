package br.net.du.myequity.controller.viewmodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntityDeleteJsonRequest {
    private Long entityId;

    // This seems to be needed because this class has only one field
    @JsonCreator
    public EntityDeleteJsonRequest(final Long entityId) {
        this.entityId = entityId;
    }
}
