package br.net.du.sztoks.model.account;

import java.math.BigDecimal;

public interface FutureTithingCapable {
    FutureTithingPolicy getFutureTithingPolicy();

    void setFutureTithingPolicy(FutureTithingPolicy futureTithingPolicy);

    BigDecimal getFutureTithingReferenceAmount();
}
