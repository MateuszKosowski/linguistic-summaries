package org.kosowskinowak.summary.multi;

import java.util.List;

import org.kosowskinowak.data.CarRecord;
import org.kosowskinowak.summary.LabelExpression;

/**
 * Wyznacza stopień prawdziwości T podsumowania wielopodmiotowego (formy I–IV) wg
 * Niewiadomski, Superson 2014 (wersja type-1). Praca definiuje dla tych form wyłącznie T;
 * inne miary jakości nie są tam podane, więc ich nie liczymy.
 *
 * <p>Σ-count sumaryzatora po podmiocie: {@code Σcount(S₁_P) = Σ_{dᵢ∈P} μ_{S₁}(dᵢ)} (wzór 6).
 * Iloczyn z kwalifikatorem: {@code Σcount((S₁∩S₂)_P) = Σ min(μ_{S₁}, μ_{S₂})} (wzór 16).
 * M_P to liczba krotek podmiotu z poprawnymi danymi.
 */
public final class MultiSubjectMeasures {

    private MultiSubjectMeasures() {
    }

    public static double degreeOfTruth(MultiSubjectSummary summary, List<CarRecord> records) {
        MultiSubjectSummary.Form form = summary.form();
        Subject p1 = summary.p1();
        Subject p2 = summary.p2();
        LabelExpression s1 = summary.summarizer();
        LabelExpression s2 = summary.qualifier().orElse(null);

        boolean qualifierOnP1 = form == MultiSubjectSummary.Form.II
                || form == MultiSubjectSummary.Form.III;
        boolean qualifierOnP2 = form == MultiSubjectSummary.Form.II;

        long mP1 = 0;
        long mP2 = 0;
        double sumP1 = 0.0;
        double sumP2 = 0.0;

        for (CarRecord rec : records) {
            boolean inP1 = p1.contains(rec);
            boolean inP2 = p2.contains(rec);
            if (!inP1 && !inP2) {
                continue;
            }
            double mu1 = s1.degree(rec);
            if (Double.isNaN(mu1)) {
                continue;
            }
            boolean useQualifier = inP1 ? qualifierOnP1 : qualifierOnP2;
            double contribution = mu1;
            if (useQualifier) {
                double mu2 = s2.degree(rec);
                if (Double.isNaN(mu2)) {
                    continue;
                }
                contribution = Math.min(mu1, mu2);
            }
            if (inP1) {
                mP1++;
                sumP1 += contribution;
            } else {
                mP2++;
                sumP2 += contribution;
            }
        }

        if (form == MultiSubjectSummary.Form.IV) {
            double denom = sumP1 + sumP2;
            return denom > 0 ? clamp(sumP1 / denom) : 0.0;
        }

        if (mP1 == 0 || mP2 == 0) {
            return 0.0;
        }
        double n1 = sumP1 / mP1;
        double n2 = sumP2 / mP2;
        double denom = n1 + n2;
        if (denom <= 0) {
            return 0.0;
        }
        double argument = n1 / denom;
        return clamp(summary.quantifier().orElseThrow().degree(argument));
    }

    private static double clamp(double v) {
        if (Double.isNaN(v)) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(1.0, v));
    }
}
