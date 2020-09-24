package br.net.du.myequity.persistence;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// TODO Create a Service
public interface AccountSnapshotRepository extends JpaRepository<AccountSnapshot, AccountSnapshot.PK> {
    Optional<AccountSnapshot> findBySnapshotIdAndAccountId(Long snapshotId, Long accountId);

    List<AccountSnapshot> findAllByAccount(Account account);
}