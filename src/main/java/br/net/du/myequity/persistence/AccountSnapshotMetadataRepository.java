package br.net.du.myequity.persistence;

import br.net.du.myequity.model.AccountSnapshotMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountSnapshotMetadataRepository extends JpaRepository<AccountSnapshotMetadata, Long> {
    Optional<AccountSnapshotMetadata> findByAccountId(Long accountId);
}
