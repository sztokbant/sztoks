package br.net.du.sztoks.controller.viewmodel.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserViewModelInput {
    private String email;
    private String firstName;
    private String lastName;
    private String currencyUnit;
    private String tithingPercentage;
    private String password;
    private String passwordConfirm;
}
