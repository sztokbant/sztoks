package br.net.du.myequity.controller.viewmodel;

import br.net.du.myequity.model.snapshot.AccountSnapshot;
import lombok.Data;
import org.joda.money.CurrencyUnit;

@Data
public class AccountViewModelInput {
    private String name;
    private String accountType;
    private String typeName;
    private String currencyUnit;

    public AccountSnapshot toAccount() {
        return AccountFactory.newInstance(typeName, name, CurrencyUnit.of(currencyUnit));
    }
}
