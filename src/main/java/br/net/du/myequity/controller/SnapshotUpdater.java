package br.net.du.myequity.controller;

import br.net.du.myequity.controller.util.SnapshotUtils;
import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.service.SnapshotService;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

@Component
public class SnapshotUpdater {
    private static Logger LOG = Logger.getLogger(SnapshotUpdater.class.getName());
    private static Level LEVEL = Level.INFO;

    @Autowired private SnapshotService snapshotService;
    @Autowired private SnapshotUtils snapshotUtils;

    @Transactional
    public Object updateField(
            final Model model,
            final ValueUpdateJsonRequest valueUpdateJsonRequest,
            final BiFunction<ValueUpdateJsonRequest, Snapshot, Object> function) {
        // Ensure snapshot belongs to logged user
        final Snapshot snapshot =
                snapshotUtils.validateLockAndRefreshSnapshot(
                        model, valueUpdateJsonRequest.getSnapshotId());

        LOG.log(
                LEVEL,
                "[SZTOKS] Locked snapshot, assetsTotal = "
                        + snapshot.getTotalFor(AccountType.ASSET)
                        + ", liabilitiesTotal = "
                        + snapshot.getTotalFor(AccountType.LIABILITY));

        final Object jsonResponse = function.apply(valueUpdateJsonRequest, snapshot);

        LOG.log(LEVEL, "[SZTOKS] Saving snapshot...");
        snapshotService.save(snapshot);

        return jsonResponse;
    }
}
