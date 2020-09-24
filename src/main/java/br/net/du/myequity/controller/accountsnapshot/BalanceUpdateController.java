package br.net.du.myequity.controller.accountsnapshot;

import br.net.du.myequity.controller.model.AccountSnapshotUpdateJsonRequest;
import br.net.du.myequity.controller.model.AccountSnapshotUpdateJsonResponse;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.AmountUpdateable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.function.BiFunction;

@RestController
public class BalanceUpdateController extends UpdateControllerBase {

    @PostMapping("/snapshot/updateAccountBalance")
    public AccountSnapshotUpdateJsonResponse post(final Model model,
                                                  @RequestBody final AccountSnapshotUpdateJsonRequest accountSnapshotUpdateJsonRequest) {

        final BiFunction<AccountSnapshotUpdateJsonRequest, AccountSnapshot, AccountSnapshotUpdateJsonResponse>
                updateAmountFunction = (jsonRequest, accountSnapshot) -> {
            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
            ((AmountUpdateable) accountSnapshot).setAmount(newValue);

            return AccountSnapshotUpdateJsonResponse.of(accountSnapshot);
        };

        return updateAccountSnapshotField(model,
                                          accountSnapshotUpdateJsonRequest,
                                          AmountUpdateable.class,
                                          updateAmountFunction);
    }
}
