package br.net.du.sztoks.service;

public interface SecurityService {
    String findLoggedInEmail();

    void autoLogin(String email, String password);
}
