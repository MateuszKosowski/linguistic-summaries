package org.kosowskinowak.fuzzy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.kosowskinowak.fuzzy.mf.GaussianMf;
import org.kosowskinowak.fuzzy.mf.MembershipFunction;
import org.kosowskinowak.fuzzy.mf.Operations;
import org.kosowskinowak.fuzzy.mf.TrapezoidalMf;
import org.kosowskinowak.fuzzy.mf.TriangularMf;
import org.kosowskinowak.fuzzy.set.FuzzySet;
import org.kosowskinowak.fuzzy.set.Interval;
import org.kosowskinowak.fuzzy.set.Universe;

class MembershipAndSetTest {

    private static final double EPS = 1e-6;

    @Test
    void trapezoidShape() {
        TrapezoidalMf mf = new TrapezoidalMf(4200, 4500, 4700, 4900);
        assertEquals(0.0, mf.degree(4000), EPS);
        assertEquals(0.5, mf.degree(4350), EPS);
        assertEquals(1.0, mf.degree(4600), EPS);
        assertEquals(0.5, mf.degree(4800), EPS);
        assertEquals(0.0, mf.degree(5000), EPS);
    }

    @Test
    void triangleShape() {
        TriangularMf mf = new TriangularMf(6, 8, 10);
        assertEquals(0.0, mf.degree(6), EPS);
        assertEquals(0.5, mf.degree(7), EPS);
        assertEquals(1.0, mf.degree(8), EPS);
        assertEquals(0.0, mf.degree(10), EPS);
    }

    @Test
    void gaussianPeakAtMean() {
        GaussianMf mf = new GaussianMf(100, 20);
        assertEquals(1.0, mf.degree(100), EPS);
        assertTrue(mf.degree(120) > 0.5 && mf.degree(120) < 1.0);
    }

    @Test
    void operationsComplementMinMax() {
        MembershipFunction a = x -> 0.7;
        MembershipFunction b = x -> 0.4;
        assertEquals(0.3, Operations.complement(a).degree(0), EPS);
        assertEquals(0.4, Operations.intersection(a, b).degree(0), EPS); // min
        assertEquals(0.7, Operations.union(a, b).degree(0), EPS);        // max
    }

    @Test
    void fuzzySetProperties() {
        Universe u = Universe.continuous(0, 10);
        FuzzySet normal = new FuzzySet("trapez", u, new TrapezoidalMf(2, 4, 6, 8));
        assertEquals(1.0, normal.height(), 1e-3);
        assertTrue(normal.isNormal());
        assertFalse(normal.isEmpty());
        assertTrue(normal.isConvex());

        Interval support = normal.support();
        assertTrue(support.lo() >= 2 - 0.05 && support.lo() <= 2 + 0.05);
        assertTrue(support.hi() >= 8 - 0.05 && support.hi() <= 8 + 0.05);

        Interval cut = normal.alphaCut(1.0);
        assertTrue(cut.lo() >= 4 - 0.05 && cut.hi() <= 6 + 0.05);
    }

    @Test
    void emptyAndNonConvexSets() {
        Universe u = Universe.continuous(0, 100);
        FuzzySet empty = new FuzzySet("pusty", u, x -> 0.0);
        assertTrue(empty.isEmpty());
        assertEquals(0.0, empty.height(), EPS);

        // suma dwóch rozłącznych trapezów => dwa szczyty => niewypukły
        MembershipFunction twoPeaks = Operations.union(
                new TrapezoidalMf(0, 10, 20, 30), new TrapezoidalMf(70, 80, 90, 100));
        FuzzySet nonConvex = new FuzzySet("dwa szczyty", u, twoPeaks);
        assertFalse(nonConvex.isConvex());
    }
}
