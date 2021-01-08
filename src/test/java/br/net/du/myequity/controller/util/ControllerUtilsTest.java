package br.net.du.myequity.controller.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
