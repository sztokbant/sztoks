package br.net.du.sztoks.persistence;

import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.SnapshotSummary;
import br.net.du.sztoks.model.User;
import br.net.du.sztoks.model.totals.CumulativeTransactionCategoryTotals;
import br.net.du.sztoks.model.totals.CumulativeTransactionTotals;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface SnapshotRepository extends CustomRepository<Snapshot, Long> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Snapshot> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Snapshot> findByIdAndUserId(Long snapshotId, Long userId);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Snapshot findTopByUserOrderByYearDescMonthDesc(User user);

    List<SnapshotSummary> findAllByUserOrderByYearDescMonthDesc(User user);

    @Query(
            value =
                    "SELECT \n"
                            + "base_currency baseCurrency, \n"
                            + "SUM(s.incomes_total) incomesTotal, \n"
                            + "SUM(s.investments_total) investmentsTotal, \n"
                            + "SUM(s.donations_total) donationsTotal, \n"
                            + "SUM(s.tax_deductible_donations_total) taxDeductibleDonationsTotal, \n"
                            + "CASE SUM(s.incomes_total) WHEN 0 THEN 0 ELSE SUM(s.investments_total) * 100.0 / SUM(s.incomes_total) END investmentAvg, \n"
                            + "CASE SUM(s.incomes_total) WHEN 0 THEN 0 ELSE SUM(s.donations_total) * 100.0 / SUM(s.incomes_total) END donationAvg \n"
                            + "FROM (SELECT * FROM snapshots WHERE id <= ?1 AND user_id = ?2 ORDER BY year DESC, month DESC LIMIT 12) s \n"
                            + "GROUP BY baseCurrency \n",
            nativeQuery = true)
    List<CumulativeTransactionTotals> findPastTwelveMonthsTransactionTotals(
            Long refSnapshotId, Long userId);

    @Query(
            value =
                    "SELECT \n"
                            + "base_currency baseCurrency, \n"
                            + "SUM(s.incomes_total) incomesTotal, \n"
                            + "SUM(s.investments_total) investmentsTotal, \n"
                            + "SUM(s.donations_total) donationsTotal, \n"
                            + "SUM(s.tax_deductible_donations_total) taxDeductibleDonationsTotal, \n"
                            + "CASE SUM(s.incomes_total) WHEN 0 THEN 0 ELSE SUM(s.investments_total) * 100.0 / SUM(s.incomes_total) END investmentAvg, \n"
                            + "CASE SUM(s.incomes_total) WHEN 0 THEN 0 ELSE SUM(s.donations_total) * 100.0 / SUM(s.incomes_total) END donationAvg \n"
                            + "FROM (SELECT * FROM snapshots WHERE year = ?1 AND month <= ?2 AND user_id = ?3) s \n"
                            + "GROUP BY baseCurrency \n",
            nativeQuery = true)
    List<CumulativeTransactionTotals> findYearToDateCumulativeTransactionTotals(
            Integer refYear, Integer refMonth, Long userId);

    @Query(
            value =
                    "SELECT \n"
                            + "t.transaction_type transactionType, \n"
                            + "t.category category, \n"
                            + "t.currency currency, \n"
                            + "SUM(t.amount) amount\n"
                            + "FROM transactions t \n"
                            + "WHERE snapshot_id IN \n"
                            + "  (SELECT id FROM \n"
                            + "    (SELECT id FROM snapshots WHERE id <= ?1 AND user_id = ?2 ORDER BY year DESC, month DESC LIMIT 12) \n"
                            + "  tmp) \n"
                            + "GROUP BY currency, transaction_type, category",
            nativeQuery = true)
    List<CumulativeTransactionCategoryTotals> findPastTwelveMonthsTransactionCategoryTotals(
            Long refSnapshotId, Long userId);

    @Query(
            value =
                    "SELECT \n"
                            + "t.transaction_type transactionType, \n"
                            + "t.category category, \n"
                            + "t.currency currency, \n"
                            + "SUM(t.amount) amount\n"
                            + "FROM transactions t \n"
                            + "WHERE snapshot_id IN \n"
                            + "  (SELECT id FROM \n"
                            + "    (SELECT id FROM snapshots WHERE year = ?1 AND month <= ?2 AND user_id = ?3) \n"
                            + "  tmp) \n"
                            + "GROUP BY currency, transaction_type, category",
            nativeQuery = true)
    List<CumulativeTransactionCategoryTotals> findYearToDateTransactionCategoryTotals(
            Integer refYear, Integer refMonth, Long userId);
}
