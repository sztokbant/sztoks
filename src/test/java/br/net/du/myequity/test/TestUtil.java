package br.net.du.myequity.test;

import br.net.du.myequity.model.Account;
import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.Workspace;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TestUtil {

    public static Account newLiabilityAccount(final String balance) {
        return newLiabilityAccount(CurrencyUnit.USD, balance);
    }

    public static Account newLiabilityAccount(final CurrencyUnit currencyUnit, final String balance) {
        return new Account("Asset",
                           AccountType.LIABILITY,
                           Money.of(currencyUnit, new BigDecimal(balance)),
                           LocalDate.now());
    }

    public static Account newAssetAccount(final String balance) {
        return newAssetAccount(CurrencyUnit.USD, balance);
    }

    public static Account newAssetAccount(final CurrencyUnit currencyUnit, final String balance) {
        return new Account("Liability",
                           AccountType.ASSET,
                           Money.of(currencyUnit, new BigDecimal(balance)),
                           LocalDate.now());
    }

    public static Workspace buildPopulatedWorkspace() {
        final Workspace workspace = new Workspace("My Workspace", CurrencyUnit.USD);

        workspace.addAccount(newLiabilityAccount("320000.00"));
        workspace.addAccount(newAssetAccount("100.00"));

        workspace.generateSnapshot(LocalDate.now());

        return workspace;
    }

    public static User buildUser() {
        final String email = "example@example.com";
        final String firstName = "Bill";
        final String lastName = "Gates";
        final Long id = 42L;

        final User user = new User(email, firstName, lastName);
        user.setId(id);

        return user;
    }
}
