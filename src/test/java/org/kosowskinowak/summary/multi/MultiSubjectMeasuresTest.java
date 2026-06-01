package org.kosowskinowak.summary.multi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kosowskinowak.config.FuzzyConfig;
import org.kosowskinowak.data.CarRecord;
import org.kosowskinowak.data.Columns;
import org.kosowskinowak.fuzzy.linguistic.Quantifier;
import org.kosowskinowak.fuzzy.set.FuzzySet;
import org.kosowskinowak.fuzzy.set.Universe;
import org.kosowskinowak.summary.LabelExpression;
import org.kosowskinowak.summary.Property;

final class MultiSubjectMeasuresTest {

    private static final Subject BMW = Subject.ofMake("BMW");
    private static final Subject TOYOTA = Subject.ofMake("Toyota");
    private static final Quantifier IDENTITY = new Quantifier(
            "większość",
            Quantifier.Type.RELATIVE,
            new FuzzySet("identity", Universe.continuous(0.0, 1.0), x -> x));

    @Test
    void formIIAppliesQualifierToP1AndP2() {
        List<CarRecord> records = List.of(
                car("1", "BMW"),
                car("2", "Toyota"));
        LabelExpression summarizer = fake("moc = dynamiczny", record -> 1.0);
        LabelExpression qualifier = fake("masa = ciężki",
                record -> "BMW".equals(record.make()) ? 0.25 : 0.75);

        double truth = MultiSubjectMeasures.degreeOfTruth(
                MultiSubjectSummary.formII(IDENTITY, BMW, TOYOTA, qualifier, summarizer),
                records);

        assertEquals(0.25, truth, 1e-9);
    }

    @Test
    void formIIIAppliesQualifierOnlyToP1() {
        List<CarRecord> records = List.of(
                car("1", "BMW"),
                car("2", "Toyota"));
        LabelExpression summarizer = fake("moc = dynamiczny", record -> 1.0);
        LabelExpression qualifier = fake("masa = ciężki",
                record -> "BMW".equals(record.make()) ? 0.25 : 0.75);

        double truth = MultiSubjectMeasures.degreeOfTruth(
                MultiSubjectSummary.formIII(IDENTITY, BMW, TOYOTA, qualifier, summarizer),
                records);

        assertEquals(0.2, truth, 1e-9);
    }

    @Test
    void formIISentenceSaysBothSubjectsAreQualified() {
        LabelExpression summarizer = fake("moc = dynamiczny", record -> 1.0);
        LabelExpression qualifier = fake("masa = ciężki", record -> 1.0);

        String sentence = MultiSubjectSummary
                .formII(IDENTITY, BMW, TOYOTA, qualifier, summarizer)
                .sentence();

        assertTrue(sentence.contains("BMW spełniających [masa = ciężki]"));
        assertTrue(sentence.contains("w porównaniu do aut Toyota spełniających [masa = ciężki]"));
        assertFalse(sentence.contains("Toyota, które spełniają"));
    }

    @Test
    void formIISkipsSummarizersFromSimpleQualifierVariable() {
        FuzzyConfig config = FuzzyConfig.defaults();
        Property qualifier = new Property(
                config.variable(Columns.CURB_WEIGHT),
                config.variable(Columns.CURB_WEIGHT).label("ciężki").orElseThrow());

        List<MultiSubjectSummary> summaries = new MultiSubjectGenerator(config)
                .formII(BMW, TOYOTA, qualifier);

        assertFalse(summaries.isEmpty());
        assertTrue(summaries.stream().allMatch(summary -> summary.summarizer().atoms().stream()
                .map(Property::variable)
                .noneMatch(variable -> variable.column().equals(Columns.CURB_WEIGHT))));
    }

    @Test
    void formIIISkipsSummarizersFromSimpleQualifierVariable() {
        FuzzyConfig config = FuzzyConfig.defaults();
        Property qualifier = new Property(
                config.variable(Columns.CURB_WEIGHT),
                config.variable(Columns.CURB_WEIGHT).label("ciężki").orElseThrow());

        List<MultiSubjectSummary> summaries = new MultiSubjectGenerator(config)
                .formIII(BMW, TOYOTA, qualifier);

        assertFalse(summaries.isEmpty());
        assertTrue(summaries.stream().allMatch(summary -> summary.summarizer().atoms().stream()
                .map(Property::variable)
                .noneMatch(variable -> variable.column().equals(Columns.CURB_WEIGHT))));
    }

    private static CarRecord car(String id, String make) {
        return new CarRecord(id, make, "model", Map.of());
    }

    private static LabelExpression fake(String text, Degree degree) {
        return new LabelExpression() {
            @Override
            public double degree(CarRecord record) {
                return degree.apply(record);
            }

            @Override
            public String text() {
                return text;
            }

            @Override
            public List<Property> atoms() {
                return List.of();
            }
        };
    }

    @FunctionalInterface
    private interface Degree {
        double apply(CarRecord record);
    }
}
