package org.kosowskinowak.fuzzy.set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.kosowskinowak.fuzzy.mf.GaussianMf;
import org.kosowskinowak.fuzzy.mf.TrapezoidalMf;

class FuzzySetMoreTest {

    private static final double TOL = 0.02;

    @Test
    void gaussianNormalDespiteSampling() {
        // regresja P0-1: szczyt Gaussa wypada między punktami siatki, ale zbiór jest normalny
        FuzzySet g = new FuzzySet("gauss", Universe.continuous(0, 200), new GaussianMf(100, 20));
        assertTrue(g.isNormal(), "Gaussian o szczycie 1.0 powinien być normalny");
        assertFalse(g.isEmpty());
    }

    @Test
    void subnormalSet() {
        FuzzySet s = new FuzzySet("subnormal", Universe.continuous(0, 10), x -> 0.6);
        assertEquals(0.6, s.height(), 1e-9);
        assertFalse(s.isNormal());
        assertFalse(s.isEmpty());
    }

    @Test
    void alphaCutAboveHeightIsEmpty() {
        FuzzySet t = new FuzzySet("t", Universe.continuous(0, 10), new TrapezoidalMf(2, 4, 6, 8));
        assertTrue(t.alphaCut(1.5).empty());
    }

    @Test
    void cardinalities() {
        // etykieta 0 dla x<5, 1 dla x>=5 na [0,10] => nośnik i sigma ~ 0.5 przestrzeni
        FuzzySet half = new FuzzySet("half", Universe.continuous(0, 10), new TrapezoidalMf(5, 5, 10, 10));
        assertEquals(0.5, half.relativeSupportCardinality(), TOL);
        assertEquals(0.5, half.relativeSigmaCardinality(), TOL);

        FuzzySet full = new FuzzySet("full", Universe.continuous(0, 10), x -> 1.0);
        assertEquals(1.0, full.relativeSupportCardinality(), 1e-9);
        assertEquals(1.0, full.relativeSigmaCardinality(), 1e-9);
    }

    @Test
    void convexValleyIsNotConvex() {
        // 1 -> 0 -> 1: dolina w środku => niewypukły
        FuzzySet valley = new FuzzySet("valley", Universe.continuous(0, 100),
                x -> Math.abs(x - 50) < 25 ? 0.0 : 1.0);
        assertFalse(valley.isConvex());
    }

    @Test
    void setOperationsAgainstMembership() {
        Universe u = Universe.continuous(0, 10);
        FuzzySet a = new FuzzySet("a", u, x -> 0.7);
        FuzzySet b = new FuzzySet("b", u, x -> 0.4);
        assertEquals(0.3, a.complement().membership(5), 1e-9);
        assertEquals(0.4, a.intersect(b).membership(5), 1e-9);
        assertEquals(0.7, a.union(b).membership(5), 1e-9);
        assertTrue(a.complement().name().startsWith("nie "));
    }
}
