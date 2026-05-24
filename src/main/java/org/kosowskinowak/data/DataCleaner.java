package org.kosowskinowak.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * Narzędzie przygotowania danych: z oryginalnego zbioru CSV (78 kolumn) wybiera 15 kolumn
 * roboczych i odrzuca rekordy z brakami, tworząc {@code dataset_CRS_task_2.csv}. Krok
 * jednorazowy, uruchamiany ręcznie ({@link #main}); aplikacja właściwa czyta dane przez DBMS
 * ({@link DbImporter} → {@link CarDataLoader}).
 */
public class DataCleaner {

    private static final Logger logger = LoggerFactory.getLogger(DataCleaner.class);

    private static final String MSG_LOADING_FILE = "Loading file: {}...";
    private static final String MSG_ERROR_EMPTY_FILE = "ERROR: File is empty!";
    private static final String MSG_ERROR_COLUMN_NOT_FOUND = "COLUMN ERROR: Column '{}' not found in file.";
    private static final String MSG_FINAL_ROW_COUNT = "Number of rows after removing missing data: {}";
    private static final String MSG_SUCCESS_RECORDS_COUNT = "Success! Dataset has over 10,000 records.";
    private static final String MSG_WARN_LOW_RECORDS_COUNT = "Warning: Dataset has fewer than 10,000 records.";
    private static final String MSG_DONE_SAVED_TO = "Done! Saved cleaned database to file: {}";
    private static final String MSG_ERROR_IO = "An error occurred during file operations: {}";

    private static final Pattern CSV_SPLITTER = Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

    public static void main(String[] args) {
        new DataCleaner().cleanData();
    }

    public void cleanData() {
        String inputFile = "src/main/resources/original dataset/Car Dataset 1945-2020.csv";
        String outputFile = "src/main/resources/filtered dataset/dataset_CRS_task_2.csv";

        List<String> columnsToKeep = Arrays.asList(
                "id_trim", "Make", "Modle", "length_mm", "wheelbase_mm",
                "curb_weight_kg", "minimum_trunk_capacity_l", "maximum_torque_n_m",
                "capacity_cm3", "engine_hp", "turning_circle_m",
                "mixed_fuel_consumption_per_100_km_l", "fuel_tank_capacity_l",
                "acceleration_0_100_km/h_s", "max_speed_km_per_h"
        );

        logger.info(MSG_LOADING_FILE, inputFile);

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String headerLine = reader.readLine();
            if (headerLine == null) {
                logger.error(MSG_ERROR_EMPTY_FILE);
                return;
            }

            String[] headers = CSV_SPLITTER.split(headerLine, -1);

            int[] targetIndices = columnsToKeep.stream()
                    .mapToInt(col -> findColumnIndex(headers, col))
                    .toArray();

            for (int i = 0; i < targetIndices.length; i++) {
                if (targetIndices[i] == -1) {
                    logger.error(MSG_ERROR_COLUMN_NOT_FOUND, columnsToKeep.get(i));
                    return;
                }
            }

            writer.write(String.join(",", columnsToKeep));
            writer.newLine();

            long finalCount = reader.lines()
                    .map(line -> processRow(line, targetIndices))
                    .filter(Objects::nonNull)
                    .peek(validLine -> writeLine(writer, validLine))
                    .count();

            logger.info(MSG_FINAL_ROW_COUNT, finalCount);
            if (finalCount >= 10000) {
                logger.info(MSG_SUCCESS_RECORDS_COUNT);
            } else {
                logger.warn(MSG_WARN_LOW_RECORDS_COUNT);
            }
            logger.info(MSG_DONE_SAVED_TO, outputFile);

        } catch (IOException e) {
            logger.error(MSG_ERROR_IO, e.getMessage());
        }
    }


    private String processRow(String line, int[] targetIndices) {
        String[] fields = CSV_SPLITTER.split(line, -1);
        String[] processedRow = new String[targetIndices.length];

        for (int i = 0; i < targetIndices.length; i++) {
            int index = targetIndices[i];

            if (index >= fields.length) return null;

            String value = fields[index].trim();

            if (value.isEmpty() || value.equalsIgnoreCase("NA") ||
                    value.equalsIgnoreCase("null") || value.equals("-")) {
                return null;
            }
            processedRow[i] = value;
        }
        return String.join(",", processedRow);
    }

    private int findColumnIndex(String[] headers, String targetCol) {
        return IntStream.range(0, headers.length)
                .filter(i -> headers[i].replaceAll("\"", "").trim().equals(targetCol))
                .findFirst()
                .orElse(-1);
    }

    private void writeLine(BufferedWriter writer, String line) {
        try {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
