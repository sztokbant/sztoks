package br.net.du.sztoks.model.account;

import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static br.net.du.sztoks.test.TestConstants.FIRST_SNAPSHOT_MONTH;
import static br.net.du.sztoks.test.TestConstants.FIRST_SNAPSHOT_YEAR;
import static br.net.du.sztoks.test.TestConstants.TITHING_PERCENTAGE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.fail;

import br.net.du.sztoks.model.Snapshot;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

/**
 * Structural + light behavioral guards for {@link FutureTithingCapable}.
 *
 * <p>{@link BillAccount#updateFutureTithingAmount(BigDecimal)} is a no-op. Any concrete bill account
 * that is {@code FutureTithingCapable} must override it (the {@link ReceivableAccount} drift bug).
 *
 * <p>Abstract intermediates that implement {@code FutureTithingCapable} are ignored; only concrete
 * types are scanned. New bill-based capable types fail CI until they override the hook.
 */
class FutureTithingCapableContractTest {

    private static final String ACCOUNT_PACKAGE = "br.net.du.sztoks.model.account";

    @Test
    public void everyBillBasedFutureTithingCapable_overridesUpdateFutureTithingAmount()
            throws Exception {
        final Set<Class<?>> capableTypes = findConcreteFutureTithingCapableTypes();
        assertThat(
                "expected at least one FutureTithingCapable type", capableTypes, is(not(empty())));

        for (final Class<?> type : capableTypes) {
            if (!BillAccount.class.isAssignableFrom(type)) {
                continue;
            }

            if (!declaresUpdateFutureTithingAmount(type)) {
                fail(
                        type.getName()
                                + " implements FutureTithingCapable and extends BillAccount"
                                + " but does not override updateFutureTithingAmount(BigDecimal)."
                                + " BillAccount's default is a no-op and leaves FTA residual.");
            }
        }
    }

    @Test
    public void billBasedFutureTithingCapable_addMutateRemove_keepsInvariant() {
        // Receivable is the bill-based type that previously drifted; acts as behavioral canary.
        final Snapshot snapshot = newEmptySnapshot();

        final ReceivableAccount receivable =
                new ReceivableAccount(
                        "Contract receivable",
                        CURRENCY_UNIT,
                        FutureTithingPolicy.ALL,
                        LocalDate.now(),
                        LocalDate.now(),
                        new BigDecimal("1000.00"),
                        false);

        snapshot.addAccount(receivable);
        assertFutureTithingInvariant(snapshot, CURRENCY_UNIT);

        receivable.setBillAmount(new BigDecimal("400.00"));
        assertFutureTithingInvariant(snapshot, CURRENCY_UNIT);

        receivable.setIsPaid(true);
        assertFutureTithingInvariant(snapshot, CURRENCY_UNIT);

        receivable.setIsPaid(false);
        assertFutureTithingInvariant(snapshot, CURRENCY_UNIT);

        snapshot.removeAccount(receivable);
        assertFutureTithingInvariant(snapshot, CURRENCY_UNIT);
    }

    private static Snapshot newEmptySnapshot() {
        return new Snapshot(
                FIRST_SNAPSHOT_YEAR,
                FIRST_SNAPSHOT_MONTH,
                CURRENCY_UNIT,
                TITHING_PERCENTAGE,
                ImmutableSortedSet.of(),
                ImmutableList.of(),
                ImmutableMap.of());
    }

    /**
     * Future tithing is maintained incrementally: each FutureTithingCapable account pushes diffs
     * into the per-currency FutureTithingAccount. Independently, each capable account can report
     * what its contribution should be right now via getFutureTithingReferenceAmount().
     *
     * <p>These two views must match. If a mutator forgets to call updateFutureTithingAmount (or
     * updates the wrong amount), the stored FTA reference drifts from the sum of current capable
     * contributions — the failure mode that blocked snapshot delete with a non-zero residual.
     *
     * <p>Per currency: sum(FTA.referenceAmount) == sum(capable.getFutureTithingReferenceAmount()).
     */
    private static void assertFutureTithingInvariant(
            final Snapshot snapshot, final CurrencyUnit currencyUnit) {
        final BigDecimal expected =
                snapshot.getAccounts().stream()
                        .filter(a -> a instanceof FutureTithingCapable)
                        .filter(a -> a.getCurrencyUnit().equals(currencyUnit))
                        .map(a -> ((FutureTithingCapable) a).getFutureTithingReferenceAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal actual =
                snapshot.getAccounts().stream()
                        .filter(a -> a instanceof FutureTithingAccount)
                        .filter(a -> a.getCurrencyUnit().equals(currencyUnit))
                        .map(a -> ((FutureTithingAccount) a).getReferenceAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(actual, comparesEqualTo(expected));
    }

    private static Set<Class<?>> findConcreteFutureTithingCapableTypes() {
        final ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(FutureTithingCapable.class));

        return scanner.findCandidateComponents(ACCOUNT_PACKAGE).stream()
                .map(BeanDefinition::getBeanClassName)
                .map(FutureTithingCapableContractTest::loadClass)
                .filter(type -> !type.isInterface())
                .filter(type -> !Modifier.isAbstract(type.getModifiers()))
                .collect(Collectors.toSet());
    }

    private static Class<?> loadClass(final String name) {
        try {
            return Class.forName(name);
        } catch (final ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private static boolean declaresUpdateFutureTithingAmount(final Class<?> type) {
        try {
            final Method method =
                    type.getDeclaredMethod("updateFutureTithingAmount", BigDecimal.class);
            return method.getDeclaringClass().equals(type);
        } catch (final NoSuchMethodException e) {
            return false;
        }
    }
}