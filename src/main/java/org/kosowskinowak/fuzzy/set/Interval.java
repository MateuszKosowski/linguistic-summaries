package org.kosowskinowak.fuzzy.set;

/**
 * Domknięty przedział [lo, hi] reprezentujący nośnik lub α-przekrój zbioru rozmytego.
 * Dla zbioru pustego {@link #empty} jest prawdą.
 */
public record Interval(double lo, double hi, boolean empty) {

    public static Interval emptyInterval() {
        return new Interval(Double.NaN, Double.NaN, true);
    }

    public static Interval of(double lo, double hi) {
        return new Interval(lo, hi, false);
    }

    @Override
    public String toString() {
        return empty ? "∅" : String.format("[%.3f, %.3f]", lo, hi);
    }
}
