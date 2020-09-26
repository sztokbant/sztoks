package br.net.du.myequity.controller.viewmodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDeleteJsonRequest {
    private Long accountId;

    // This seems to be needed because this class has only one field
    @JsonCreator
    public AccountDeleteJsonRequest(final Long accountId) {
        this.accountId = accountId;
    }
}
