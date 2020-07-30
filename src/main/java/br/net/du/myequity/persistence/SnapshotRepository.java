package br.net.du.myequity.persistence;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SnapshotRepository extends JpaRepository<Snapshot, Long> {
    List<Snapshot> findAllByUser(User user);
}
