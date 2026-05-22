package org.kosowskinowak.summary.quality;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class QualityWeightsTest {

    private static final double EPS = 1e-12;

    @Test
    void equalWeightsNormalized() {
        QualityWeights w = QualityWeights.equal();
        double sum = 0;
        for (int i = 0; i < 11; i++) {
            assertEquals(1.0 / 11, w.weight(i), EPS);
            sum += w.weight(i);
        }
        assertEquals(1.0, sum, EPS);
    }

    @Test
    void arbitraryWeightsNormalized() {
        QualityWeights w = QualityWeights.of(2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        assertEquals(1.0, w.weight(0), EPS);
        assertEquals(0.0, w.weight(1), EPS);
    }

    @Test
    void weightedSum() {
        QualityWeights w = QualityWeights.equal();
        double[] ones = new double[11];
        java.util.Arrays.fill(ones, 1.0);
        assertEquals(1.0, w.weightedSum(ones), EPS);

        double[] onlyFirst = new double[11];
        onlyFirst[0] = 1.0;
        assertEquals(1.0 / 11, w.weightedSum(onlyFirst), EPS);
    }

    @Test
    void validation() {
        assertThrows(IllegalArgumentException.class, () -> QualityWeights.of(1, 2, 3));
        assertThrows(IllegalArgumentException.class,
                () -> QualityWeights.of(-1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));
        assertThrows(IllegalArgumentException.class,
                () -> QualityWeights.of(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        assertThrows(IllegalArgumentException.class,
                () -> QualityWeights.equal().weightedSum(new double[10]));
    }
}
