package br.net.du.sztoks.model.account;

import java.math.BigDecimal;

public interface SharesUpdatable {
    void setShares(BigDecimal shares);

    void setCurrentShareValue(BigDecimal currentShareValue);
}
