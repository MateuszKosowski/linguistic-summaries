package org.kosowskinowak.fuzzy.mf;

/**
 * Operacje na funkcjach przynależności jako dekoratory: dopełnienie (1 − μ),
 * iloczyn (t-norma minimum) i suma (t-konorma maksimum). Te same wzory obsługują
 * zbiory rozmyte oraz — dla wartości 0/1 — zbiory klasyczne.
 */
public final class Operations {

    private Operations() {
    }

    /** Dopełnienie: μ' (x) = 1 − μ(x). */
    public static MembershipFunction complement(MembershipFunction mf) {
        return x -> TrapezoidalMf.clamp(1.0 - mf.degree(x));
    }

    /** Iloczyn (AND): μ(x) = min(μ_a(x), μ_b(x)). */
    public static MembershipFunction intersection(MembershipFunction a, MembershipFunction b) {
        return x -> Math.min(a.degree(x), b.degree(x));
    }

    /** Suma (OR): μ(x) = max(μ_a(x), μ_b(x)). */
    public static MembershipFunction union(MembershipFunction a, MembershipFunction b) {
        return x -> Math.max(a.degree(x), b.degree(x));
    }
}
