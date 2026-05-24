package org.kosowskinowak.fuzzy.sets.mf;

public class GaussianMf implements MembershipFunction {

    private double mean;

    // It is response for the width of the bell curve. The smaller the sigma, the narrower the bell curve.
    private double sigma;
    public GaussianMf(double mean, double sigma) {
        if (sigma <= 0) {
            throw new IllegalArgumentException("sigma must be greater than 0");
        }
        this.mean = mean;
        this.sigma = sigma;
    }

    @Override
    public double degreeOfBelonging(double x) {
        double d = (x - mean) / sigma;
        return Math.exp(-0.5 * d * d);
    }
}
