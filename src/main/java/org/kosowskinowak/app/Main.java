package org.kosowskinowak.app;

import java.io.File;
import java.util.Comparator;
import java.util.List;

import org.kosowskinowak.config.FuzzyConfig;
import org.kosowskinowak.data.CarDataLoader;
import org.kosowskinowak.data.CarRecord;
import org.kosowskinowak.data.Columns;
import org.kosowskinowak.data.DbImporter;
import org.kosowskinowak.summary.Connective;
import org.kosowskinowak.summary.LabelExpression;
import org.kosowskinowak.summary.Property;
import org.kosowskinowak.summary.SingleSubjectSummary;
import org.kosowskinowak.summary.SummaryGenerator;
import org.kosowskinowak.summary.multi.MultiSubjectGenerator;
import org.kosowskinowak.summary.multi.MultiSubjectMeasures;
import org.kosowskinowak.summary.multi.MultiSubjectSummary;
import org.kosowskinowak.summary.multi.Subject;
import org.kosowskinowak.summary.quality.Quality;
import org.kosowskinowak.summary.quality.QualityMeasures;
import org.kosowskinowak.summary.quality.QualityWeights;

/**
 * Konsolowy runner: ładuje dane przez DBMS (SQLite/JDBC), generuje podsumowania lingwistyczne
 * jednopodmiotowe, liczy miary jakości T1–T11 oraz wartość podsumowania optymalnego i wypisuje
 * rankingi. Dowód, że silnik działa na realnym zbiorze — baza pod diagram UML i sprawozdanie.
 */
public final class Main {

    private static final int TOP = 20;

    public static void main(String[] args) {
        String dbPath = DbImporter.DEFAULT_DB;
        if (!new File(dbPath).exists()) {
            System.out.println("Baza " + dbPath + " nie istnieje — importuję z CSV...");
            new DbImporter().importCsv(DbImporter.DEFAULT_CSV, dbPath);
        }

        List<CarRecord> records = new CarDataLoader(dbPath).load();
        System.out.printf("Wczytano %d rekordów z bazy SQLite (przez JDBC).%n", records.size());

        FuzzyConfig config = FuzzyConfig.defaults();
        SummaryGenerator generator = new SummaryGenerator(config);
        QualityWeights weights = QualityWeights.equal();

        // --- Forma pierwsza, prosty sumaryzator, kwantyfikatory względne ---
        List<SingleSubjectSummary> form1 = generator.simpleForm1(false);
        List<Scored> scored = form1.stream()
                .map(s -> new Scored(s, QualityMeasures.evaluate(s, records, weights)))
                .sorted(Comparator.comparingDouble((Scored x) -> x.q.optimal()).reversed())
                .toList();

        printRanking("PODSUMOWANIA JEDNOPODMIOTOWE — FORMA 1 (kwantyfikatory względne)\n"
                + "Ranking wg podsumowania optymalnego (wagi równe). "
                + scored.size() + " kandydatów.", scored, TOP);
        printDetailTable(scored, 10);

        // --- Forma pierwsza, kwantyfikatory bezwzględne (przykład) ---
        List<Scored> abs = generator.simpleForm1(true).stream()
                .map(s -> new Scored(s, QualityMeasures.evaluate(s, records, weights)))
                .sorted(Comparator.comparingDouble((Scored x) -> x.q.optimal()).reversed())
                .toList();
        printRanking("\nFORMA 1 — kwantyfikatory bezwzględne (TOP 10)", abs, 10);

        // --- Sumaryzator złożony (AND) — przykłady ---
        printCompoundExamples(config, records, weights);

        // --- Forma druga: kwalifikator „masa = ciężki" ---
        List<Scored> form2 = generator.simpleForm2(Columns.CURB_WEIGHT, "ciężki").stream()
                .map(s -> new Scored(s, QualityMeasures.evaluate(s, records, weights)))
                .sorted(Comparator.comparingDouble((Scored x) -> x.q.optimal()).reversed())
                .toList();
        printRanking("\nPODSUMOWANIA JEDNOPODMIOTOWE — FORMA 2 (kwalifikator: masa = ciężki) (TOP 15)",
                form2, 15);

        // --- Podsumowania WIELOPODMIOTOWE: BMW vs Toyota (formy I i IV) ---
        printMultiSubject(config, records, "BMW", "Toyota");
    }

