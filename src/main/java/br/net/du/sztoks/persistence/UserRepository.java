package br.net.du.sztoks.persistence;

import br.net.du.sztoks.model.User;

public interface UserRepository extends CustomRepository<User, Long> {
    User findByEmail(String email);
}
