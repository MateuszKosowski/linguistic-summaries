package org.kosowskinowak.fuzzy.linguistic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.kosowskinowak.fuzzy.mf.TrapezoidalMf;
import org.kosowskinowak.fuzzy.set.FuzzySet;
import org.kosowskinowak.fuzzy.set.Universe;

class LinguisticTest {

    private static final double EPS = 1e-6;

    private static LinguisticVariable var(double min, double max) {
        Universe u = Universe.continuous(min, max);
        FuzzySet big = new FuzzySet("wielki", u, new TrapezoidalMf(min, min, max, max));
        return new LinguisticVariable("X", "x", u, List.of(new Label("wielki", big)));
    }

    @Test
    void isValidBoundaries() {
        LinguisticVariable above0 = var(1500, 6500);
        assertFalse(above0.isValid(Double.NaN));
        assertFalse(above0.isValid(-5));
        assertFalse(above0.isValid(0.0));      // 0 to sentinel braku danych, bo min>0
        assertTrue(above0.isValid(3000));
        assertTrue(above0.isValid(99999));     // poza zakresem, ale dodatnie => poprawne (clamp)

        LinguisticVariable from0 = var(0, 1500);
        assertTrue(from0.isValid(0.0));         // 0 legalne, bo min==0
    }

    @Test
    void degreeOfClampsToUniverse() {
        LinguisticVariable v = var(1500, 6500);
        Label big = v.label("wielki").orElseThrow();
        // funkcja rośnie do max; wartość poza zakresem przycięta do krańca
        assertEquals(1.0, v.degreeOf(big, 7000), EPS);
        assertEquals(1.0, v.degreeOf(big, 6500), EPS);
    }

    @Test
    void quantifierRelativeAndAbsolute() {
        FuzzySet most = new FuzzySet("większość", Universe.continuous(0, 1),
                new TrapezoidalMf(0.6, 0.7, 0.85, 0.95));
        Quantifier rel = new Quantifier("większość", Quantifier.Type.RELATIVE, most);
        assertTrue(rel.isRelative());
        assertEquals(1.0, rel.degree(0.8), EPS);
        assertEquals(0.0, rel.degree(0.5), EPS);
        assertEquals(0.5, rel.degree(0.65), EPS);

        FuzzySet few = new FuzzySet("kilka", Universe.discrete(0, 10), new TrapezoidalMf(1, 3, 3, 5));
        Quantifier abs = new Quantifier("kilka", Quantifier.Type.ABSOLUTE, few);
        assertFalse(abs.isRelative());
        assertEquals(1.0, abs.degree(3), EPS);
        assertEquals(0.5, abs.degree(2), EPS);
        assertEquals(0.0, abs.degree(6), EPS);
    }
}
