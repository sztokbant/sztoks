package br.net.du.sztoks.model.account;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

class FutureTithingPolicyTest {

    @Test
    void forValue_profitsOnly_allowedForInvestmentAndAdditionalCurrency() {
        assertThat(
                FutureTithingPolicy.forValue("PROFITS_ONLY", "InvestmentAccount"),
                is(FutureTithingPolicy.PROFITS_ONLY));
        assertThat(
                FutureTithingPolicy.forValue("PROFITS_ONLY", "AdditionalCurrencyAccount"),
                is(FutureTithingPolicy.PROFITS_ONLY));
    }

    @Test
    void forValue_profitsOnly_notAllowedForOtherTypes() {
        assertThat(
                FutureTithingPolicy.forValue("PROFITS_ONLY", "SimpleAssetAccount"),
                is(FutureTithingPolicy.ALL));
    }

    @Test
    void forValue_noneAndAll() {
        assertThat(
                FutureTithingPolicy.forValue(null, "InvestmentAccount"),
                is(FutureTithingPolicy.NONE));
        assertThat(
                FutureTithingPolicy.forValue("NONE", "InvestmentAccount"),
                is(FutureTithingPolicy.NONE));
        assertThat(
                FutureTithingPolicy.forValue("ALL", "InvestmentAccount"),
                is(FutureTithingPolicy.ALL));
    }
}
