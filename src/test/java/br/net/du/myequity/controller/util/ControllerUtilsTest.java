package br.net.du.myequity.controller.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ControllerUtilsTest {

    @Test
    public void formatAsDecimal_nullInput_returnZero() {
        assertEquals("0.00", ControllerUtils.formatAsDecimal(null));
    }

    @Test
    public void formatAsPercentage_nullInput_returnZero() {
        assertEquals("0.00%", ControllerUtils.formatAsPercentage(null));
    }

    @Test
    public void formatAsPercentage_always2DecimalPlacesRoundUp() {
        assertEquals("0.00%", ControllerUtils.formatAsPercentage(new BigDecimal("0")).toString());
        assertEquals("1.00%", ControllerUtils.formatAsPercentage(new BigDecimal("1")).toString());
        assertEquals("1.00%", ControllerUtils.formatAsPercentage(new BigDecimal("1.0")).toString());
        assertEquals(
                "1.00%", ControllerUtils.formatAsPercentage(new BigDecimal("1.00")).toString());
        assertEquals(
                "1.00%", ControllerUtils.formatAsPercentage(new BigDecimal("1.004")).toString());
        assertEquals(
                "1.01%", ControllerUtils.formatAsPercentage(new BigDecimal("1.005")).toString());
    }

    @Test
    public void toDecimal_atLeast2DecimalPlacesMaxToLastSignificativeDigit() {
        assertEquals("0.00", ControllerUtils.toDecimal(new BigDecimal("0")).toString());
        assertEquals("1.00", ControllerUtils.toDecimal(new BigDecimal("1")).toString());
        assertEquals("1.00", ControllerUtils.toDecimal(new BigDecimal("1.0")).toString());
        assertEquals("1.00", ControllerUtils.toDecimal(new BigDecimal("1.00")).toString());
        assertEquals("1.00", ControllerUtils.toDecimal(new BigDecimal("1.000")).toString());
        assertEquals("1.00", ControllerUtils.toDecimal(new BigDecimal("1.0000")).toString());
        assertEquals("1.0001", ControllerUtils.toDecimal(new BigDecimal("1.0001")).toString());
        assertEquals(
                "1.0001549", ControllerUtils.toDecimal(new BigDecimal("1.0001549")).toString());
    }
}
