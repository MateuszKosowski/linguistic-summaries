package org.kosowskinowak.fuzzy.sets.mf;

public class TrapezoidalMf implements MembershipFunction {

    private final double a, b, c, d;

    public TrapezoidalMf(double a, double b, double c, double d) {
        if (!(a <= b && b <= c && c <= d)) {
            throw new IllegalArgumentException("ERROR: a <= b <= c <= d");
        }
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    @Override
    public double degreeOfBelonging(double x) {
        if (x < a || x > d) {
            return 0;
        } else if (x >= a && x < b) {
            return (x - a) / (b - a);
        } else if (x >= b && x <= c) {
            return 1;
        } else {
            return (d - x) / (d - c);
        }
    }
}
