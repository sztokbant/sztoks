package br.net.du.myequity.controller.accountsnapshot;

import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
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
public class BalanceUpdateController extends AccountSnapshotUpdateControllerBase {

    @PostMapping("/snapshot/updateAccountBalance")
    public AccountSnapshotViewModelOutput post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, AccountSnapshot, AccountSnapshotViewModelOutput>
                updateAmountFunction =
                        (jsonRequest, accountSnapshot) -> {
                            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                            ((AmountUpdateable) accountSnapshot).setAmount(newValue);

                            return AccountSnapshotViewModelOutput.of(accountSnapshot, true);
                        };

        return updateAccountSnapshotField(
                model, valueUpdateJsonRequest, AmountUpdateable.class, updateAmountFunction);
    }
}
