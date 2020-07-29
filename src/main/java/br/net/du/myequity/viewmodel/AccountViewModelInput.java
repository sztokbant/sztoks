package br.net.du.myequity.viewmodel;

import br.net.du.myequity.model.Account;
import br.net.du.myequity.model.AccountType;
import lombok.Data;
import org.joda.money.CurrencyUnit;

import java.time.LocalDate;

@Data
public class AccountViewModelInput {
    private String name;
    private String accountType;
    private String currencyUnit;

    public Account toAccount() {
        return new Account(this.name,
                           AccountType.valueOf(this.accountType),
                           CurrencyUnit.of(this.currencyUnit),
                           LocalDate.now());
    }
}
