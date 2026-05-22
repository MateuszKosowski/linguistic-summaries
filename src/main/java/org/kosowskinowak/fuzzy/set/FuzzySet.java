package org.kosowskinowak.fuzzy.set;

import org.kosowskinowak.fuzzy.mf.MembershipFunction;
import org.kosowskinowak.fuzzy.mf.Operations;

/**
 * Zbiór rozmyty: nazwa, przestrzeń rozważań i funkcja przynależności. Udostępnia własności
 * wymagane w projekcie (wysokość, nośnik, α-przekrój, normalność, pustość, wypukłość) oraz
 * operacje teoriomnogościowe (dopełnienie, suma, iloczyn). Własności liczone numerycznie przez
 * próbkowanie przestrzeni.
 */
public final class FuzzySet {

    private static final double EPS = 1e-9;

    private final String name;
    private final Universe universe;
    private final MembershipFunction mf;

    public FuzzySet(String name, Universe universe, MembershipFunction mf) {
        this.name = name;
        this.universe = universe;
        this.mf = mf;
    }

    public double membership(double x) {
        return mf.degree(x);
    }

    /** Wysokość: największy stopień przynależności w całej przestrzeni. */
    public double height() {
        double h = 0.0;
        for (double x : universe.samplePoints()) {
            h = Math.max(h, mf.degree(x));
        }
        return h;
    }

    /**
     * Zbiór normalny: wysokość = 1. Tolerancja uwzględnia próbkowanie — szczyt funkcji gładkiej
     * (Gauss) lub trójkątnej może wypaść między punktami siatki, zaniżając zmierzoną wysokość.
     */
    public boolean isNormal() {
        return height() >= 1.0 - 1e-3;
    }

    /** Zbiór pusty: wysokość = 0. */
    public boolean isEmpty() {
        return height() <= EPS;
    }

    /** Nośnik: {x : μ(x) > 0} jako przedział obejmujący. */
    public Interval support() {
        return levelSet(EPS, false);
    }

    /** α-przekrój: {x : μ(x) ≥ α} jako przedział obejmujący. */
    public Interval alphaCut(double alpha) {
        return levelSet(alpha, true);
    }

    private Interval levelSet(double threshold, boolean inclusive) {
        double lo = Double.NaN;
        double hi = Double.NaN;
        for (double x : universe.samplePoints()) {
            double mu = mf.degree(x);
            boolean in = inclusive ? mu >= threshold : mu > threshold;
            if (in) {
                if (Double.isNaN(lo)) {
                    lo = x;
                }
                hi = x;
            }
        }
        return Double.isNaN(lo) ? Interval.emptyInterval() : Interval.of(lo, hi);
    }

    /**
     * Zbiór wypukły: funkcja przynależności jest unimodalna (rośnie, ewentualnie plateau, maleje),
     * bez „dolin" w środku.
     */
    public boolean isConvex() {
        double[] xs = universe.samplePoints();
        boolean decreasing = false;
        double prev = mf.degree(xs[0]);
        for (int i = 1; i < xs.length; i++) {
            double cur = mf.degree(xs[i]);
            if (cur < prev - EPS) {
                decreasing = true;
            } else if (cur > prev + EPS && decreasing) {
                return false; // wzrost po spadku => dwa szczyty
            }
            prev = cur;
        }
        return true;
    }

    /** Względna liczność nośnika: |{x : μ(x) > 0}| / |X| (miara „in" – nieprecyzyjność). */
    public double relativeSupportCardinality() {
        double[] xs = universe.samplePoints();
        int positive = 0;
        for (double x : xs) {
            if (mf.degree(x) > EPS) {
                positive++;
            }
        }
        return (double) positive / xs.length;
    }

    /** Względna liczność (sigma-count): średni stopień przynależności po przestrzeni. */
    public double relativeSigmaCardinality() {
        double[] xs = universe.samplePoints();
        double sum = 0.0;
        for (double x : xs) {
            sum += mf.degree(x);
        }
        return sum / xs.length;
    }

    public FuzzySet complement() {
        return new FuzzySet("nie " + name, universe, Operations.complement(mf));
    }

    public FuzzySet union(FuzzySet other) {
        return new FuzzySet(name + " ∪ " + other.name, universe, Operations.union(mf, other.mf));
    }

    public FuzzySet intersect(FuzzySet other) {
        return new FuzzySet(name + " ∩ " + other.name, universe, Operations.intersection(mf, other.mf));
    }

    public String name() {
        return name;
    }

    public Universe universe() {
        return universe;
    }

    public MembershipFunction membershipFunction() {
        return mf;
    }
}
