package br.net.du.myequity.persistence;

import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// TODO Create a Service
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUser(User user);
}
