package br.net.du.myequity.model.account;

import java.math.BigDecimal;

public interface SharesUpdatable {
    void setShares(BigDecimal shares);

    void setCurrentShareValue(BigDecimal currentShareValue);
}
