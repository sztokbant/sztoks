package br.net.du.sztoks.controller.viewmodel.user;

import lombok.Data;

@Data
public class EmailUpdateViewModelInput {
    private String currentEmail;
    private String email;
    private String emailConfirmation;
    private String password;
}
