package br.net.du.myequity.model.account;

import java.math.BigDecimal;

public interface SharesUpdateable {
    void setShares(BigDecimal shares);

    void setCurrentShareValue(BigDecimal currentShareValue);
}
