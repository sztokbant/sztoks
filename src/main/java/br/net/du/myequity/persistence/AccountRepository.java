package br.net.du.myequity.persistence;

import br.net.du.myequity.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
