package org.kosowskinowak.summary.multi;

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
import org.kosowskinowak.summary.LabelExpression;
import org.kosowskinowak.summary.Property;

/**
 * Oracle policzone ręcznie wg wzorów Niewiadomski, Superson 2014 (type-1).
 * Etykieta "tall" = trójkąt 150/172.5/195 (wzór 33 z pracy):
 * μ(155)=0.2222, μ(165)=0.6667, μ(160)=0.4444, μ(140)=μ(130)=μ(135)=0.
 * Kwantyfikator tożsamościowy μ_Q(x)=x (trapez 0,1,1,1) izoluje argument μ_Q.
 */
class MultiSubjectMeasuresTest {

    private static final double EPS = 0.005;

    private static CarRecord rec(String make, double height) {
        return new CarRecord("id", make, "m", Map.of("height", height));
    }

    private static CarRecord rec(String make, double height, double flag) {
        return new CarRecord("id", make, "m", Map.of("height", height, "flag", flag));
    }

    private static LabelExpression tall() {
        Universe u = Universe.continuous(100, 200);
        FuzzySet set = new FuzzySet("tall", u, new TrapezoidalMf(150, 172.5, 172.5, 195));
        LinguisticVariable v = new LinguisticVariable("Wzrost", "height", u, List.of(new Label("tall", set)));
        return new Property(v, v.label("tall").orElseThrow());
    }

    /** Kwalifikator binarny: μ=1 dla flag=1, μ=0 dla flag=0 (trapez 0.5,0.5,1.5,1.5 na [0,2]). */
    private static LabelExpression flagged() {
        Universe u = Universe.continuous(0, 2);
        FuzzySet set = new FuzzySet("flagged", u, new TrapezoidalMf(0.5, 0.5, 1.5, 1.5));
        LinguisticVariable v = new LinguisticVariable("Flaga", "flag", u, List.of(new Label("flagged", set)));
        return new Property(v, v.label("flagged").orElseThrow());
    }

    private static Quantifier identity() {
        FuzzySet set = new FuzzySet("id", Universe.continuous(0, 1), new TrapezoidalMf(0, 1, 1, 1));
        return new Quantifier("identyczność", Quantifier.Type.RELATIVE, set);
    }

    @Test
    void formIEqualSubjectSizes() {
        List<CarRecord> data = List.of(
                rec("boy", 155), rec("boy", 140), rec("boy", 165),
                rec("girl", 160), rec("girl", 130), rec("girl", 135));
        MultiSubjectSummary s = MultiSubjectSummary.formI(
                identity(), Subject.ofMake("boy"), Subject.ofMake("girl"), tall());
        // arg = (0.8889/3) / ((0.8889/3)+(0.4444/3)) = 0.6667
        assertEquals(0.6667, MultiSubjectMeasures.degreeOfTruth(s, data), EPS);
    }

    @Test
    void formIVRawCountComparison() {
        List<CarRecord> data = List.of(
                rec("boy", 155), rec("boy", 140), rec("boy", 165),
                rec("girl", 160), rec("girl", 130), rec("girl", 135));
        MultiSubjectSummary s = MultiSubjectSummary.formIV(
                Subject.ofMake("boy"), Subject.ofMake("girl"), tall());
        // 0.8889 / (0.8889 + 0.4444) = 0.6667
        assertEquals(0.6667, MultiSubjectMeasures.degreeOfTruth(s, data), EPS);
    }

    @Test
    void formINormalizesBySubjectSizeUnlikeFormIV() {
        List<CarRecord> data = List.of(
                rec("boy", 165),                       // 1 boy, μ=0.6667
                rec("girl", 160), rec("girl", 130));   // 2 girls, μ=0.4444 + 0
        MultiSubjectSummary i = MultiSubjectSummary.formI(
                identity(), Subject.ofMake("boy"), Subject.ofMake("girl"), tall());
        MultiSubjectSummary iv = MultiSubjectSummary.formIV(
                Subject.ofMake("boy"), Subject.ofMake("girl"), tall());
        // I: (0.6667/1)/((0.6667/1)+(0.4444/2)) = 0.75 ; IV: 0.6667/(0.6667+0.4444) = 0.6
        assertEquals(0.75, MultiSubjectMeasures.degreeOfTruth(i, data), EPS);
        assertEquals(0.60, MultiSubjectMeasures.degreeOfTruth(iv, data), EPS);
    }

    @Test
    void formIIQualifierAppliesToBothSubjects() {
        List<CarRecord> data = List.of(
                rec("boy", 155, 1), rec("boy", 140, 1), rec("boy", 165, 0),
                rec("girl", 160, 0), rec("girl", 130, 1), rec("girl", 135, 1));
        MultiSubjectSummary s = MultiSubjectSummary.formII(
                identity(), Subject.ofMake("boy"), Subject.ofMake("girl"), flagged(), tall());
        // P1 qualified = min(0.2222,1)=0.2222 ; P2 qualified = 0 (only tall girl has flag0)
        // arg = (0.2222/3)/((0.2222/3)+(0/3)) = 1.0
        assertEquals(1.0, MultiSubjectMeasures.degreeOfTruth(s, data), EPS);
    }

    @Test
    void formIIIQualifierAppliesToP1Only() {
        List<CarRecord> data = List.of(
                rec("boy", 155, 1), rec("boy", 140, 1), rec("boy", 165, 0),
                rec("girl", 160, 0), rec("girl", 130, 1), rec("girl", 135, 1));
        MultiSubjectSummary s = MultiSubjectSummary.formIII(
                identity(), Subject.ofMake("boy"), Subject.ofMake("girl"), flagged(), tall());
        // P1 qualified = 0.2222 ; P2 plain tall = 0.4444
        // arg = (0.2222/3)/((0.2222/3)+(0.4444/3)) = 0.3333
        assertEquals(0.3333, MultiSubjectMeasures.degreeOfTruth(s, data), EPS);
    }

    @Test
    void emptySubjectYieldsZero() {
        List<CarRecord> data = List.of(rec("girl", 160), rec("girl", 130));
        MultiSubjectSummary s = MultiSubjectSummary.formI(
                identity(), Subject.ofMake("boy"), Subject.ofMake("girl"), tall());
        assertEquals(0.0, MultiSubjectMeasures.degreeOfTruth(s, data), 1e-9);
    }
}
