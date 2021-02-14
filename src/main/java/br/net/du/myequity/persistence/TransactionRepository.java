package br.net.du.myequity.persistence;

import br.net.du.myequity.model.transaction.Transaction;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByIdAndSnapshotId(Long id, Long snapshotId);
}
