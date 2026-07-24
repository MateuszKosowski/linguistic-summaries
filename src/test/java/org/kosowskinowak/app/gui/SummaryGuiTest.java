package org.kosowskinowak.app.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class SummaryGuiTest {

    @Test
    void createsTriangularMembershipFunctionFromAdvancedFormParameters() {
        var mf = SummaryGui.createMembershipFunction("trójkątna", "0; 5; 10");

        assertEquals(1.0, mf.degree(5.0), 1e-9);
        assertEquals(0.0, mf.degree(0.0), 1e-9);
    }

    @Test
    void createsGaussianMembershipFunctionFromAdvancedFormParameters() {
        var mf = SummaryGui.createMembershipFunction("gaussowska", "100; 10");

        assertEquals(1.0, mf.degree(100.0), 1e-9);
    }

    @Test
    void rejectsWrongParameterCountForTrapezoid() {
        assertThrows(IllegalArgumentException.class,
                () -> SummaryGui.createMembershipFunction("trapezoidalna", "0; 1; 2"));
    }
}
