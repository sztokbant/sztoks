package br.net.du.myequity.controller.accountsnapshot;

import br.net.du.myequity.controller.viewmodel.AccountSnapshotUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.AccountSnapshotViewModelOutput;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.AmountUpdateable;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceUpdateController extends UpdateControllerBase {

    @PostMapping("/snapshot/updateAccountBalance")
    public AccountSnapshotViewModelOutput post(
            final Model model,
            @RequestBody final AccountSnapshotUpdateJsonRequest accountSnapshotUpdateJsonRequest) {

        final BiFunction<
                        AccountSnapshotUpdateJsonRequest,
                        AccountSnapshot,
                        AccountSnapshotViewModelOutput>
                updateAmountFunction =
                        (jsonRequest, accountSnapshot) -> {
                            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                            ((AmountUpdateable) accountSnapshot).setAmount(newValue);

                            return AccountSnapshotViewModelOutput.of(accountSnapshot, true);
                        };

        return updateAccountSnapshotField(
                model,
                accountSnapshotUpdateJsonRequest,
                AmountUpdateable.class,
                updateAmountFunction);
    }
}
