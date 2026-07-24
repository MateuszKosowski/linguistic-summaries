package org.kosowskinowak.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kosowskinowak.data.Columns;
import org.kosowskinowak.fuzzy.linguistic.Label;
import org.kosowskinowak.fuzzy.linguistic.LinguisticVariable;
import org.kosowskinowak.fuzzy.linguistic.Quantifier;
import org.kosowskinowak.fuzzy.mf.MembershipFunction;
import org.kosowskinowak.fuzzy.mf.TrapezoidalMf;
import org.kosowskinowak.fuzzy.set.FuzzySet;
import org.kosowskinowak.fuzzy.set.Universe;

/**
 * Predefiniowana konfiguracja rozmyta: 12 zmiennych lingwistycznych (po 5 etykiet trapezoidalnych)
 * oraz kwantyfikatory względne i bezwzględne. Wartości parametrów odpowiadają dokładnie projektowi
 * opisanemu w sprawozdaniu (sekcje „Zmienne lingwistyczne" i „Kwantyfikatory lingwistyczne").
 *
 * <p>Na obecnym etapie konfiguracja jest zaszyta w kodzie; docelowo (tryb użytkownika
 * zaawansowanego) zostanie wyniesiona do pliku konfiguracyjnego.
 */
public final class FuzzyConfig {

    private final Map<String, LinguisticVariable> variables = new LinkedHashMap<>();
    private final List<Quantifier> relativeQuantifiers = new ArrayList<>();
    private final List<Quantifier> absoluteQuantifiers = new ArrayList<>();

    private FuzzyConfig() {
    }

    public static FuzzyConfig defaults() {
        FuzzyConfig c = new FuzzyConfig();
        c.buildVariables();
        c.buildQuantifiers();
        return c;
    }

    private void buildVariables() {
        add("Długość pojazdu", Columns.LENGTH, 1500, 6500,
                trap("krótki", 1500, 1500, 3500, 3900),
                trap("kompaktowy", 3500, 3900, 4200, 4500),
                trap("standardowy", 4200, 4500, 4700, 4900),
                trap("długi", 4700, 4900, 5100, 5300),
                trap("wielkogabarytowy", 5100, 5300, 6500, 6500));

        add("Rozstaw osi", Columns.WHEELBASE, 1300, 4000,
                trap("zwarty", 1300, 1300, 2300, 2450),
                trap("krótki", 2300, 2450, 2550, 2650),
                trap("standardowy", 2550, 2650, 2750, 2850),
                trap("wydłużony", 2750, 2850, 2950, 3050),
                trap("limuzynowy", 2950, 3050, 4000, 4000));

        add("Masa własna", Columns.CURB_WEIGHT, 500, 3500,
                trap("piórkowy", 500, 500, 900, 1100),
                trap("lekki", 900, 1100, 1250, 1400),
                trap("średnio ciężki", 1250, 1400, 1500, 1650),
                trap("ciężki", 1500, 1650, 1850, 2050),
                trap("masywny", 1850, 2050, 3500, 3500));

        add("Pojemność bagażnika", Columns.TRUNK, 0, 1500,
                trap("symboliczny", 0, 0, 200, 300),
                trap("ciasny", 200, 300, 350, 400),
                trap("standardowy", 350, 400, 450, 500),
                trap("pojemny", 450, 500, 600, 700),
                trap("ładunkowy", 600, 700, 1500, 1500));

        add("Moment obrotowy", Columns.TORQUE, 0, 1100,
                trap("wiotki", 0, 0, 100, 150),
                trap("miękki", 100, 150, 180, 230),
                trap("zrównoważony", 180, 230, 280, 350),
                trap("mocarny", 280, 350, 450, 550),
                trap("potężny", 450, 550, 1100, 1100));

        add("Pojemność skokowa", Columns.CAPACITY, 400, 8000,
                trap("miniaturowy", 400, 400, 1200, 1400),
                trap("małolitrażowy", 1200, 1400, 1600, 1800),
                trap("średniolitrażowy", 1600, 1800, 2000, 2500),
                trap("wielkolitrażowy", 2000, 2500, 3500, 4500),
                trap("gigantyczny", 3500, 4500, 8000, 8000));

        add("Moc silnika", Columns.ENGINE_HP, 0, 800,
                trap("anemiczny", 0, 0, 70, 100),
                trap("słaby", 70, 100, 120, 150),
                trap("przyzwoity", 120, 150, 180, 220),
                trap("dynamiczny", 180, 220, 300, 400),
                trap("wyczynowy", 300, 400, 800, 800));

        add("Średnica zawracania", Columns.TURNING_CIRCLE, 5, 20,
                trap("zwinny", 5.0, 5.0, 9.5, 10.0),
                trap("zwrotny", 9.5, 10.0, 10.5, 11.0),
                trap("przeciętny", 10.5, 11.0, 11.3, 11.7),
                trap("nieporęczny", 11.3, 11.7, 12.5, 13.5),
                trap("ociężały", 12.5, 13.5, 20.0, 20.0));

        add("Spalanie mieszane", Columns.FUEL_CONSUMPTION, 0, 25,
                trap("oszczędny", 0, 0, 4, 5),
                trap("ekonomiczny", 4, 5, 6, 7),
                trap("umiarkowany", 6, 7, 8, 10),
                trap("paliwożerny", 8, 10, 12, 15),
                trap("rozrzutny", 12, 15, 25, 25));

        add("Pojemność zbiornika paliwa", Columns.FUEL_TANK, 10, 160,
                trap("mały", 10, 10, 40, 45),
                trap("niewielki", 40, 45, 50, 55),
                trap("standardowy", 50, 55, 65, 70),
                trap("pojemny", 65, 70, 80, 90),
                trap("ogromny", 80, 90, 160, 160));

        add("Przyspieszenie 0–100 km/h", Columns.ACCELERATION, 2, 25,
                trap("rakietowy", 2, 2, 5, 6),
                trap("szybki", 5, 6, 8, 9),
                trap("przeciętny", 8, 9, 11, 13),
                trap("ospały", 11, 13, 15, 17),
                trap("ślamazarny", 15, 17, 25, 25));

        add("Prędkość maksymalna", Columns.MAX_SPEED, 80, 360,
                trap("powolny", 80, 80, 150, 165),
                trap("miejski", 150, 165, 180, 195),
                trap("autostradowy", 180, 195, 210, 230),
                trap("wyścigowy", 210, 230, 250, 280),
                trap("błyskawiczny", 250, 280, 360, 360));
    }

