package org.kosowskinowak.summary.multi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import org.kosowskinowak.summary.LabelExpression;
import org.kosowskinowak.summary.Property;

class MultiSubjectSummaryTest {

    private static CarRecord rec(String make, double height) {
        return new CarRecord("id", make, "model", Map.of("height", height));
    }

    private static LabelExpression tall() {
        Universe u = Universe.continuous(100, 200);
        FuzzySet set = new FuzzySet("tall", u, new TrapezoidalMf(150, 172.5, 172.5, 195));
        LinguisticVariable v = new LinguisticVariable("Wzrost", "height", u, List.of(new Label("tall", set)));
        return new Property(v, v.label("tall").orElseThrow());
    }

    private static Quantifier most() {
        FuzzySet set = new FuzzySet("większość", Universe.continuous(0, 1),
                new TrapezoidalMf(0.6, 0.7, 0.85, 0.95));
        return new Quantifier("większość", Quantifier.Type.RELATIVE, set);
    }

    @Test
    void subjectOfMakeMatchesByMake() {
        Subject boys = Subject.ofMake("boy");
        assertTrue(boys.contains(rec("boy", 150)));
        assertFalse(boys.contains(rec("girl", 150)));
        assertEquals("boy", boys.name());
    }

    @Test
    void formIHasQuantifierAndComparisonSentence() {
        MultiSubjectSummary s = MultiSubjectSummary.formI(
                most(), Subject.ofMake("boy"), Subject.ofMake("girl"), tall());
        assertEquals(MultiSubjectSummary.Form.I, s.form());
        assertTrue(s.quantifier().isPresent());
        String sentence = s.sentence();
        assertTrue(sentence.contains("w porównaniu do"), sentence);
        assertTrue(sentence.contains("boy"), sentence);
        assertTrue(sentence.contains("girl"), sentence);
    }

    @Test
    void formIVHasNoQuantifierAndStartsWithMore() {
        MultiSubjectSummary s = MultiSubjectSummary.formIV(
                Subject.ofMake("boy"), Subject.ofMake("girl"), tall());
        assertEquals(MultiSubjectSummary.Form.IV, s.form());
        assertFalse(s.quantifier().isPresent());
        assertTrue(s.sentence().startsWith("Więcej"), s.sentence());
    }

    @Test
    void formIIAndIIIExposeQualifier() {
        MultiSubjectSummary ii = MultiSubjectSummary.formII(
                most(), Subject.ofMake("boy"), Subject.ofMake("girl"), tall(), tall());
        MultiSubjectSummary iii = MultiSubjectSummary.formIII(
                most(), Subject.ofMake("boy"), Subject.ofMake("girl"), tall(), tall());
        assertTrue(ii.qualifier().isPresent());
        assertTrue(iii.qualifier().isPresent());
        assertEquals(MultiSubjectSummary.Form.II, ii.form());
        assertEquals(MultiSubjectSummary.Form.III, iii.form());
    }
}
