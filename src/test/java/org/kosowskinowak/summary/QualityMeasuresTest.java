package org.kosowskinowak.summary;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kosowskinowak.data.CarRecord;
import org.kosowskinowak.fuzzy.linguistic.Label;
import org.kosowskinowak.fuzzy.linguistic.LinguisticVariable;
import org.kosowskinowak.fuzzy.linguistic.Quantifier;
import org.kosowskinowak.fuzzy.mf.TrapezoidalMf;
import org.kosowskinowak.fuzzy.set.FuzzySet;
import org.kosowskinowak.fuzzy.set.Universe;
import org.kosowskinowak.summary.quality.Quality;
import org.kosowskinowak.summary.quality.QualityMeasures;
import org.kosowskinowak.summary.quality.QualityWeights;

class QualityMeasuresTest {

    private static final double EPS = 1e-6;

    private static CarRecord rec(double x) {
        return new CarRecord("id", "Make", "Model", Map.of("x", x));
    }

    private static LinguisticVariable variable(double min, double max) {
        Universe u = Universe.continuous(min, max);
        // etykieta "high": μ = 1 dla x >= 5, w przeciwnym razie 0
        FuzzySet high = new FuzzySet("high", u, new TrapezoidalMf(5, 5, max, max));
        return new LinguisticVariable("X", "x", u, List.of(new Label("high", high)));
    }

    @Test
    void truthAndCoveringForm1() {
        LinguisticVariable var = variable(0, 10);
        Property summarizer = new Property(var, var.label("high").orElseThrow());

        // proporcja aut "high" = 3/6 = 0.5
        List<CarRecord> data = List.of(rec(0), rec(2), rec(4), rec(6), rec(8), rec(10));

        // kwantyfikator "około połowy": szczyt w 0.5 => T1 = 1.0
        FuzzySet half = new FuzzySet("około połowy", Universe.continuous(0, 1),
                new TrapezoidalMf(0.3, 0.4, 0.6, 0.7));
        Quantifier q = new Quantifier("około połowy", Quantifier.Type.RELATIVE, half);

        Quality quality = QualityMeasures.evaluate(
                new SingleSubjectSummary(q, summarizer), data, QualityWeights.equal());

        assertEquals(1.0, quality.t1(), EPS);   // proporcja 0.5 trafia w szczyt kwantyfikatora
        assertEquals(0.5, quality.t3(), EPS);   // pokrycie = 3/6
        assertEquals(0.0, quality.t4(), EPS);   // dla prostego sumaryzatora r1 == T3
    }

    @Test
    void absoluteQuantifierCountsObjects() {
        LinguisticVariable var = variable(0, 10);
        Property summarizer = new Property(var, var.label("high").orElseThrow());
        List<CarRecord> data = List.of(rec(6), rec(8), rec(10)); // 3 auta high, sigma-count = 3

        FuzzySet aroundThree = new FuzzySet("około trzech", Universe.discrete(0, 10),
                new TrapezoidalMf(1, 3, 3, 5));
        Quantifier q = new Quantifier("około trzech", Quantifier.Type.ABSOLUTE, aroundThree);

        Quality quality = QualityMeasures.evaluate(
                new SingleSubjectSummary(q, summarizer), data, QualityWeights.equal());
        assertEquals(1.0, quality.t1(), EPS); // liczność 3 trafia w szczyt
    }

    @Test
    void invalidValuesAreSkipped() {
        LinguisticVariable var = variable(1, 10); // min > 0 => x=0 to brak danych
        Property summarizer = new Property(var, var.label("high").orElseThrow());
        List<CarRecord> data = List.of(rec(0), rec(6), rec(8)); // x=0 pominięte => n=2, oba high

        FuzzySet almostAll = new FuzzySet("prawie wszystkie", Universe.continuous(0, 1),
                new TrapezoidalMf(0.85, 0.95, 1.0, 1.0));
        Quantifier q = new Quantifier("prawie wszystkie", Quantifier.Type.RELATIVE, almostAll);

        Quality quality = QualityMeasures.evaluate(
                new SingleSubjectSummary(q, summarizer), data, QualityWeights.equal());
        assertEquals(1.0, quality.t1(), EPS); // proporcja 2/2 = 1.0
        assertEquals(1.0, quality.t3(), EPS); // pokrycie liczone po 2 poprawnych rekordach
    }
}
