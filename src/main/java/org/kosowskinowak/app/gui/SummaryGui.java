package org.kosowskinowak.app.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import org.kosowskinowak.config.FuzzyConfig;
import org.kosowskinowak.data.CarDataLoader;
import org.kosowskinowak.data.CarRecord;
import org.kosowskinowak.data.DbImporter;
import org.kosowskinowak.fuzzy.linguistic.Label;
import org.kosowskinowak.fuzzy.linguistic.LinguisticVariable;
import org.kosowskinowak.fuzzy.linguistic.Quantifier;
import org.kosowskinowak.fuzzy.mf.GaussianMf;
import org.kosowskinowak.fuzzy.mf.MembershipFunction;
import org.kosowskinowak.fuzzy.mf.TrapezoidalMf;
import org.kosowskinowak.fuzzy.mf.TriangularMf;
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
 * Prosty desktopowy interfejs Swing do generowania podsumowań i definiowania własnych etykiet
 * oraz kwantyfikatorów w trakcie działania aplikacji.
 */
public final class SummaryGui {

    private static final String[] SORT_SINGLE = {
            "OPT", "T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11"
    };
    private static final String[] FUNCTIONS = {"trójkątna", "trapezoidalna", "gaussowska"};

    private final FuzzyConfig config = FuzzyConfig.defaults();
    private final List<CarRecord> records;
    private final JFrame frame = new JFrame("Podsumowania lingwistyczne relacyjnej bazy samochodów");

