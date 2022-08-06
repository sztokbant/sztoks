package br.net.du.sztoks.controller.currency;

import static br.net.du.sztoks.controller.util.ControllerConstants.CURRENCY;
import static br.net.du.sztoks.controller.util.ControllerConstants.ID;

import br.net.du.sztoks.controller.util.SnapshotUtils;
import br.net.du.sztoks.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.service.SnapshotService;
import org.joda.money.CurrencyUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SnapshotChangeBaseCurrencyController {
    @Autowired private SnapshotService snapshotService;

    @Autowired private SnapshotUtils snapshotUtils;

    @PostMapping("/snapshot/{id}/changeBaseCurrency/{currency}")
    @Transactional
    public Object changeBaseCurrency(
            @PathVariable(value = ID) final Long snapshotId,
            @PathVariable(value = CURRENCY) final String currency,
            final Model model) {
        final Snapshot snapshot = snapshotUtils.validateSnapshot(model, snapshotId);

        snapshot.changeBaseCurrencyUnitTo(CurrencyUnit.of(currency));

        snapshotService.save(snapshot);

        return ValueUpdateJsonRequest.builder().snapshotId(snapshotId).build();
    }
}
