package br.net.du.myequity.model.account;

import java.math.BigDecimal;

public interface FutureTithingCapable {
    FutureTithingPolicy getFutureTithingPolicy();

    BigDecimal getFutureTithingReferenceAmount();
}