    private final DefaultTableModel singleModel = nonEditableModel(
            "Zdanie", "T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "OPT");
    private final JTable singleTable = new JTable(singleModel);
    private final DefaultTableModel multiModel = nonEditableModel("Forma", "Zdanie", "T");
    private final JTable multiTable = new JTable(multiModel);

    private final JComboBox<String> singleForm = new JComboBox<>(new String[]{"Forma I", "Forma II"});
    private final JComboBox<String> quantifierMode = new JComboBox<>(new String[]{"względne", "bezwzględne"});
    private final JComboBox<NamedValue<LinguisticVariable>> qualifierVariable = new JComboBox<>();
    private final JComboBox<NamedValue<Label>> qualifierLabel = new JComboBox<>();
    private final JComboBox<NamedValue<LinguisticVariable>> summarizerVariable = new JComboBox<>();
    private final JComboBox<NamedValue<Label>> summarizerLabel = new JComboBox<>();
    private final JCheckBox allSummarizers = new JCheckBox("wszystkie sumaryzatory", true);
    private final JComboBox<String> singleSort = new JComboBox<>(SORT_SINGLE);
    private final JSpinner singleTop = new JSpinner(new SpinnerNumberModel(20, 1, 500, 1));
    private final JSpinner[] weights = new JSpinner[11];

    private final JComboBox<String> subjectA = new JComboBox<>();
    private final JComboBox<String> subjectB = new JComboBox<>();
    private final JComboBox<String> multiForm = new JComboBox<>(new String[]{"I", "II", "III", "IV"});
    private final JComboBox<NamedValue<LinguisticVariable>> multiQualifierVariable = new JComboBox<>();
    private final JComboBox<NamedValue<Label>> multiQualifierLabel = new JComboBox<>();
    private final JComboBox<String> multiSort = new JComboBox<>(new String[]{"T malejąco", "T rosnąco"});
    private final JSpinner multiTop = new JSpinner(new SpinnerNumberModel(20, 1, 500, 1));

    private final JComboBox<NamedValue<LinguisticVariable>> advancedVariable = new JComboBox<>();
    private final JTextField advancedLabelName = new JTextField(16);
    private final JComboBox<String> advancedLabelFunction = new JComboBox<>(FUNCTIONS);
    private final JTextField advancedLabelParams = new JTextField("0; 1; 2; 3", 18);
    private final JTextField advancedQuantifierName = new JTextField(16);
    private final JComboBox<String> advancedQuantifierType = new JComboBox<>(new String[]{"względny", "bezwzględny"});
    private final JComboBox<String> advancedQuantifierFunction = new JComboBox<>(FUNCTIONS);
    private final JTextField advancedQuantifierParams = new JTextField("0.3; 0.4; 0.6; 0.7", 18);

    private SummaryGui() {
        this.records = loadRecords();
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Systemowy wygląd jest miły, ale nie jest wymagany do działania aplikacji.
            }
            new SummaryGui().show();
        });
    }

    static MembershipFunction createMembershipFunction(String functionName, String rawParams) {
        double[] p = parseParameters(rawParams);
        return switch (functionName) {
            case "trójkątna" -> {
                requireCount(functionName, p, 3);
                yield new TriangularMf(p[0], p[1], p[2]);
            }
            case "trapezoidalna" -> {
                requireCount(functionName, p, 4);
                yield new TrapezoidalMf(p[0], p[1], p[2], p[3]);
            }
            case "gaussowska" -> {
                requireCount(functionName, p, 2);
                yield new GaussianMf(p[0], p[1]);
            }
            default -> throw new IllegalArgumentException("Nieznany typ funkcji: " + functionName);
        };
    }

    private static double[] parseParameters(String rawParams) {
        String[] parts = rawParams.trim().split("[;\\s]+");
        List<Double> values = new ArrayList<>();
        for (String part : parts) {
            if (!part.isBlank()) {
                values.add(Double.parseDouble(part.replace(',', '.')));
            }
        }
        double[] out = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            out[i] = values.get(i);
        }
        return out;
    }

    private static void requireCount(String functionName, double[] params, int expected) {
        if (params.length != expected) {
            throw new IllegalArgumentException(functionName + " wymaga " + expected
                    + " parametrów, podano " + params.length);
        }
    }

    private void show() {
        refreshVariableCombos();
        refreshBrandCombos();
        configureTables();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));
        frame.add(header(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Jednopodmiotowe", singleSubjectTab());
        tabs.addTab("Wielopodmiotowe", multiSubjectTab());
        tabs.addTab("Tryb zaawansowany", advancedTab());
        frame.add(tabs, BorderLayout.CENTER);

        frame.setSize(1200, 760);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel header() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 12, 0, 12));
        panel.add(new JLabel("SQLite: " + DbImporter.DEFAULT_DB + " | rekordy: " + records.size()),
                BorderLayout.WEST);
        return panel;
    }

    private JPanel singleSubjectTab() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(10, 12, 12, 12));
        root.add(singleControls(), BorderLayout.NORTH);
        root.add(new JScrollPane(singleTable), BorderLayout.CENTER);
        root.add(exportPanel(singleTable, "single-subject.csv"), BorderLayout.SOUTH);
        return root;
    }

    private JPanel singleControls() {
        JPanel panel = formPanel();
        singleForm.addActionListener(e -> updateSingleControls());
        qualifierVariable.addActionListener(e -> refreshLabelCombo(qualifierVariable, qualifierLabel));
        summarizerVariable.addActionListener(e -> refreshLabelCombo(summarizerVariable, summarizerLabel));
        allSummarizers.addActionListener(e -> updateSingleControls());
        JButton generate = new JButton("Generuj");
        generate.addActionListener(e -> generateSingleSubject());

        addRow(panel, 0, new JLabel("Forma"), singleForm, new JLabel("Kwantyfikatory"), quantifierMode,
                new JLabel("Sortuj"), singleSort, new JLabel("TOP"), singleTop, generate);
        addRow(panel, 1, new JLabel("Kwalifikator"), qualifierVariable, qualifierLabel,
                new JLabel("Sumaryzator"), summarizerVariable, summarizerLabel, allSummarizers);
        addWeights(panel, 2);
        updateSingleControls();
        return panel;
    }

    private JPanel multiSubjectTab() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(10, 12, 12, 12));
        root.add(multiControls(), BorderLayout.NORTH);
        root.add(new JScrollPane(multiTable), BorderLayout.CENTER);
        root.add(exportPanel(multiTable, "multi-subject.csv"), BorderLayout.SOUTH);
        return root;
    }

    private JPanel multiControls() {
        JPanel panel = formPanel();
        multiQualifierVariable.addActionListener(e -> refreshLabelCombo(multiQualifierVariable, multiQualifierLabel));
        multiForm.addActionListener(e -> updateMultiControls());
        JButton generate = new JButton("Generuj");
        generate.addActionListener(e -> generateMultiSubject());

        addRow(panel, 0, new JLabel("P1"), subjectA, new JLabel("P2"), subjectB,
                new JLabel("Forma"), multiForm, new JLabel("Sortuj"), multiSort,
                new JLabel("TOP"), multiTop, generate);
        addRow(panel, 1, new JLabel("Kwalifikator S2"), multiQualifierVariable, multiQualifierLabel);
        updateMultiControls();
        return panel;
    }

    private JPanel advancedTab() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(10, 12, 12, 12));

        JPanel forms = new JPanel(new GridBagLayout());
        forms.setBorder(BorderFactory.createTitledBorder("Definicje użytkownika zaawansowanego"));
        JButton addLabel = new JButton("Dodaj etykietę");
        addLabel.addActionListener(e -> addAdvancedLabel());
        JButton addQuantifier = new JButton("Dodaj kwantyfikator");
        addQuantifier.addActionListener(e -> addAdvancedQuantifier());

        addRow(forms, 0, new JLabel("Zmienna"), advancedVariable, new JLabel("Nazwa etykiety"),
                advancedLabelName, new JLabel("Funkcja"), advancedLabelFunction,
                new JLabel("Parametry"), advancedLabelParams, addLabel);
        addRow(forms, 1, new JLabel("Typ kwantyfikatora"), advancedQuantifierType,
                new JLabel("Nazwa"), advancedQuantifierName, new JLabel("Funkcja"),
                advancedQuantifierFunction, new JLabel("Parametry"), advancedQuantifierParams,
                addQuantifier);

        root.add(forms, BorderLayout.NORTH);
        root.add(new JLabel("Parametry: trójkątna a;b;c, trapezoidalna a;b;c;d, gaussowska środek;sigma."),
                BorderLayout.CENTER);
        return root;
    }

    private void generateSingleSubject() {
        try {
            QualityWeights qWeights = currentWeights();
            List<ScoredSingle> rows = new ArrayList<>();
            SummaryGenerator generator = new SummaryGenerator(config);
            boolean form2 = singleForm.getSelectedIndex() == 1;
            boolean absolute = quantifierMode.getSelectedIndex() == 1;

            if (allSummarizers.isSelected()) {
                List<SingleSubjectSummary> summaries = form2
                        ? generator.simpleForm2(selected(qualifierVariable).column(), selected(qualifierLabel).name())
                        : generator.simpleForm1(absolute);
                for (SingleSubjectSummary summary : summaries) {
                    rows.add(score(summary, qWeights));
                }
            } else {
                LabelExpression summarizer = selectedProperty(summarizerVariable, summarizerLabel);
                if (form2) {
                    LabelExpression qualifier = selectedProperty(qualifierVariable, qualifierLabel);
                    for (Quantifier q : config.relativeQuantifiers()) {
                        rows.add(score(new SingleSubjectSummary(q, qualifier, summarizer), qWeights));
                    }
                } else {
                    List<Quantifier> quantifiers = absolute
                            ? config.absoluteQuantifiers() : config.relativeQuantifiers();
                    for (Quantifier q : quantifiers) {
                        rows.add(score(new SingleSubjectSummary(q, summarizer), qWeights));
                    }
                }
            }

            rows.sort(Comparator.comparingDouble(this::singleSortValue).reversed());
            fillSingleTable(rows.stream().limit((Integer) singleTop.getValue()).toList());
        } catch (RuntimeException e) {
            showError(e);
        }
    }

    private ScoredSingle score(SingleSubjectSummary summary, QualityWeights qWeights) {
        return new ScoredSingle(summary, QualityMeasures.evaluate(summary, records, qWeights));
    }

    private double singleSortValue(ScoredSingle row) {
        int idx = singleSort.getSelectedIndex();
        return idx == 0 ? row.quality().optimal() : row.quality().toArray()[idx - 1];
    }

    private void fillSingleTable(List<ScoredSingle> rows) {
        singleModel.setRowCount(0);
        for (ScoredSingle row : rows) {
            Quality q = row.quality();
            Object[] values = new Object[14];
            values[0] = row.summary().sentence();
            double[] measures = q.toArray();
            for (int i = 0; i < measures.length; i++) {
                values[i + 1] = fmt(measures[i]);
            }
            values[13] = fmt(q.optimal());
            singleModel.addRow(values);
        }
    }

    private void generateMultiSubject() {
        try {
            Subject p1 = Subject.ofMake((String) subjectA.getSelectedItem());
            Subject p2 = Subject.ofMake((String) subjectB.getSelectedItem());
            MultiSubjectGenerator generator = new MultiSubjectGenerator(config);
            List<MultiSubjectSummary> summaries = switch ((String) multiForm.getSelectedItem()) {
                case "I" -> generator.formI(p1, p2);
                case "II" -> generator.formII(p1, p2, selectedProperty(multiQualifierVariable, multiQualifierLabel));
                case "III" -> generator.formIII(p1, p2, selectedProperty(multiQualifierVariable, multiQualifierLabel));
                case "IV" -> generator.formIV(p1, p2);
                default -> throw new IllegalStateException("Nieznana forma");
            };
            Comparator<ScoredMulti> cmp = Comparator.comparingDouble(ScoredMulti::truth);
            if (multiSort.getSelectedIndex() == 0) {
                cmp = cmp.reversed();
            }
            List<ScoredMulti> rows = summaries.stream()
                    .map(s -> new ScoredMulti(s, MultiSubjectMeasures.degreeOfTruth(s, records)))
                    .sorted(cmp)
                    .limit((Integer) multiTop.getValue())
                    .toList();
            fillMultiTable(rows);
        } catch (RuntimeException e) {
            showError(e);
        }
    }

    private void fillMultiTable(List<ScoredMulti> rows) {
        multiModel.setRowCount(0);
        for (ScoredMulti row : rows) {
            multiModel.addRow(new Object[]{
                    row.summary().form().name(), row.summary().sentence(), fmt(row.truth())
            });
        }
    }

    private void addAdvancedLabel() {
        try {
            String name = advancedLabelName.getText().trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Podaj nazwę etykiety");
            }
            config.addLabel(selected(advancedVariable).column(), name,
                    createMembershipFunction((String) advancedLabelFunction.getSelectedItem(),
                            advancedLabelParams.getText()));
            refreshVariableCombos();
            JOptionPane.showMessageDialog(frame, "Dodano etykietę: " + name);
        } catch (RuntimeException e) {
            showError(e);
        }
    }

    private void addAdvancedQuantifier() {
        try {
            String name = advancedQuantifierName.getText().trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Podaj nazwę kwantyfikatora");
            }
            MembershipFunction mf = createMembershipFunction(
                    (String) advancedQuantifierFunction.getSelectedItem(), advancedQuantifierParams.getText());
            if (advancedQuantifierType.getSelectedIndex() == 0) {
                config.addRelativeQuantifier(name, mf);
            } else {
                config.addAbsoluteQuantifier(name, mf);
            }
            JOptionPane.showMessageDialog(frame, "Dodano kwantyfikator: " + name);
        } catch (RuntimeException e) {
            showError(e);
        }
    }

    private void updateSingleControls() {
        boolean form2 = singleForm.getSelectedIndex() == 1;
        qualifierVariable.setEnabled(form2);
        qualifierLabel.setEnabled(form2);
        quantifierMode.setEnabled(!form2);
        summarizerVariable.setEnabled(!allSummarizers.isSelected());
        summarizerLabel.setEnabled(!allSummarizers.isSelected());
    }

    private void updateMultiControls() {
        String form = (String) multiForm.getSelectedItem();
        boolean qualified = "II".equals(form) || "III".equals(form);
        multiQualifierVariable.setEnabled(qualified);
        multiQualifierLabel.setEnabled(qualified);
    }

    private QualityWeights currentWeights() {
        double[] raw = new double[11];
        for (int i = 0; i < raw.length; i++) {
            raw[i] = ((Number) weights[i].getValue()).doubleValue();
        }
        return QualityWeights.of(raw);
    }

    private LabelExpression selectedProperty(JComboBox<NamedValue<LinguisticVariable>> varCombo,
                                             JComboBox<NamedValue<Label>> labelCombo) {
        return new Property(selected(varCombo), selected(labelCombo));
    }

    private void refreshVariableCombos() {
        List<JComboBox<NamedValue<LinguisticVariable>>> combos = List.of(
                qualifierVariable, summarizerVariable, multiQualifierVariable, advancedVariable);
        for (JComboBox<NamedValue<LinguisticVariable>> combo : combos) {
            Object current = combo.getSelectedItem();
            combo.removeAllItems();
            for (LinguisticVariable variable : config.variables()) {
                combo.addItem(new NamedValue<>(variable.name(), variable));
            }
            restoreSelection(combo, current);
        }
        refreshLabelCombo(qualifierVariable, qualifierLabel);
        refreshLabelCombo(summarizerVariable, summarizerLabel);
        refreshLabelCombo(multiQualifierVariable, multiQualifierLabel);
    }

    private void refreshLabelCombo(JComboBox<NamedValue<LinguisticVariable>> varCombo,
                                   JComboBox<NamedValue<Label>> labelCombo) {
        Object current = labelCombo.getSelectedItem();
        labelCombo.removeAllItems();
        NamedValue<LinguisticVariable> variable = selectedItem(varCombo);
        if (variable != null) {
            for (Label label : variable.value().labels()) {
                labelCombo.addItem(new NamedValue<>(label.name(), label));
            }
        }
        restoreSelection(labelCombo, current);
    }

    private void refreshBrandCombos() {
        Set<String> brands = new LinkedHashSet<>();
        records.stream().map(CarRecord::make).sorted().forEach(brands::add);
        subjectA.removeAllItems();
        subjectB.removeAllItems();
        for (String brand : brands) {
            subjectA.addItem(brand);
            subjectB.addItem(brand);
        }
        subjectA.setSelectedItem("BMW");
        subjectB.setSelectedItem("Toyota");
    }

    private void configureTables() {
        singleTable.setAutoCreateRowSorter(true);
        multiTable.setAutoCreateRowSorter(true);
        singleTable.setFillsViewportHeight(true);
        multiTable.setFillsViewportHeight(true);
    }

    private JPanel exportPanel(JTable table, String defaultName) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton selected = new JButton("Eksportuj zaznaczone");
        selected.addActionListener(e -> exportRows(table, defaultName, true));
        JButton all = new JButton("Eksportuj wszystkie");
        all.addActionListener(e -> exportRows(table, defaultName, false));
        panel.add(selected);
        panel.add(all);
        return panel;
    }

    private void exportRows(JTable table, String defaultName, boolean selectedOnly) {
        try {
            int[] viewRows = selectedOnly ? table.getSelectedRows() : allRows(table);
            if (viewRows.length == 0) {
                throw new IllegalArgumentException("Nie wybrano żadnych wierszy");
            }
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(defaultName));
            if (chooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            Files.writeString(chooser.getSelectedFile().toPath(), tableToCsv(table, viewRows),
                    StandardCharsets.UTF_8);
        } catch (IOException | RuntimeException e) {
            showError(e);
        }
    }

    private static int[] allRows(JTable table) {
        int[] rows = new int[table.getRowCount()];
        for (int i = 0; i < rows.length; i++) {
            rows[i] = i;
        }
        return rows;
    }

    private static String tableToCsv(JTable table, int[] viewRows) {
        StringBuilder out = new StringBuilder();
        for (int c = 0; c < table.getColumnCount(); c++) {
            if (c > 0) {
                out.append(';');
            }
            out.append(csv(table.getColumnName(c)));
        }
        out.append(System.lineSeparator());
        for (int viewRow : viewRows) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            for (int c = 0; c < table.getColumnCount(); c++) {
                if (c > 0) {
                    out.append(';');
                }
                out.append(csv(String.valueOf(table.getModel().getValueAt(modelRow, c))));
            }
            out.append(System.lineSeparator());
        }
        return out.toString();
    }

    private static String csv(String value) {
        return '"' + value.replace("\"", "\"\"") + '"';
    }

    private static JPanel formPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Parametry"),
                BorderFactory.createEmptyBorder(4, 6, 6, 6)));
        return panel;
    }

    private void addWeights(JPanel panel, int row) {
        for (int i = 0; i < weights.length; i++) {
            weights[i] = new JSpinner(new SpinnerNumberModel(1.0, 0.0, 10.0, 0.1));
            addAt(panel, new JLabel(Quality.NAMES[i]), i * 2, row);
            addAt(panel, weights[i], i * 2 + 1, row);
        }
    }

    private static void addRow(JPanel panel, int row, java.awt.Component... components) {
        for (int i = 0; i < components.length; i++) {
            addAt(panel, components[i], i, row);
        }
    }

    private static void addAt(JPanel panel, java.awt.Component component, int col, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.insets = new Insets(3, 4, 3, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = component instanceof JTextField || component instanceof JComboBox<?>
                ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
        gbc.weightx = component instanceof JTextField || component instanceof JComboBox<?> ? 1.0 : 0.0;
        panel.add(component, gbc);
    }

    private static DefaultTableModel nonEditableModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private static List<CarRecord> loadRecords() {
        File db = new File(DbImporter.DEFAULT_DB);
        if (!db.exists()) {
            new DbImporter().importCsv(DbImporter.DEFAULT_CSV, DbImporter.DEFAULT_DB);
        }
        return new CarDataLoader(DbImporter.DEFAULT_DB).load();
    }

    private static String fmt(double value) {
        return String.format(Locale.US, "%.3f", value);
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(frame, e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
    }

    private static <T> T selected(JComboBox<NamedValue<T>> combo) {
        return selectedItem(combo).value();
    }

    @SuppressWarnings("unchecked")
    private static <T> NamedValue<T> selectedItem(JComboBox<NamedValue<T>> combo) {
        return (NamedValue<T>) combo.getSelectedItem();
    }

    private static <T> void restoreSelection(JComboBox<NamedValue<T>> combo, Object previous) {
        if (previous instanceof NamedValue<?> named) {
            for (int i = 0; i < combo.getItemCount(); i++) {
                if (combo.getItemAt(i).label().equals(named.label())) {
                    combo.setSelectedIndex(i);
                    return;
                }
            }
        }
        if (combo.getItemCount() > 0) {
            combo.setSelectedIndex(0);
        }
    }

    private record NamedValue<T>(String label, T value) {
        @Override
        public String toString() {
            return label;
        }
    }

    private record ScoredSingle(SingleSubjectSummary summary, Quality quality) {
    }

    private record ScoredMulti(MultiSubjectSummary summary, double truth) {
    }
}
