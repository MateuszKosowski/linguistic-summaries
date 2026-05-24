package org.kosowskinowak.fuzzy.universe;

public class ContinuousUniverse implements Universe{
    private final double min;
    private final double max;

    public ContinuousUniverse(double min, double max){
        if (min > max){
            throw new IllegalArgumentException("min > max");
        }
        this.min = min;
        this.max = max;
    }

    @Override
    public double min() {
        return min;
    }
    @Override
    public double max() {
        return max;
    }
    @Override
    public boolean contains(double x) {
        return x >= min && x <= max;
    }
}
