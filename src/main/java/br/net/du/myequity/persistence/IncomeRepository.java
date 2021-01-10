package br.net.du.myequity.persistence;

import br.net.du.myequity.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeRepository extends JpaRepository<Income, Long> {}
