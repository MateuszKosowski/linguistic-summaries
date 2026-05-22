package org.kosowskinowak.fuzzy.mf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MfEdgeTest {

    private static final double EPS = 1e-6;

    @Test
    void trapezoidLeftOpen() {
        TrapezoidalMf mf = new TrapezoidalMf(1500, 1500, 3500, 3900);
        assertEquals(1.0, mf.degree(1500), EPS);
        assertEquals(0.0, mf.degree(1000), EPS);
        assertEquals(0.5, mf.degree(3700), EPS);
        assertEquals(0.0, mf.degree(3900), EPS);
    }

    @Test
    void trapezoidRightOpen() {
        TrapezoidalMf mf = new TrapezoidalMf(5100, 5300, 6500, 6500);
        assertEquals(1.0, mf.degree(6500), EPS);
        assertEquals(0.5, mf.degree(5200), EPS);
        assertEquals(0.0, mf.degree(5000), EPS);
    }

    @Test
    void trapezoidCorners() {
        TrapezoidalMf mf = new TrapezoidalMf(2, 4, 6, 8);
        assertEquals(0.0, mf.degree(2), EPS);
        assertEquals(1.0, mf.degree(4), EPS);
        assertEquals(1.0, mf.degree(6), EPS);
        assertEquals(0.0, mf.degree(8), EPS);
        assertEquals(0.5, mf.degree(3), EPS);
        assertEquals(0.5, mf.degree(7), EPS);
    }

    @Test
    void constructorValidation() {
        assertThrows(IllegalArgumentException.class, () -> new TrapezoidalMf(4, 2, 6, 8));
        assertThrows(IllegalArgumentException.class, () -> new TriangularMf(3, 2, 1));
        assertThrows(IllegalArgumentException.class, () -> new GaussianMf(0, 0));
        assertThrows(IllegalArgumentException.class, () -> new GaussianMf(0, -1));
    }

    @Test
    void gaussianSymmetricAndClampedPeak() {
        GaussianMf mf = new GaussianMf(100, 20);
        assertEquals(1.0, mf.degree(100), 1e-4);
        assertEquals(mf.degree(80), mf.degree(120), 1e-9);
    }

    @Test
    void operationsClampOutOfRange() {
        assertEquals(0.0, Operations.complement(x -> 1.5).degree(0), EPS); // 1-1.5 -> clamp 0
        assertEquals(1.0, Operations.complement(x -> -0.2).degree(0), EPS); // 1-(-0.2) clamp 1
        assertEquals(0.0, Operations.intersection(x -> 1.0, x -> 0.0).degree(0), EPS);
        assertEquals(1.0, Operations.union(x -> 1.0, x -> 0.0).degree(0), EPS);
    }
}
