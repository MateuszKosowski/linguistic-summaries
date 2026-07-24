package org.kosowskinowak.summary;

import java.util.ArrayList;
import java.util.List;

import org.kosowskinowak.config.FuzzyConfig;
import org.kosowskinowak.fuzzy.linguistic.Label;
import org.kosowskinowak.fuzzy.linguistic.LinguisticVariable;
import org.kosowskinowak.fuzzy.linguistic.Quantifier;

/** Generuje zbiory kandydujących podsumowań jednopodmiotowych na podstawie konfiguracji rozmytej. */
public final class SummaryGenerator {

    private final FuzzyConfig config;

    public SummaryGenerator(FuzzyConfig config) {
        this.config = config;
    }

    /** Forma pierwsza, prosty sumaryzator: wszystkie etykiety × kwantyfikatory względne. */
    public List<SingleSubjectSummary> simpleForm1(boolean absolute) {
        List<Quantifier> quantifiers = absolute
                ? config.absoluteQuantifiers() : config.relativeQuantifiers();
        List<SingleSubjectSummary> out = new ArrayList<>();
        for (LinguisticVariable var : config.variables()) {
            for (Label label : var.labels()) {
                LabelExpression summarizer = new Property(var, label);
                for (Quantifier q : quantifiers) {
                    out.add(new SingleSubjectSummary(q, summarizer));
                }
            }
        }
        return out;
    }

    /**
     * Forma pierwsza, sumaryzator złożony: dla dwóch różnych zmiennych łączy każdą parę ich etykiet
     * spójnikiem {@code kind} (AND/OR) × kwantyfikatory względne.
     */
    public List<SingleSubjectSummary> compoundForm1(Connective.Kind kind,
                                                    String columnA, String columnB) {
        if (columnA.equals(columnB)) {
            throw new IllegalArgumentException("Forma złożona wymaga różnych zmiennych");
        }
        LinguisticVariable varA = config.variable(columnA);
        LinguisticVariable varB = config.variable(columnB);
        List<SingleSubjectSummary> out = new ArrayList<>();
        for (Label la : varA.labels()) {
            for (Label lb : varB.labels()) {
                LabelExpression summarizer = Connective.of(kind,
                        new Property(varA, la), new Property(varB, lb));
                for (Quantifier q : config.relativeQuantifiers()) {
                    out.add(new SingleSubjectSummary(q, summarizer));
                }
            }
        }
        return out;
    }

    /**
     * Forma druga: dla każdej pary różnych zmiennych (kwalifikator, sumaryzator) bierze pierwszą
     * etykietę kwalifikatora i wszystkie etykiety sumaryzatora × kwantyfikatory względne.
     * Zestaw demonstracyjny — pełna kombinatoryka będzie sterowana z GUI.
     */
    public List<SingleSubjectSummary> simpleForm2(String qualifierColumn, String qualifierLabel) {
        LinguisticVariable qVar = config.variable(qualifierColumn);
        Label qLabel = qVar.label(qualifierLabel)
                .orElseThrow(() -> new IllegalArgumentException("Brak etykiety: " + qualifierLabel));
        LabelExpression qualifier = new Property(qVar, qLabel);

        List<SingleSubjectSummary> out = new ArrayList<>();
        for (LinguisticVariable var : config.variables()) {
            if (var.column().equals(qualifierColumn)) {
                continue;
            }
            for (Label label : var.labels()) {
                LabelExpression summarizer = new Property(var, label);
                for (Quantifier q : config.relativeQuantifiers()) {
                    out.add(new SingleSubjectSummary(q, qualifier, summarizer));
                }
            }
        }
        return out;
    }
}
