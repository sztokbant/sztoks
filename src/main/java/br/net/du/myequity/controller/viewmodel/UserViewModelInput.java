package br.net.du.myequity.controller.viewmodel;

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
    private String password;
    private String passwordConfirm;
}
