package br.net.du.myequity.service;

import br.net.du.myequity.model.User;

public interface UserService {
    void signUp(String email, String firstName, String lastName, String password);

    void save(User user);

    User findByEmail(String email);
}
