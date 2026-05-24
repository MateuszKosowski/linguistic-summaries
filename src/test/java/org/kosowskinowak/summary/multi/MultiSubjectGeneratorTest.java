package org.kosowskinowak.summary.multi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.kosowskinowak.config.FuzzyConfig;

class MultiSubjectGeneratorTest {

    private static final FuzzyConfig CONFIG = FuzzyConfig.defaults();

    private static int labelCount() {
        return CONFIG.variables().stream().mapToInt(v -> v.labels().size()).sum();
    }

    @Test
    void formIVCoversEverySummarizerLabel() {
        MultiSubjectGenerator gen = new MultiSubjectGenerator(CONFIG);
        List<MultiSubjectSummary> out = gen.formIV(Subject.ofMake("BMW"), Subject.ofMake("Audi"));
        assertEquals(labelCount(), out.size());
        assertTrue(out.stream().allMatch(s -> s.form() == MultiSubjectSummary.Form.IV));
    }

    @Test
    void formIMultipliesLabelsByRelativeQuantifiers() {
        MultiSubjectGenerator gen = new MultiSubjectGenerator(CONFIG);
        List<MultiSubjectSummary> out = gen.formI(Subject.ofMake("BMW"), Subject.ofMake("Audi"));
        assertEquals(labelCount() * CONFIG.relativeQuantifiers().size(), out.size());
        assertTrue(out.stream().allMatch(s -> s.form() == MultiSubjectSummary.Form.I));
        assertTrue(out.stream().allMatch(s -> s.quantifier().isPresent()));
    }
}
