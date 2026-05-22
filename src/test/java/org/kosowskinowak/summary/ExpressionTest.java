package org.kosowskinowak.summary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

class ExpressionTest {

    private static final double EPS = 1e-6;

    /** Zmienna z etykietą-rampą μ(x) = x/10 na [0,10] (kolumna = nazwa). */
    private static LinguisticVariable ramp(String column) {
        Universe u = Universe.continuous(0, 10);
        FuzzySet set = new FuzzySet(column, u, new TrapezoidalMf(0, 10, 10, 10));
        return new LinguisticVariable(column, column, u, List.of(new Label("duży", set)));
    }

    private static Property prop(LinguisticVariable v) {
        return new Property(v, v.label("duży").orElseThrow());
    }

    private static CarRecord rec(Map<String, Double> attrs) {
        return new CarRecord("id", "Mk", "Md", attrs);
    }

    @Test
    void propertyDegreeAndNaN() {
        LinguisticVariable v = ramp("x");
        Property p = prop(v);
        assertEquals(0.7, p.degree(rec(Map.of("x", 7.0))), EPS);
        assertTrue(Double.isNaN(p.degree(rec(Map.of("y", 1.0))))); // brak kolumny x
        assertEquals(1, p.atoms().size());
        assertEquals(1, p.length());
    }

    @Test
    void connectiveAndOr() {
        Property a = prop(ramp("x"));
        Property b = prop(ramp("y"));
        CarRecord r = rec(Map.of("x", 7.0, "y", 4.0)); // A=0.7, B=0.4
        assertEquals(0.4, Connective.and(a, b).degree(r), EPS);
        assertEquals(0.7, Connective.or(a, b).degree(r), EPS);
    }

    @Test
    void connectiveNaNPropagates() {
        Property a = prop(ramp("x"));
        Property b = prop(ramp("y"));
        CarRecord r = rec(Map.of("x", 7.0)); // brak y => B=NaN
        assertTrue(Double.isNaN(Connective.and(a, b).degree(r)));
        assertTrue(Double.isNaN(Connective.or(a, b).degree(r)));
    }

    @Test
    void nestedConnective() {
        Property a = prop(ramp("a"));
        Property b = prop(ramp("b"));
        Property c = prop(ramp("c"));
        CarRecord r = rec(Map.of("a", 2.0, "b", 9.0, "c", 5.0)); // 0.2, 0.9, 0.5
        LabelExpression expr = Connective.or(Connective.and(a, b), c); // or(0.2, 0.5)
        assertEquals(0.5, expr.degree(r), EPS);
        assertEquals(3, expr.length());
        assertEquals(List.of(a, b, c), expr.atoms());
    }

    @Test
    void textParenthesisesNestedConnective() {
        Property a = prop(ramp("a"));
        Property b = prop(ramp("b"));
        Property c = prop(ramp("c"));
        String text = Connective.or(Connective.and(a, b), c).text();
        assertTrue(text.contains("(") && text.contains(" lub "), text);
    }

    @Test
    void connectiveRequiresTwoParts() {
        Property a = prop(ramp("x"));
        assertThrows(IllegalArgumentException.class, () -> Connective.and(a));
    }

    @Test
    void summarySentences() {
        LinguisticVariable v = ramp("x");
        Property s = prop(v);
        FuzzySet most = new FuzzySet("większość", Universe.continuous(0, 1),
                new TrapezoidalMf(0.6, 0.7, 0.85, 0.95));
        Quantifier q = new Quantifier("większość", Quantifier.Type.RELATIVE, most);

        SingleSubjectSummary form1 = new SingleSubjectSummary(q, s);
        assertFalse(form1.hasQualifier());
        assertTrue(form1.sentence().startsWith("Większość aut ma:"));

        Property qualifier = prop(ramp("y"));
        SingleSubjectSummary form2 = new SingleSubjectSummary(q, qualifier, s);
        assertTrue(form2.hasQualifier());
        assertTrue(form2.sentence().contains("które spełniają ["));
    }
}
