package org.kosowskinowak.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ładuje przefiltrowany zbiór CSV do relacyjnej bazy SQLite (tabela {@code cars}) — realizacja
 * wymogu dostępu przez DBMS. Czyści wartości liczbowe (przecinek dziesiętny → kropka, usunięcie
 * cudzysłowów). Operacja idempotentna: tabela jest odtwarzana od nowa.
 */
public final class DbImporter {

    private static final Logger log = LoggerFactory.getLogger(DbImporter.class);

    /** Ten sam podział CSV co w {@code DataCleaner}: nie dzieli przecinków w cudzysłowach. */
    private static final Pattern CSV_SPLITTER = Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

    public static final String DEFAULT_CSV =
            "src/main/resources/filtered dataset/dataset_CRS_task_2.csv";
    public static final String DEFAULT_DB = "cars.db";

    public String jdbcUrl(String dbPath) {
        return "jdbc:sqlite:" + dbPath;
    }

    /** Importuje CSV do {@code dbPath}. @return liczba wstawionych rekordów. */
    public int importCsv(String csvPath, String dbPath) {
        Path csv = Path.of(csvPath);
        try (BufferedReader reader = Files.newBufferedReader(csv, StandardCharsets.UTF_8);
             Connection conn = DriverManager.getConnection(jdbcUrl(dbPath))) {

            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalStateException("Pusty plik CSV: " + csvPath);
            }
            String[] headers = CSV_SPLITTER.split(headerLine, -1);
            int[] idx = columnIndices(headers);

            conn.setAutoCommit(false);
            recreateTable(conn);
            int inserted = insertRows(conn, reader, idx);
            conn.commit();
            log.info("Zaimportowano {} rekordów do {} (tabela cars).", inserted, dbPath);
            return inserted;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (SQLException e) {
            throw new IllegalStateException("Błąd importu do SQLite: " + e.getMessage(), e);
        }
    }

    private int[] columnIndices(String[] headers) {
        int[] idx = new int[Columns.ALL.size()];
        for (int i = 0; i < Columns.ALL.size(); i++) {
            String name = Columns.ALL.get(i);
            int found = -1;
            for (int h = 0; h < headers.length; h++) {
                if (headers[h].trim().equals(name)) {
                    found = h;
                    break;
                }
            }
            if (found < 0) {
                throw new IllegalStateException("Brak kolumny w CSV: " + name);
            }
            idx[i] = found;
        }
        return idx;
    }

    private void recreateTable(Connection conn) throws SQLException {
        StringBuilder cols = new StringBuilder();
        cols.append('"').append(Columns.ID_TRIM).append("\" TEXT, ");
        cols.append('"').append(Columns.MAKE).append("\" TEXT, ");
        cols.append('"').append(Columns.MODEL).append("\" TEXT");
        for (String c : Columns.NUMERIC) {
            cols.append(", \"").append(c).append("\" REAL");
        }
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DROP TABLE IF EXISTS cars");
            st.executeUpdate("CREATE TABLE cars (" + cols + ")");
        }
    }

    private int insertRows(Connection conn, BufferedReader reader, int[] idx)
            throws IOException, SQLException {
        StringBuilder names = new StringBuilder();
        StringBuilder marks = new StringBuilder();
        for (int i = 0; i < Columns.ALL.size(); i++) {
            if (i > 0) {
                names.append(", ");
                marks.append(", ");
            }
            names.append('"').append(Columns.ALL.get(i)).append('"');
            marks.append('?');
        }
        String sql = "INSERT INTO cars (" + names + ") VALUES (" + marks + ")";

        int count = 0;
        int skippedShort = 0;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] f = CSV_SPLITTER.split(line, -1);
                if (f.length < headersNeeded(idx)) {
                    skippedShort++;
                    continue;
                }
                // 3 kolumny tekstowe
                ps.setString(1, clean(f[idx[0]]));
                ps.setString(2, clean(f[idx[1]]));
                ps.setString(3, clean(f[idx[2]]));
                // 12 kolumn liczbowych
                for (int i = 3; i < Columns.ALL.size(); i++) {
                    Double v = parseNumber(f[idx[i]]);
                    if (v == null) {
                        ps.setNull(i + 1, java.sql.Types.REAL);
                    } else {
                        ps.setDouble(i + 1, v);
                    }
                }
                ps.addBatch();
                if (++count % 2000 == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch();
        }
        if (skippedShort > 0) {
            log.warn("Pominięto {} wierszy ze zbyt małą liczbą kolumn.", skippedShort);
        }
        return count;
    }

    private int headersNeeded(int[] idx) {
        int max = 0;
        for (int i : idx) {
            max = Math.max(max, i + 1);
        }
        return max;
    }

    private static String clean(String raw) {
        String s = raw.trim();
        if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length() - 1).trim();
        }
        return s;
    }

    private static Double parseNumber(String raw) {
        String s = clean(raw).replace(',', '.');
        if (s.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
