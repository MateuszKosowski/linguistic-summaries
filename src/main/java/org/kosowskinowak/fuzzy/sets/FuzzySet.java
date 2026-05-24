package org.kosowskinowak.fuzzy.sets;

import org.kosowskinowak.fuzzy.sets.mf.MembershipFunction;
import org.kosowskinowak.fuzzy.universe.Universe;

public class FuzzySet {
    private final String name;
    private final Universe universe;
    private final MembershipFunction mf;

    public FuzzySet(String name, Universe universe, MembershipFunction mf) {
        this.name = name;
        this.universe = universe;
        this.mf = mf;
    }

    public double membershipF(double x) {
        return mf.degreeOfBelonging(x);
    }

    public String getName() {
        return name;
    }
}
