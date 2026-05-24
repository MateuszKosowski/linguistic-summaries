package org.kosowskinowak.fuzzy.sets;

import org.kosowskinowak.fuzzy.sets.mf.MembershipFunction;
import org.kosowskinowak.fuzzy.universe.Universe;

public class FuzzySet {

    private static final int SAMPLES = 1000;
    private static final double EPSILON = 1e-3;

    private final String name;
    private final Universe universe;
    private final MembershipFunction mf;

    public FuzzySet(String name, Universe universe, MembershipFunction mf) {
        this.name = name;
        this.universe = universe;
        this.mf = mf;
    }

    public double calculateMembership(double x) {
        return mf.degreeOfBelonging(x);
    }

    public String getName() {
        return name;
    }

    public FuzzySet complement() {
        return new FuzzySet(
                "not " + name,
                universe,
                x -> 1.0 - mf.degreeOfBelonging(x));
    }

    public FuzzySet union(FuzzySet other) {
        return new FuzzySet(
                name + " ∪ " + other.name,
                universe,
                x -> Math.max(mf.degreeOfBelonging(x), other.mf.degreeOfBelonging(x)));
    }

    public FuzzySet intersect(FuzzySet other) {
        return new FuzzySet(
                name + " ∩ " + other.name,
                universe,
                x -> Math.min(mf.degreeOfBelonging(x), other.mf.degreeOfBelonging(x)));
    }

    public double height(){
        double max = 0.0;
        for (double x : sampleUniverse()) {
            double mu = mf.degreeOfBelonging(x);
            if (mu > max) max = mu;
        }
        return max;
    }

    public boolean isEmpty() { return height() < EPSILON; } // height = 0

    public boolean isNormal() { return height() >= 1.0 - EPSILON; } // height = 1

    // no dips in the membership function
    public boolean isConvex() {
        double[] xs = sampleUniverse();
        boolean wasDecreasing = false;
        double prev = mf.degreeOfBelonging(xs[0]);
        for (int i = 1; i < xs.length; i++) {
            double curr = mf.degreeOfBelonging(xs[i]);
            if (curr < prev - EPSILON) wasDecreasing = true;
            else if (curr > prev + EPSILON && wasDecreasing) return false;
            prev = curr;
        }
        return true;
    }

    private double[] sampleUniverse() {
        double[] samples = new double[SAMPLES];
        double step = (universe.max() - universe.min()) / (SAMPLES - 1);
        for (int i = 0; i < SAMPLES; i++) {
            samples[i] = universe.min() + i * step;
        }
        samples[SAMPLES - 1] = universe.max();
        return samples;
    }
}