    private static void printMultiSubject(FuzzyConfig config, List<CarRecord> records,
                                          String makeA, String makeB) {
        Subject p1 = Subject.ofMake(makeA);
        Subject p2 = Subject.ofMake(makeB);
        MultiSubjectGenerator gen = new MultiSubjectGenerator(config);

        List<MultiSubjectSummary> candidates = new java.util.ArrayList<>(gen.formIV(p1, p2));
        candidates.addAll(gen.formI(p1, p2));
        List<MultiScored> scored = candidates.stream()
                .map(s -> new MultiScored(s, MultiSubjectMeasures.degreeOfTruth(s, records)))
                .sorted(Comparator.comparingDouble((MultiScored x) -> x.t).reversed())
                .toList();

        System.out.printf("%n=== PODSUMOWANIA WIELOPODMIOTOWE — %s vs %s (formy I, IV) ===%n", makeA, makeB);
        System.out.println("Ranking wg stopnia prawdziwości T (Niewiadomski, Superson 2014). "
                + scored.size() + " kandydatów.");
        int n = Math.min(20, scored.size());
        for (int i = 0; i < n; i++) {
            MultiScored s = scored.get(i);
            System.out.printf("%2d. T=%.3f  | %s%n", i + 1, s.t, s.s.sentence());
        }
    }

    private record MultiScored(MultiSubjectSummary s, double t) {
    }

    private static void printCompoundExamples(FuzzyConfig config, List<CarRecord> records,
                                              QualityWeights weights) {
        LabelExpression powerful = new Property(config.variable(Columns.ENGINE_HP),
                config.variable(Columns.ENGINE_HP).label("dynamiczny").orElseThrow());
        LabelExpression thirsty = new Property(config.variable(Columns.FUEL_CONSUMPTION),
                config.variable(Columns.FUEL_CONSUMPTION).label("paliwożerny").orElseThrow());
        LabelExpression fast = new Property(config.variable(Columns.MAX_SPEED),
                config.variable(Columns.MAX_SPEED).label("wyścigowy").orElseThrow());

        var q = config.relativeQuantifiers().stream()
                .filter(x -> x.name().equals("większość")).findFirst().orElseThrow();

        System.out.println("\nSUMARYZATOR ZŁOŻONY (AND / OR) — przykłady:");
        for (LabelExpression expr : List.of(
                Connective.and(powerful, thirsty),
                Connective.or(fast, powerful),
                Connective.and(powerful, thirsty, fast))) {
            SingleSubjectSummary s = new SingleSubjectSummary(q, expr);
            Quality quality = QualityMeasures.evaluate(s, records, weights);
            System.out.printf("  T1=%.3f  opt=%.3f  | %s%n", quality.t1(), quality.optimal(), s.sentence());
        }
    }

    private static void printRanking(String title, List<Scored> scored, int limit) {
        System.out.println("\n=== " + title + " ===");
        int n = Math.min(limit, scored.size());
        for (int i = 0; i < n; i++) {
            Scored s = scored.get(i);
            System.out.printf("%2d. T1=%.3f  opt=%.3f  | %s%n",
                    i + 1, s.q.t1(), s.q.optimal(), s.s.sentence());
        }
    }

    private static void printDetailTable(List<Scored> scored, int limit) {
        System.out.println("\n--- Miary szczegółowe T1–T11 (TOP " + limit + ") ---");
        System.out.printf("%-3s", "#");
        for (String name : Quality.NAMES) {
            System.out.printf("%7s", name);
        }
        System.out.printf("%8s%n", "OPT");
        int n = Math.min(limit, scored.size());
        for (int i = 0; i < n; i++) {
            Quality q = scored.get(i).q;
            System.out.printf("%-3d", i + 1);
            for (double v : q.toArray()) {
                System.out.printf("%7.3f", v);
            }
            System.out.printf("%8.3f%n", q.optimal());
        }
    }

    private record Scored(SingleSubjectSummary s, Quality q) {
    }
}
