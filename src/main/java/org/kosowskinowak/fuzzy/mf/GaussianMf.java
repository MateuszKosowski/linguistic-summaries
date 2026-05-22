package org.kosowskinowak.fuzzy.mf;

import net.sourceforge.jFuzzyLogic.membership.MembershipFunctionGaussian;
import net.sourceforge.jFuzzyLogic.membership.Value;

/**
 * Funkcja gaussowska o środku (mean) i odchyleniu (sigma). Gładka górka dążąca do zera,
 * nigdy go nie osiągająca. Delegacja do jFuzzyLogic.
 */
public final class GaussianMf implements MembershipFunction {

    private final double mean;
    private final double sigma;
    private final MembershipFunctionGaussian delegate;

    public GaussianMf(double mean, double sigma) {
        if (!(sigma > 0.0)) {
            throw new IllegalArgumentException("Odchylenie sigma musi być > 0, otrzymano: " + sigma);
        }
        this.mean = mean;
        this.sigma = sigma;
        this.delegate = new MembershipFunctionGaussian(new Value(mean), new Value(sigma));
    }

    @Override
    public double degree(double x) {
        return TrapezoidalMf.clamp(delegate.membership(x));
    }

    public double mean() {
        return mean;
    }

    public double sigma() {
        return sigma;
    }
}
