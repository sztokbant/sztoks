package br.net.du.sztoks.controller.snapshot;

import br.net.du.sztoks.controller.util.SnapshotValidations;
import br.net.du.sztoks.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.account.AccountType;
import br.net.du.sztoks.service.SnapshotService;
import java.util.function.BiFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

@Component
@Slf4j
public class SnapshotUpdater {
    @Autowired private SnapshotService snapshotService;
    @Autowired private SnapshotValidations snapshotValidations;

    @Transactional
    public Object updateField(
            final Model model,
            final ValueUpdateJsonRequest valueUpdateJsonRequest,
            final BiFunction<ValueUpdateJsonRequest, Snapshot, Object> function) {
        // Ensure snapshot belongs to logged user
        final Snapshot snapshot =
                snapshotValidations.validateLockAndRefreshSnapshot(
                        model, valueUpdateJsonRequest.getSnapshotId());

        log.debug(
                "[SZTOKS] Locked snapshot, assetsTotal = "
                        + snapshot.getTotalFor(AccountType.ASSET)
                        + ", liabilitiesTotal = "
                        + snapshot.getTotalFor(AccountType.LIABILITY));

        final Object jsonResponse = function.apply(valueUpdateJsonRequest, snapshot);

        log.debug("[SZTOKS] Saving snapshot...");
        snapshotService.save(snapshot);

        return jsonResponse;
    }
}
