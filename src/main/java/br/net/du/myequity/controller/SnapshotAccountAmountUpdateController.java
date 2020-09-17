package br.net.du.myequity.controller;

import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonRequest;
import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonResponse;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.AmountUpdateable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.function.BiFunction;

@RestController
public class SnapshotAccountAmountUpdateController extends SnapshotAccountUpdateControllerBase {

    @PostMapping("/snapshot/updateAccountBalance")
    public SnapshotAccountUpdateJsonResponse post(final Model model,
                                                  @RequestBody final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest) {

        final BiFunction<SnapshotAccountUpdateJsonRequest, AccountSnapshot, SnapshotAccountUpdateJsonResponse>
                updateAmountFunction =
                new BiFunction<SnapshotAccountUpdateJsonRequest, AccountSnapshot, SnapshotAccountUpdateJsonResponse>() {
                    @Override
                    public SnapshotAccountUpdateJsonResponse apply(final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest,
                                                                   final AccountSnapshot accountSnapshot) {
                        final BigDecimal newValue = new BigDecimal(snapshotAccountUpdateJsonRequest.getNewValue());
                        ((AmountUpdateable) accountSnapshot).setAmount(newValue);

                        return getDefaultResponseBuilder(accountSnapshot).build();
                    }
                };

        return updateAccountSnapshotField(model,
                                          snapshotAccountUpdateJsonRequest,
                                          AmountUpdateable.class,
                                          updateAmountFunction);
    }
}
