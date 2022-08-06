package br.net.du.sztoks.model.account;

import java.math.BigDecimal;

public interface BalanceUpdatable {
    void setBalance(BigDecimal balance);
}
