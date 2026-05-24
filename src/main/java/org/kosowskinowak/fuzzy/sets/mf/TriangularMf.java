package org.kosowskinowak.fuzzy.sets.mf;

public class TriangularMf implements MembershipFunction {
    private final double a, b, c;

    public TriangularMf(double a, double b, double c) {
        if (!(a <= b && b <= c)) {
            throw new IllegalArgumentException("ERROR: a <= b <= c");
        }
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public double degreeOfBelonging(double x) {
        if (x < a || x > c) {
            return 0;
        } else if (x >= a && x < b) {
            return (x - a) / (b - a);
        } else if (x == b) {
            return 1;
        } else {
            return (c - x) / (c - b);
        }
    }
}
