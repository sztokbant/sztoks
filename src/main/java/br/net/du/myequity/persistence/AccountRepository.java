package br.net.du.myequity.persistence;

import br.net.du.myequity.model.Account;
import br.net.du.myequity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// TODO Create a Service
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUser(User user);
}
