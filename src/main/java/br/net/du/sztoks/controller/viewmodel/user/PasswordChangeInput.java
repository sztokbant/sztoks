package br.net.du.sztoks.controller.viewmodel.user;

import lombok.Data;

@Data
public class PasswordChangeInput {
    private String email;
    private String currentPassword;
    private String password;
    private String passwordConfirm;
}