    private void buildQuantifiers() {
        Universe rel = Universe.continuous(0.0, 1.0);
        relativeQuantifiers.add(quant("prawie żaden", Quantifier.Type.RELATIVE, rel, 0.00, 0.00, 0.05, 0.20));
        relativeQuantifiers.add(quant("mniejszość", Quantifier.Type.RELATIVE, rel, 0.05, 0.20, 0.30, 0.40));
        relativeQuantifiers.add(quant("około połowy", Quantifier.Type.RELATIVE, rel, 0.30, 0.40, 0.60, 0.70));
        relativeQuantifiers.add(quant("większość", Quantifier.Type.RELATIVE, rel, 0.60, 0.70, 0.85, 0.95));
        relativeQuantifiers.add(quant("prawie wszystkie", Quantifier.Type.RELATIVE, rel, 0.85, 0.95, 1.00, 1.00));

        Universe abs = Universe.discrete(0.0, 25000.0);
        absoluteQuantifiers.add(quant("bardzo mało", Quantifier.Type.ABSOLUTE, abs, 0, 0, 1000, 3000));
        absoluteQuantifiers.add(quant("kilka tysięcy", Quantifier.Type.ABSOLUTE, abs, 1000, 3000, 6000, 8000));
        absoluteQuantifiers.add(quant("kilkanaście tysięcy", Quantifier.Type.ABSOLUTE, abs, 6000, 8000, 14000, 16000));
        absoluteQuantifiers.add(quant("dużo", Quantifier.Type.ABSOLUTE, abs, 14000, 16000, 25000, 25000));
    }

    private void add(String name, String column, double min, double max, Label... labels) {
        Universe u = Universe.continuous(min, max);
        List<Label> withUniverse = new ArrayList<>();
        for (Label l : labels) {
            // przypisz właściwą przestrzeń rozważań do zbioru rozmytego etykiety
            FuzzySet set = new FuzzySet(l.set().name(), u, l.set().membershipFunction());
            withUniverse.add(new Label(l.name(), set));
        }
        variables.put(column, new LinguisticVariable(name, column, u, withUniverse));
    }

    private static Label trap(String name, double a, double b, double c, double d) {
        // przestrzeń tymczasowa; właściwa zostanie nadana w add()
        FuzzySet set = new FuzzySet(name, Universe.continuous(a, d), new TrapezoidalMf(a, b, c, d));
        return new Label(name, set);
    }

    private static Quantifier quant(String name, Quantifier.Type type, Universe u,
                                    double a, double b, double c, double d) {
        FuzzySet set = new FuzzySet(name, u, new TrapezoidalMf(a, b, c, d));
        return new Quantifier(name, type, set);
    }

    public LinguisticVariable variable(String column) {
        LinguisticVariable v = variables.get(column);
        if (v == null) {
            throw new IllegalArgumentException("Nieznana zmienna: " + column);
        }
        return v;
    }

    public List<LinguisticVariable> variables() {
        return List.copyOf(variables.values());
    }

    public List<Quantifier> relativeQuantifiers() {
        return List.copyOf(relativeQuantifiers);
    }

    public List<Quantifier> absoluteQuantifiers() {
        return List.copyOf(absoluteQuantifiers);
    }

    public void addLabel(String column, String labelName, MembershipFunction mf) {
        LinguisticVariable current = variable(column);
        if (current.label(labelName).isPresent()) {
            throw new IllegalArgumentException("Etykieta już istnieje: " + labelName);
        }
        List<Label> labels = new ArrayList<>(current.labels());
        labels.add(new Label(labelName, new FuzzySet(labelName, current.universe(), mf)));
        variables.put(column, new LinguisticVariable(
                current.name(), current.column(), current.universe(), labels));
    }

    public void addRelativeQuantifier(String name, MembershipFunction mf) {
        addQuantifier(relativeQuantifiers, name, Quantifier.Type.RELATIVE, Universe.continuous(0.0, 1.0), mf);
    }

    public void addAbsoluteQuantifier(String name, MembershipFunction mf) {
        addQuantifier(absoluteQuantifiers, name, Quantifier.Type.ABSOLUTE, Universe.discrete(0.0, 25000.0), mf);
    }

    private static void addQuantifier(List<Quantifier> target, String name, Quantifier.Type type,
                                      Universe universe, MembershipFunction mf) {
        if (target.stream().anyMatch(q -> q.name().equals(name))) {
            throw new IllegalArgumentException("Kwantyfikator już istnieje: " + name);
        }
        target.add(new Quantifier(name, type, new FuzzySet(name, universe, mf)));
    }
}
