package br.net.du.myequity.controller.viewmodel;

import br.net.du.myequity.model.account.Account;
import java.time.LocalDate;
import lombok.Data;
import org.joda.money.CurrencyUnit;

@Data
public class AccountViewModelInput {
    private String name;
    private String typeName;
    private String currencyUnit;

    public Account toAccount() {
        return AccountFactory.newInstance(
                typeName, name, CurrencyUnit.of(currencyUnit), LocalDate.now());
    }
}
