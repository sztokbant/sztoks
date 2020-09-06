package br.net.du.myequity.viewmodel;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.AccountFactory;
import lombok.Data;
import org.joda.money.CurrencyUnit;

import java.time.LocalDate;

@Data
public class AccountViewModelInput {
    private String name;
    private String typeName;
    private String currencyUnit;

    public Account toAccount() {
        return AccountFactory.newInstance(typeName, name, CurrencyUnit.of(currencyUnit), LocalDate.now());
    }
}
