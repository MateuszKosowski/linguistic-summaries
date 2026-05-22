package org.kosowskinowak.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Odczytuje rekordy z relacyjnej bazy SQLite przez JDBC (tabela {@code cars}). Jedyne źródło danych
 * dla części obliczeniowej — aplikacja nie czyta CSV bezpośrednio.
 */
public final class CarDataLoader {

    private final String jdbcUrl;

    public CarDataLoader(String dbPath) {
        this.jdbcUrl = "jdbc:sqlite:" + dbPath;
    }

    public List<CarRecord> load() {
        List<CarRecord> records = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM cars")) {

            while (rs.next()) {
                Map<String, Double> attrs = new LinkedHashMap<>();
                for (String col : Columns.NUMERIC) {
                    double v = rs.getDouble(col);
                    attrs.put(col, rs.wasNull() ? Double.NaN : v);
                }
                records.add(new CarRecord(
                        rs.getString(Columns.ID_TRIM),
                        rs.getString(Columns.MAKE),
                        rs.getString(Columns.MODEL),
                        attrs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Błąd odczytu z SQLite (" + jdbcUrl
                    + "). Czy baza została zaimportowana? " + e.getMessage(), e);
        }
        return records;
    }
}
