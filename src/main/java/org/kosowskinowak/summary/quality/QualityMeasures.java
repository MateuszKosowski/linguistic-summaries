package org.kosowskinowak.summary.quality;

import java.util.List;

import org.kosowskinowak.data.CarRecord;
import org.kosowskinowak.fuzzy.linguistic.Quantifier;
import org.kosowskinowak.summary.LabelExpression;
import org.kosowskinowak.summary.Property;
import org.kosowskinowak.summary.SingleSubjectSummary;

/**
 * Wyznacza miary jakości T1–T11 podsumowania jednopodmiotowego oraz wartość podsumowania
 * optymalnego (ważona suma). Definicje wg literatury (Yager 1982; Kacprzyk, Yager 2001;
 * Zadrożny 2006) — w sprawozdaniu komentujemy znaczenie miar, nie przepisujemy wzorów.
 *
 * <p>Oznaczenia: rekordy poprawne (z kompletem danych dla użytych atrybutów) tworzą zbiór roboczy
 * o liczności n. Miary korzystają z sigma-counts μ po rekordach oraz z liczności zbiorów rozmytych
 * po przestrzeni rozważań.
 */
public final class QualityMeasures {

    private QualityMeasures() {
    }

    public static Quality evaluate(SingleSubjectSummary summary, List<CarRecord> records,
                                   QualityWeights weights) {
        Quantifier q = summary.quantifier();
        boolean relative = q.isRelative();
        LabelExpression s = summary.summarizer();
        List<Property> sAtoms = s.atoms();
        int m = sAtoms.size();

        boolean form2 = summary.hasQualifier();
        LabelExpression r = summary.qualifier().orElse(null);

        int nEff = 0;
        double sumS = 0.0;
        double sumR = 0.0;
        double sumRandS = 0.0;
        int countSpos = 0;
        int countRpos = 0;
        int countRSpos = 0;
        int[] atomPos = new int[m];

        for (CarRecord rec : records) {
            double sDeg = s.degree(rec);
            if (Double.isNaN(sDeg)) {
                continue;
            }
            double rDeg = 1.0;
            if (form2) {
                rDeg = r.degree(rec);
                if (Double.isNaN(rDeg)) {
                    continue;
                }
            }
            nEff++;
            sumS += sDeg;
            if (sDeg > 0) {
                countSpos++;
            }
            // T4: liczność atomów sumaryzatora liczona na tej samej populacji co T3 —
            // w formie 2 tylko wśród rekordów spełniających kwalifikator.
            boolean countAtoms = !form2 || rDeg > 0;
            if (countAtoms) {
                for (int j = 0; j < m; j++) {
                    if (sAtoms.get(j).degree(rec) > 0) {
                        atomPos[j]++;
                    }
                }
            }
            if (form2) {
                sumR += rDeg;
                sumRandS += Math.min(rDeg, sDeg);
                if (rDeg > 0) {
                    countRpos++;
                    if (sDeg > 0) {
                        countRSpos++;
                    }
                }
            }
        }

        if (nEff == 0) {
            return new Quality(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }

        // T1 — stopień prawdziwości
        double quantity;
        if (!form2) {
            quantity = relative ? sumS / nEff : sumS;
        } else if (relative) {
            quantity = sumR > 0 ? sumRandS / sumR : 0.0;
        } else {
            quantity = sumRandS;
        }
        double t1 = clamp(q.degree(quantity));

        // T2 — nieprecyzyjność sumaryzatora: 1 − śr. geom. względnych liczności nośników S_j
        double[] inS = new double[m];
        double[] cardS = new double[m];
        for (int j = 0; j < m; j++) {
            inS[j] = sAtoms.get(j).label().set().relativeSupportCardinality();
            cardS[j] = sAtoms.get(j).label().set().relativeSigmaCardinality();
        }
        double t2 = clamp(1.0 - geometricMean(inS));

        // T3 — stopień pokrycia
        double t3;
        if (!form2) {
            t3 = (double) countSpos / nEff;
        } else {
            t3 = countRpos > 0 ? (double) countRSpos / countRpos : 0.0;
        }
        t3 = clamp(t3);

        // T4 — stopień trafności: |Π r_j − T3|, gdzie r_j liczone na tej samej populacji co T3
        int t4Denom = form2 ? countRpos : nEff;
        double t4;
        if (t4Denom == 0) {
            t4 = 0.0;
        } else {
            double prodRj = 1.0;
            for (int j = 0; j < m; j++) {
                prodRj *= (double) atomPos[j] / t4Denom;
            }
            t4 = clamp(Math.abs(prodRj - t3));
        }

        // T5 — długość podsumowania: 2·(1/2)^|S|
        double t5 = clamp(2.0 * Math.pow(0.5, m));

        // T6 — nieprecyzyjność kwantyfikatora
        double t6 = clamp(1.0 - q.set().relativeSupportCardinality());

        // T7 — kardynalność kwantyfikatora
        double t7 = clamp(1.0 - q.set().relativeSigmaCardinality());

        // T8 — kardynalność sumaryzatora
        double t8 = clamp(1.0 - geometricMean(cardS));

        // T9–T11 — miary kwalifikatora (zero dla formy pierwszej)
        double t9 = 0.0;
        double t10 = 0.0;
        double t11 = 0.0;
        if (form2) {
            List<Property> rAtoms = r.atoms();
            int mr = rAtoms.size();
            double[] inR = new double[mr];
            double[] cardR = new double[mr];
            for (int k = 0; k < mr; k++) {
                inR[k] = rAtoms.get(k).label().set().relativeSupportCardinality();
                cardR[k] = rAtoms.get(k).label().set().relativeSigmaCardinality();
            }
            t9 = clamp(1.0 - geometricMean(inR));
            t10 = clamp(1.0 - geometricMean(cardR));
            t11 = clamp(2.0 * Math.pow(0.5, mr));
        }

        double[] all = {t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11};
        double optimal = weights.weightedSum(all);
        return new Quality(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, optimal);
    }

    private static double geometricMean(double[] values) {
        if (values.length == 0) {
            return 1.0;
        }
        double product = 1.0;
        for (double v : values) {
            product *= v;
        }
        return Math.pow(Math.max(product, 0.0), 1.0 / values.length);
    }

    private static double clamp(double v) {
        if (Double.isNaN(v)) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(1.0, v));
    }
}
