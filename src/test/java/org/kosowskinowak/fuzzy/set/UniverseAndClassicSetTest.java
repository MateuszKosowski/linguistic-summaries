package org.kosowskinowak.fuzzy.set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class UniverseAndClassicSetTest {

    @Test
    void continuousSampling() {
        double[] xs = Universe.continuous(0, 10).samplePoints();
        assertEquals(1000, xs.length);
        assertEquals(0.0, xs[0], 1e-9);
        assertEquals(10.0, xs[999], 1e-9);
    }

    @Test
    void discreteSmallRangeIsIntegerAligned() {
        Universe u = Universe.discrete(0, 10);
        assertEquals(11, u.sampleCount());
        double[] xs = u.samplePoints();
        for (int i = 0; i <= 10; i++) {
            assertEquals(i, xs[i], 1e-9);
        }
    }

    @Test
    void discreteLargeRangeStaysIntegerStep() {
        // regresja P0-2: [0,25000] musi mieć krok 1 (a nie 1000 punktów co 25)
        Universe u = Universe.discrete(0, 25000);
        assertEquals(25001, u.sampleCount());
        double[] xs = u.samplePoints();
        assertEquals(1.0, xs[1] - xs[0], 1e-9);
    }

    @Test
    void containsAndClamp() {
        Universe u = Universe.continuous(1500, 6500);
        assertFalse(u.contains(1000));
        assertTrue(u.contains(1500));
        assertEquals(1500, u.clamp(1000), 1e-9);
        assertEquals(6500, u.clamp(7000), 1e-9);
        assertEquals(3000, u.clamp(3000), 1e-9);
    }

    @Test
    void maxLessThanMinThrows() {
        assertThrows(IllegalArgumentException.class, () -> Universe.continuous(10, 5));
    }

    @Test
    void classicSetMembershipAndOps() {
        Universe u = Universe.continuous(-10, 10);
        ClassicSet positive = new ClassicSet("dodatni", u, x -> x > 0);
        ClassicSet small = new ClassicSet("mały", u, x -> x < 5);

        assertEquals(1.0, positive.membership(5), 1e-9);
        assertEquals(0.0, positive.membership(-1), 1e-9);
        assertTrue(positive.contains(5));
        assertFalse(positive.complement().contains(5));
        assertTrue(positive.union(small).contains(-1));   // -1<5
        assertFalse(positive.intersect(small).contains(-1)); // -1 nie >0
        assertTrue(positive.intersect(small).contains(3));
    }
}
