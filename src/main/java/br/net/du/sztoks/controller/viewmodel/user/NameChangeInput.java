package br.net.du.sztoks.controller.viewmodel.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NameChangeInput {
    private String firstName;
    private String lastName;
}
