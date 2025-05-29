package lab;

import lab.population.PopulationTimingType;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static lab.encoding.Encoding.STANDARD;

@Component
@RequiredArgsConstructor
public class SnapshotParser {

    private static final String INDIVIDUAL_HEADER = "Individual";
    private static final String FITNESS_HEADER = "Fitness";

    public static void main(String[] args) {
        Path basePath = Paths.get("stats_none/plots/FH/100/NONE/ONE_OPTIMAL/STANDARD/RWS/success (1)/population_snapshots");
        int populationSize = 100;
        SnapshotParser snapshotParser = new SnapshotParser();

        Map<PopulationTimingType, Map<Individual, Double>> timingToFitnessMap = snapshotParser.getTimingToIndividualToFitness(basePath, populationSize);
        printFitnessMap(timingToFitnessMap);
    }

    /**
     * Walks through all .xlsx files in the directory and extracts individual-fitness mappings from each.
     */
    public Map<PopulationTimingType, Map<Individual, Double>> getTimingToIndividualToFitness(Path basePath, int populationSize) {
        Map<PopulationTimingType, Map<Individual, Double>> result = new HashMap<>();

        try (Stream<Path> files = Files.walk(basePath)) {
            files.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".xlsx"))
                    .forEach(file -> {
                        try {
                            Map<Individual, Double> individualToFitness = parseExcelFile(file.toFile(), populationSize);
                            String fileName = file.getFileName().toString();
                            String fileNameWithoutExtension = fileName.contains(".")
                                    ? fileName.substring(0, fileName.lastIndexOf('.'))
                                    : fileName;
                            result.put(PopulationTimingType.valueOf(fileNameWithoutExtension), individualToFitness);
                        } catch (IOException e) {
                            System.err.printf("Failed to read '%s': %s%n", file, e.getMessage());
                        }
                    });
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read file: " + basePath, e);
        }

        return result;
    }

    /**
     * Parses a single Excel file and returns a map of Individual to Fitness.
     */
    private static Map<Individual, Double> parseExcelFile(File file, int populationSize) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Header row missing in file: " + file.getName());
            }

            int individualCol = findColumnIndex(headerRow, INDIVIDUAL_HEADER);
            int fitnessCol = findColumnIndex(headerRow, FITNESS_HEADER);

            return readIndividualFitnessMap(sheet, individualCol, fitnessCol, populationSize, file.getName());
        }
    }

    /**
     * Reads up to N rows from the sheet and returns a map of Individual to Fitness.
     */
    private static Map<Individual, Double> readIndividualFitnessMap(Sheet sheet, int individualCol, int fitnessCol, int populationSize, String fileName) {
        Map<Individual, Double> individualToFitness = new LinkedHashMap<>();

        for (int i = 1; i <= populationSize; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Cell individualCell = row.getCell(individualCol);
            Cell fitnessCell = row.getCell(fitnessCol);
            if (individualCell == null || fitnessCell == null) continue;

            String individualStr = individualCell.toString().trim();
            Individual individual = new Individual(individualStr, STANDARD);

            double fitness = parseFitnessValue(fitnessCell, fileName, i);
            individualToFitness.put(individual, fitness);
        }

        return individualToFitness;
    }

    /**
     * Finds the index of a column based on the header name.
     */
    private static int findColumnIndex(Row headerRow, String columnName) {
        for (Cell cell : headerRow) {
            if (columnName.equalsIgnoreCase(cell.getStringCellValue().trim())) {
                return cell.getColumnIndex();
            }
        }
        throw new IllegalArgumentException("Column '" + columnName + "' not found in header.");
    }

    /**
     * Parses a fitness value from a cell, safely handling both numeric and string formats.
     */
    private static double parseFitnessValue(Cell cell, String fileName, int rowIndex) {
        try {
            return cell.getNumericCellValue();
        } catch (IllegalStateException e) {
            try {
                return Double.parseDouble(cell.toString().trim());
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid fitness value at row " + rowIndex + " in file " + fileName, ex);
            }
        }
    }

    /**
     * Nicely prints the full map of files → (individual → fitness).
     */
    private static void printFitnessMap(Map<PopulationTimingType, Map<Individual, Double>> fitnessMap) {
        fitnessMap.forEach((timingType, map) -> {
            System.out.println("File: " + timingType.name());
            map.forEach((individual, fitness) ->
                    System.out.println("  " + individual + " -> " + fitness + "\n")
            );
        });
    }

}
