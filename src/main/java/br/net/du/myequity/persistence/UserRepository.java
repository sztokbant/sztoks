package br.net.du.myequity.persistence;

import br.net.du.myequity.model.User;

public interface UserRepository extends CustomRepository<User, Long> {
    User findByEmail(String email);
}
