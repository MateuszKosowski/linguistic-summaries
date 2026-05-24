package org.kosowskinowak.fuzzy.sets;

public record Interval(double start, double end) {
    public Interval(double start, double end) {

        if (start > end) {
            throw new IllegalArgumentException("start > end");
        }

        this.start = start;
        this.end = end;
    }

    public boolean contains(double x) {
        return start <= x && x <= end;
    }
}
