package org.kosowskinowak.fuzzy.mf;

import net.sourceforge.jFuzzyLogic.membership.MembershipFunctionTrapetzoidal;
import net.sourceforge.jFuzzyLogic.membership.Value;

/**
 * Funkcja trapezoidalna o parametrach a ≤ b ≤ c ≤ d. Rośnie na [a, b], jest równa 1 na [b, c]
 * i maleje na [c, d]. Delegacja obliczeń do jFuzzyLogic. Trapez połówkowy uzyskuje się przez
 * a = b (otwarty z lewej) lub c = d (otwarty z prawej).
 */
public final class TrapezoidalMf implements MembershipFunction {

    private final double a;
    private final double b;
    private final double c;
    private final double d;
    private final MembershipFunctionTrapetzoidal delegate;

    public TrapezoidalMf(double a, double b, double c, double d) {
        if (!(a <= b && b <= c && c <= d)) {
            throw new IllegalArgumentException(
                    "Wymagane a <= b <= c <= d, otrzymano: " + a + ", " + b + ", " + c + ", " + d);
        }
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.delegate = new MembershipFunctionTrapetzoidal(
                new Value(a), new Value(b), new Value(c), new Value(d));
    }

    @Override
    public double degree(double x) {
        return clamp(delegate.membership(x));
    }

    public double a() {
        return a;
    }

    public double b() {
        return b;
    }

    public double c() {
        return c;
    }

    public double d() {
        return d;
    }

    static double clamp(double v) {
        if (Double.isNaN(v) || v < 0.0) {
            return 0.0;
        }
        return Math.min(v, 1.0);
    }
}
