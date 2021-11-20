package br.net.du.myequity.model.account;

import java.math.BigDecimal;

public interface BalanceUpdatable {
    void setBalance(BigDecimal balance);
}
