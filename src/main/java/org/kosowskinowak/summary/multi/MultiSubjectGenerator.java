package org.kosowskinowak.summary.multi;

import java.util.ArrayList;
import java.util.List;

import org.kosowskinowak.config.FuzzyConfig;
import org.kosowskinowak.fuzzy.linguistic.Label;
import org.kosowskinowak.fuzzy.linguistic.LinguisticVariable;
import org.kosowskinowak.fuzzy.linguistic.Quantifier;
import org.kosowskinowak.summary.LabelExpression;
import org.kosowskinowak.summary.Property;

/**
 * Generuje kandydujące podsumowania wielopodmiotowe dla pary podmiotów (P₁, P₂).
 * Sumaryzatorami są proste etykiety wszystkich zmiennych lingwistycznych; kwantyfikatory
 * względne pochodzą z konfiguracji. Forma IV nie używa kwantyfikatora.
 */
public final class MultiSubjectGenerator {

    private final FuzzyConfig config;

    public MultiSubjectGenerator(FuzzyConfig config) {
        this.config = config;
    }

    /** Forma IV: {@code Więcej P₁ niż P₂ ma S₁} dla każdej etykiety sumaryzatora. */
    public List<MultiSubjectSummary> formIV(Subject p1, Subject p2) {
        List<MultiSubjectSummary> out = new ArrayList<>();
        for (LinguisticVariable var : config.variables()) {
            for (Label label : var.labels()) {
                out.add(MultiSubjectSummary.formIV(p1, p2, new Property(var, label)));
            }
        }
        return out;
    }

    /** Forma I: każda etykieta sumaryzatora × kwantyfikatory względne. */
    public List<MultiSubjectSummary> formI(Subject p1, Subject p2) {
        List<MultiSubjectSummary> out = new ArrayList<>();
        for (LinguisticVariable var : config.variables()) {
            for (Label label : var.labels()) {
                LabelExpression s1 = new Property(var, label);
                for (Quantifier q : config.relativeQuantifiers()) {
                    out.add(MultiSubjectSummary.formI(q, p1, p2, s1));
                }
            }
        }
        return out;
    }

    /** Forma II: kwalifikator S₂ zawęża oba podmioty; każda etykieta sumaryzatora × kwantyfikatory względne. */
    public List<MultiSubjectSummary> formII(Subject p1, Subject p2, LabelExpression s2) {
        List<MultiSubjectSummary> out = new ArrayList<>();
        for (LinguisticVariable var : config.variables()) {
            if (isSameVariableAsSimpleQualifier(s2, var)) {
                continue;
            }
            for (Label label : var.labels()) {
                LabelExpression s1 = new Property(var, label);
                for (Quantifier q : config.relativeQuantifiers()) {
                    out.add(MultiSubjectSummary.formII(q, p1, p2, s2, s1));
                }
            }
        }
        return out;
    }

    /** Forma III: kwalifikator S₂ zawęża tylko P₁; każda etykieta sumaryzatora × kwantyfikatory względne. */
    public List<MultiSubjectSummary> formIII(Subject p1, Subject p2, LabelExpression s2) {
        List<MultiSubjectSummary> out = new ArrayList<>();
        for (LinguisticVariable var : config.variables()) {
            if (isSameVariableAsSimpleQualifier(s2, var)) {
                continue;
            }
            for (Label label : var.labels()) {
                LabelExpression s1 = new Property(var, label);
                for (Quantifier q : config.relativeQuantifiers()) {
                    out.add(MultiSubjectSummary.formIII(q, p1, p2, s2, s1));
                }
            }
        }
        return out;
    }

    private static boolean isSameVariableAsSimpleQualifier(LabelExpression qualifier, LinguisticVariable var) {
        return qualifier instanceof Property property
                && property.variable().column().equals(var.column());
    }
}
