package br.net.du.myequity.model.account;

import java.math.BigDecimal;

public interface BalanceUpdateable {
    void setBalance(BigDecimal balance);
}
