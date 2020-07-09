package br.net.du.myequity.service;

public interface SecurityService {
    String findLoggedInEmail();

    void autoLogin(String email, String password);
}
