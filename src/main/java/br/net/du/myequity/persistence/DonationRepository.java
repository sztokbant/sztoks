package br.net.du.myequity.persistence;

import br.net.du.myequity.model.transaction.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationRepository extends JpaRepository<Donation, Long> {}
