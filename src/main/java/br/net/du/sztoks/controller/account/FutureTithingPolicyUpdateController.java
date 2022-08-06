package br.net.du.sztoks.controller.account;

import br.net.du.sztoks.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.sztoks.controller.viewmodel.account.AccountViewModelOutput;
import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.account.FutureTithingCapable;
import br.net.du.sztoks.model.account.FutureTithingPolicy;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FutureTithingPolicyUpdateController {

    @Autowired AccountUpdater accountUpdater;

    @PostMapping("/snapshot/updateFutureTithingPolicy")
    public Object post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, Object>
                updateFutureTithingPolicyFunction =
                        (jsonRequest, account) -> {
                            final FutureTithingPolicy newValue =
                                    FutureTithingPolicy.forValue(
                                            jsonRequest.getNewValue(),
                                            account.getClass().getSimpleName());

                            ((FutureTithingCapable) account).setFutureTithingPolicy(newValue);

                            return AccountViewModelOutput.of(account, true);
                        };

        return accountUpdater.updateField(
                model,
                valueUpdateJsonRequest,
                FutureTithingCapable.class,
                updateFutureTithingPolicyFunction,
                true);
    }
}
