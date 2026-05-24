package org.kosowskinowak.fuzzy.universe;

public class DiscreteUniverse implements Universe {
    private final long min;
    private final long max;

    public DiscreteUniverse(long min, long max) {
        if (max < min) {
            throw new IllegalArgumentException("max < min");
        }
        this.min = min;
        this.max = max;
    }

    @Override public double min() { return min; }
    @Override public double max() { return max; }

    @Override
    public boolean contains(double x) {
        return x >= min && x <= max && x == Math.rint(x);
    }
}
