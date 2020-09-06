package br.net.du.myequity.viewmodel;

import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.SimpleAssetAccount;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
import lombok.Data;
import org.joda.money.CurrencyUnit;

import java.time.LocalDate;

@Data
public class AccountViewModelInput {
    private String name;
    private String accountType;
    private String currencyUnit;

    public Account toAccount() {
        final AccountType accountType = AccountType.valueOf(this.accountType);

        // TODO This if-zilla has to be cleaned up
        return accountType.equals(AccountType.ASSET) ?
                new SimpleAssetAccount(this.name, CurrencyUnit.of(this.currencyUnit), LocalDate.now()) :
                new SimpleLiabilityAccount(this.name, CurrencyUnit.of(this.currencyUnit), LocalDate.now());
    }
}
