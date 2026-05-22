package org.kosowskinowak.summary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.kosowskinowak.config.FuzzyConfig;
import org.kosowskinowak.data.Columns;

class SummaryGeneratorTest {

    private final SummaryGenerator gen = new SummaryGenerator(FuzzyConfig.defaults());

    @Test
    void simpleForm1Counts() {
        // 12 zmiennych × 5 etykiet × 5 kwantyfikatorów względnych
        List<SingleSubjectSummary> rel = gen.simpleForm1(false);
        assertEquals(300, rel.size());
        assertTrue(rel.stream().noneMatch(SingleSubjectSummary::hasQualifier));

        // × 4 kwantyfikatory bezwzględne
        assertEquals(240, gen.simpleForm1(true).size());
    }

    @Test
    void simpleForm2ExcludesQualifierVariable() {
        // 11 pozostałych zmiennych × 5 etykiet × 5 kwantyfikatorów
        List<SingleSubjectSummary> f2 = gen.simpleForm2(Columns.CURB_WEIGHT, "ciężki");
        assertEquals(275, f2.size());
        assertTrue(f2.stream().allMatch(SingleSubjectSummary::hasQualifier));
    }

    @Test
    void compoundForm1Counts() {
        // 5 etykiet × 5 etykiet × 5 kwantyfikatorów
        List<SingleSubjectSummary> c = gen.compoundForm1(
                Connective.Kind.AND, Columns.ENGINE_HP, Columns.MAX_SPEED);
        assertEquals(125, c.size());
    }

    @Test
    void invalidArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> gen.simpleForm2(Columns.CURB_WEIGHT, "nieistnieje"));
        assertThrows(IllegalArgumentException.class,
                () -> gen.compoundForm1(Connective.Kind.OR, Columns.ENGINE_HP, Columns.ENGINE_HP));
    }

    @Test
    void compoundExclusionSanity() {
        assertFalse(gen.simpleForm1(false).isEmpty());
    }
}
