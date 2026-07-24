package org.kosowskinowak.fuzzy.mf;

import net.sourceforge.jFuzzyLogic.membership.MembershipFunctionTriangular;
import net.sourceforge.jFuzzyLogic.membership.Value;

/**
 * Funkcja trójkątna o parametrach a ≤ b ≤ c: rośnie na [a, b], osiąga 1 w b, maleje na [b, c].
 * Delegacja do jFuzzyLogic. Dla użytkownika zaawansowanego (definiowanie własnych etykiet).
 */
public final class TriangularMf implements MembershipFunction {

    private final double a;
    private final double b;
    private final double c;
    private final MembershipFunctionTriangular delegate;

    public TriangularMf(double a, double b, double c) {
        if (!(a <= b && b <= c)) {
            throw new IllegalArgumentException(
                    "Wymagane a <= b <= c, otrzymano: " + a + ", " + b + ", " + c);
        }
        this.a = a;
        this.b = b;
        this.c = c;
        this.delegate = new MembershipFunctionTriangular(new Value(a), new Value(b), new Value(c));
    }

    @Override
    public double degree(double x) {
        return TrapezoidalMf.clamp(delegate.membership(x));
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
}
