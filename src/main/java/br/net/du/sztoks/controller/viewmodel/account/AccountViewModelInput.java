package br.net.du.sztoks.controller.viewmodel.account;

import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.account.FutureTithingPolicy;
import lombok.Data;
import org.joda.money.CurrencyUnit;

@Data
public class AccountViewModelInput {
    private String name;
    private String accountType;
    private String subtypeName;
    private String currencyUnit;
    private String futureTithingPolicy;

    public Account toAccount() {
        return AccountFactory.newInstance(
                subtypeName,
                name,
                CurrencyUnit.of(currencyUnit),
                FutureTithingPolicy.forValue(futureTithingPolicy, subtypeName));
    }
}
