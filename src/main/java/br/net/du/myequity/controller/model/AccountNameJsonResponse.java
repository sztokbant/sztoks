package br.net.du.myequity.controller.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountNameJsonResponse {
    private String name;
}
