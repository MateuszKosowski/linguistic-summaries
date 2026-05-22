package org.kosowskinowak.summary.quality;

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
import org.kosowskinowak.summary.Connective;
import org.kosowskinowak.summary.Property;
import org.kosowskinowak.summary.SingleSubjectSummary;

class QualityMeasuresExtraTest {

    private static final double TOL = 0.02;
    private static final QualityWeights W = QualityWeights.equal();

    /** Zmienna z etykietą „high": μ=1 dla x≥5, 0 poniżej, na [0,10]. */
    private static LinguisticVariable highVar(String column) {
        Universe u = Universe.continuous(0, 10);
        FuzzySet high = new FuzzySet("high", u, new TrapezoidalMf(5, 5, 10, 10));
        return new LinguisticVariable("V", column, u, List.of(new Label("high", high)));
    }

    private static Property prop(LinguisticVariable v) {
        return new Property(v, v.label("high").orElseThrow());
    }

    private static Quantifier aboutHalf() {
        FuzzySet half = new FuzzySet("około połowy", Universe.continuous(0, 1),
                new TrapezoidalMf(0.3, 0.4, 0.6, 0.7));
        return new Quantifier("około połowy", Quantifier.Type.RELATIVE, half);
    }

    private static CarRecord r1(double x) {
        return new CarRecord("id", "Mk", "Md", Map.of("x", x));
    }

    private static CarRecord r2(double x, double y) {
        return new CarRecord("id", "Mk", "Md", Map.of("x", x, "y", y));
    }

    @Test
    void dataIndependentAndSetMeasuresForm1() {
        LinguisticVariable v = highVar("x");
        List<CarRecord> data = List.of(r1(0), r1(2), r1(4), r1(6), r1(8), r1(10)); // proporcja 0.5
        Quality q = QualityMeasures.evaluate(new SingleSubjectSummary(aboutHalf(), prop(v)), data, W);

        assertEquals(1.0, q.t1(), TOL);   // 0.5 trafia w szczyt "około połowy"
        assertEquals(0.5, q.t2(), TOL);   // nieprecyzyjność sumaryzatora (nośnik ~0.5 przestrzeni)
        assertEquals(0.5, q.t3(), TOL);   // pokrycie 3/6
        assertEquals(1.0, q.t5(), 1e-9);  // długość: 2*(1/2)^1
        assertEquals(0.6, q.t6(), TOL);   // nieprecyzyjność kwantyfikatora
        assertEquals(0.7, q.t7(), TOL);   // kardynalność kwantyfikatora
        assertEquals(0.5, q.t8(), TOL);   // kardynalność sumaryzatora
        assertEquals(0.0, q.t9(), 1e-9);  // brak kwalifikatora => T9..T11 = 0
        assertEquals(0.0, q.t10(), 1e-9);
        assertEquals(0.0, q.t11(), 1e-9);
    }

    @Test
    void absoluteQuantifierForm1() {
        LinguisticVariable v = highVar("x");
        List<CarRecord> data = List.of(r1(6), r1(8), r1(10)); // sigma-count = 3
        FuzzySet aroundThree = new FuzzySet("około trzech", Universe.discrete(0, 10),
                new TrapezoidalMf(1, 3, 3, 5));
        Quantifier q = new Quantifier("około trzech", Quantifier.Type.ABSOLUTE, aroundThree);
        Quality quality = QualityMeasures.evaluate(new SingleSubjectSummary(q, prop(v)), data, W);
        assertEquals(1.0, quality.t1(), TOL);
    }

    @Test
    void appropriatenessNonZeroForCompound() {
        LinguisticVariable vx = highVar("x");
        LinguisticVariable vy = highVar("y");
        var summarizer = Connective.and(prop(vx), prop(vy));
        // x>=5 w 2/4, y>=5 w 3/4, oba (min>0) w 2/4
        List<CarRecord> data = List.of(r2(6, 6), r2(6, 8), r2(0, 6), r2(0, 0));
        Quality q = QualityMeasures.evaluate(new SingleSubjectSummary(aboutHalf(), summarizer), data, W);

        assertEquals(0.5, q.t3(), TOL);     // 2/4
        assertEquals(0.125, q.t4(), TOL);   // |0.375 - 0.5|
        assertEquals(0.5, q.t5(), 1e-9);    // 2 atomy => 2*(1/2)^2
    }

    @Test
    void form2QualifierMeasures() {
        LinguisticVariable vx = highVar("x"); // kwalifikator
        LinguisticVariable vy = highVar("y"); // sumaryzator
        List<CarRecord> data = List.of(r2(6, 6), r2(6, 0), r2(0, 6), r2(0, 0));
        SingleSubjectSummary s = new SingleSubjectSummary(aboutHalf(), prop(vx), prop(vy));
        Quality q = QualityMeasures.evaluate(s, data, W);

        assertEquals(1.0, q.t1(), TOL);   // (Σ min(R,S))/ΣR = 1/2 = 0.5 -> szczyt "około połowy"
        assertEquals(0.5, q.t3(), TOL);   // countRSpos/countRpos = 1/2
        assertEquals(1.0, q.t11(), 1e-9); // długość kwalifikatora (1 atom)
        assertEquals(0.5, q.t9(), TOL);   // nieprecyzyjność kwalifikatora
        assertEquals(0.5, q.t10(), TOL);  // kardynalność kwalifikatora
    }

    @Test
    void emptyDatasetYieldsZeros() {
        LinguisticVariable v = highVar("x");
        Quality q = QualityMeasures.evaluate(new SingleSubjectSummary(aboutHalf(), prop(v)), List.of(), W);
        assertEquals(0.0, q.t1(), 1e-9);
        assertEquals(0.0, q.optimal(), 1e-9);
    }

    @Test
    void allInvalidDatasetYieldsZeros() {
        Universe u = Universe.continuous(1, 10); // min>0 => x=0 jest brakiem danych
        FuzzySet high = new FuzzySet("high", u, new TrapezoidalMf(5, 5, 10, 10));
        LinguisticVariable v = new LinguisticVariable("V", "x", u, List.of(new Label("high", high)));
        List<CarRecord> data = List.of(r1(0), r1(0), r1(0));
        Quality q = QualityMeasures.evaluate(
                new SingleSubjectSummary(aboutHalf(), new Property(v, v.label("high").orElseThrow())),
                data, W);
        assertEquals(0.0, q.t1(), 1e-9);
        assertEquals(0.0, q.t3(), 1e-9);
    }
}
