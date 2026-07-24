package org.kosowskinowak.fuzzy.set;

/**
 * Przestrzeń rozważań (universe of discourse) zbioru rozmytego. Może być:
 * <ul>
 *   <li>{@link Kind#CONTINUOUS gęsta} – zakres liczb rzeczywistych (np. długość auta),
 *       próbkowana równomiernie {@code sampleCount} punktami przy obliczeniach własności;</li>
 *   <li>{@link Kind#DISCRETE dyskretna} – wartości całkowite z [min, max] (np. liczność aut),
 *       próbkowana co 1 (z górnym limitem bezpieczeństwa).</li>
 * </ul>
 */
public final class Universe {

    public enum Kind {DISCRETE, CONTINUOUS}

    private static final int DEFAULT_SAMPLES = 1000;
    private static final int MAX_DISCRETE_SAMPLES = 200_001;

    private final double min;
    private final double max;
    private final Kind kind;
    private final int sampleCount;
    private double[] cachedSamples; // niemutowalna, liczona raz na żądanie

    private Universe(double min, double max, Kind kind, int sampleCount) {
        if (max < min) {
            throw new IllegalArgumentException("max < min: " + min + " > " + max);
        }
        this.min = min;
        this.max = max;
        this.kind = kind;
        this.sampleCount = sampleCount;
    }

    public static Universe continuous(double min, double max) {
        return new Universe(min, max, Kind.CONTINUOUS, DEFAULT_SAMPLES);
    }

    public static Universe discrete(double min, double max) {
        long span = Math.round(max - min);
        int n = (int) Math.min(MAX_DISCRETE_SAMPLES, Math.max(2, span + 1));
        return new Universe(min, max, Kind.DISCRETE, n);
    }

    /**
     * Punkty próbkowania przestrzeni (włącznie z krańcami). Tablica jest liczona jednokrotnie
     * i współdzielona — przeznaczona wyłącznie do odczytu.
     */
    public double[] samplePoints() {
        double[] xs = cachedSamples;
        if (xs != null) {
            return xs;
        }
        xs = new double[sampleCount];
        double step = sampleCount == 1 ? 0.0 : (max - min) / (sampleCount - 1);
        for (int i = 0; i < sampleCount; i++) {
            xs[i] = min + i * step;
        }
        xs[sampleCount - 1] = max;
        cachedSamples = xs;
        return xs;
    }

    public boolean contains(double x) {
        return x >= min && x <= max;
    }

    /** Przycięcie wartości do [min, max] (dla etykiet połówkowych „i dalej"). */
    public double clamp(double x) {
        return Math.max(min, Math.min(max, x));
    }

    public double min() {
        return min;
    }

    public double max() {
        return max;
    }

    public Kind kind() {
        return kind;
    }

    public int sampleCount() {
        return sampleCount;
    }
}
