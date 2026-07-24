package org.kosowskinowak.summary.quality;

/**
 * Wagi miar jakości T1–T11 używane przy wyznaczaniu podsumowania optymalnego (Σ wᵢ·Tᵢ).
 * Wagi są normalizowane do sumy 1. Domyślnie równe — w GUI będą sterowane suwakami.
 */
public final class QualityWeights {

    private final double[] weights;

    private QualityWeights(double[] weights) {
        double sum = 0.0;
        for (double w : weights) {
            if (w < 0) {
                throw new IllegalArgumentException("Waga nie może być ujemna: " + w);
            }
            sum += w;
        }
        if (sum == 0.0) {
            throw new IllegalArgumentException("Suma wag musi być dodatnia");
        }
        this.weights = new double[weights.length];
        for (int i = 0; i < weights.length; i++) {
            this.weights[i] = weights[i] / sum;
        }
    }

    /** Wagi równe (1/11 każda). */
    public static QualityWeights equal() {
        double[] w = new double[11];
        java.util.Arrays.fill(w, 1.0);
        return new QualityWeights(w);
    }

    /** Wagi z tablicy 11 wartości (zostaną znormalizowane). */
    public static QualityWeights of(double... weights) {
        if (weights.length != 11) {
            throw new IllegalArgumentException("Wymagane 11 wag, otrzymano: " + weights.length);
        }
        return new QualityWeights(weights);
    }

    /** Ważona suma miar: T = Σ wᵢ·Tᵢ. */
    public double weightedSum(double[] measures) {
        if (measures.length != weights.length) {
            throw new IllegalArgumentException("Oczekiwano " + weights.length + " miar");
        }
        double sum = 0.0;
        for (int i = 0; i < measures.length; i++) {
            sum += weights[i] * measures[i];
        }
        return sum;
    }

    public double weight(int index) {
        return weights[index];
    }
}
