package lab.v2.export;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import lab.v2.function.FitnessFunctionV2;
import lab.v2.run.RunConfiguration;
import lab.v2.run.RunPoolStats;
import lab.v2.run.RunStats;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.IntStream;

public class Exporter {

    private static final int COLUMN_NUMBER = 81;
    private static final String STATS_PATH = Paths.get(".")
            .toAbsolutePath()
            .normalize()
            .toString()
            .replace("\\", "/")
            + "/stats_v2/";

    private static final String TABLES_PATH = STATS_PATH + "tables/";
    private static final String PLOTS_PATH = STATS_PATH + "plots/";
    private static final String ALL_STATS_TABLE_NAME = "all_stats.xlsx";

    private String getFileName(RunConfiguration runConfiguration) {
        String functionName = runConfiguration.function().getName();
        String selectorName = runConfiguration.selector().getName();
        String operatorName = runConfiguration.operator().getName();
        String populationType = runConfiguration.populationType().name();
        String encoding = runConfiguration.encoding().name();

        return functionName + "_" +
                selectorName + "_" +
                operatorName + "_" +
                populationType + "_" +
                encoding;
    }

    public void exportSingleRunPoolStats(RunPoolStats runPoolStats) {
        RunConfiguration runConfiguration = runPoolStats.runConfiguration();
        FitnessFunctionV2<?, ? extends Number> function = runConfiguration.function();
        List<RunStats> allRunStats = runPoolStats.allRunStats();

        String filename = getFileName(runConfiguration);

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Stats");

        String tablePath = TABLES_PATH + runConfiguration.populationSize() + "/" + filename + ".xlsx";

        createRunHeaderRow(sheet);
        IntStream.range(0, allRunStats.size())
                .forEach(i -> createRunRow(sheet, i + 1, function, allRunStats.get(i)));

        int runPoolStatsRowIndex = allRunStats.size() + 1 + 2;
        createRunPoolHeaderRow(sheet, runPoolStatsRowIndex);
        createRunPoolRow(sheet, runPoolStatsRowIndex + 1, 1, runPoolStats);

        IntStream.range(0, COLUMN_NUMBER)
                .forEach(sheet::autoSizeColumn);

        saveWorkbook(workbook, tablePath);
    }

    public void exportAllRunPools(List<RunPoolStats> allRunPoolStats) {
        RunConfiguration runConfiguration = allRunPoolStats.get(0).runConfiguration();

        String allStatsTablePath = TABLES_PATH + runConfiguration.populationSize() + "/" + ALL_STATS_TABLE_NAME;
        Workbook allStatsWorkbook = getAllConfigsWorkbook(allStatsTablePath);
        Sheet allStatsSheet = allStatsWorkbook.getSheetAt(0);
        int runPoolRowIndex = getFirstNullRowIndex(allStatsSheet);

        IntStream.range(0, allRunPoolStats.size())
                .forEach(i -> createRunPoolRow(allStatsSheet, runPoolRowIndex + i, runPoolRowIndex + i, allRunPoolStats.get(i)));

        IntStream.range(0, COLUMN_NUMBER)
                .forEach(allStatsSheet::autoSizeColumn);

        saveWorkbook(allStatsWorkbook, allStatsTablePath);
    }


    public void exportPlots(List<RunStats> allRunStats, RunConfiguration runConfiguration) {
        String plotFilepath = getPlotFilepath(runConfiguration);
        String plotExportDir = PLOTS_PATH + plotFilepath;

        File theDir1 = new File(plotExportDir);
        if (!theDir1.exists()) {
            theDir1.mkdirs();
        }

        for (int i = 0; i < 5 && i < allRunStats.size(); i++) {
            String plotExportPath = plotExportDir + (i + 1) + "/";
            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Plots data");
            String tablePath = plotExportPath + "plots_data" + ".xlsx";

            List<Integer> xIterations = IntStream.rangeClosed(1, allRunStats.get(i).ni())
                    .boxed().toList();
            List<Integer> xGenerations = IntStream.rangeClosed(1, allRunStats.get(i).ni() + 1)
                    .boxed().toList();

            List<Double> rrs = allRunStats.get(i).rrs();
            List<Double> tetas = allRunStats.get(i).tetas();
            List<Integer> uniques = allRunStats.get(i).uniques();

            drawPlot(xIterations, rrs, tetas, plotExportPath, "rr and teta", "rr", "teta", -0.1, 1.1);
            drawPlot(xGenerations, uniques, plotExportPath, "uniques");

            if (!runConfiguration.function().isConstant()) {
                List<Double> avgFs = allRunStats.get(i).avgFs();
                List<Double> maxFs = allRunStats.get(i).maxFs();
                List<Double> sigmaFs = allRunStats.get(i).sigmaFs();
                List<Double> optimalRatios = allRunStats.get(i).optimalRatios();
                List<Double> bestRatios = allRunStats.get(i).bestRatios();
                List<Double> ss = allRunStats.get(i).ss();
                List<Double> is = allRunStats.get(i).is();
                List<Double> prs = allRunStats.get(i).prs();
                List<Double> grs = allRunStats.get(i).grs();
                List<Double> fishes = allRunStats.get(i).fishes();

                drawPlot(xGenerations, avgFs, plotExportPath, "avgFs");
                drawPlot(xGenerations, maxFs, plotExportPath, "maxFs");
                drawPlot(xGenerations, sigmaFs, plotExportPath, "sigmaFs");
                drawPlot(xGenerations, optimalRatios, plotExportPath, "optimalRatios", -0.1, 1.1);
                drawPlot(xGenerations, bestRatios, plotExportPath, "bestRatios", -0.1, 1.1);
                drawPlot(xIterations, ss, plotExportPath, "difference");
                drawPlot(xIterations, is, plotExportPath, "intensity");
                drawPlot(xGenerations, prs, plotExportPath, "Pr");
                drawPlot(xIterations, grs, plotExportPath, "Gr");
                drawPlot(xIterations, fishes, plotExportPath, "Fisher Exact Test");
                drawPlot(xGenerations, prs, sigmaFs, plotExportPath, "Pr and sigmaF", "Pr", "SigmaF");


                createPlotsGenerationDataHeader(sheet);
                createGenerationRows(sheet, 1, xGenerations, avgFs, maxFs, sigmaFs, optimalRatios, bestRatios, uniques, prs);

                int freeIndex = getFirstNullRowIndex(sheet);
                createPlotsIterationDataHeader(sheet, freeIndex + 1);
                createIterationRows(sheet, freeIndex + 2, xIterations, rrs, tetas, ss, is, grs, fishes);
            } else {
                createPlotsGenerationDataHeaderConst(sheet);
                createGenerationRows(sheet, 1, xGenerations, uniques);

                int freeIndex = getFirstNullRowIndex(sheet);
                createPlotsIterationDataHeader(sheet, freeIndex + 1);
                createIterationRows(sheet, freeIndex + 2, xIterations, rrs, tetas);
            }

            saveWorkbook(workbook, tablePath);
        }
    }

    private void createGenerationRows(Sheet sheet, int index,
                                      List<Integer> xGenerations,
                                      List<Double> avgFs,
                                      List<Double> maxFs,
                                      List<Double> sigmaFs,
                                      List<Double> optimalRatios,
                                      List<Double> bestRatios,
                                      List<Integer> uniques,
                                      List<Double> prs
    ) {
        int rowIndex = index;
        for (int j = 0; j < xGenerations.size(); j++) {
            Row row = sheet.createRow(rowIndex++);
            int cellIndex = 0;

            row.createCell(cellIndex++).setCellValue(xGenerations.get(j));
            row.createCell(cellIndex++).setCellValue(avgFs.get(j));
            row.createCell(cellIndex++).setCellValue(maxFs.get(j));
            row.createCell(cellIndex++).setCellValue(optimalRatios.get(j));
            row.createCell(cellIndex++).setCellValue(bestRatios.get(j));
            row.createCell(cellIndex++).setCellValue(sigmaFs.get(j));
            row.createCell(cellIndex++).setCellValue(uniques.get(j));
            row.createCell(cellIndex++).setCellValue(prs.get(j));
        }
    }

    private void createGenerationRows(Sheet sheet, int index,
                                      List<Integer> xGenerations,
                                      List<Integer> uniques
    ) {
        int rowIndex = index;
        for (int j = 0; j < xGenerations.size(); j++) {
            Row row = sheet.createRow(rowIndex++);
            int cellIndex = 0;

            row.createCell(cellIndex++).setCellValue(xGenerations.get(j));
            row.createCell(cellIndex++).setCellValue(uniques.get(j));
        }
    }

    private void createIterationRows(Sheet sheet, int index,
                                     List<Integer> xIterations,
                                     List<Double> rrs,
                                     List<Double> tetas,
                                     List<Double> ss,
                                     List<Double> is,
                                     List<Double> grs,
                                     List<Double> fishes
    ) {
        int rowIndex = index;
        for (int j = 0; j < xIterations.size(); j++) {
            Row row = sheet.createRow(rowIndex++);
            int cellIndex = 0;

            row.createCell(cellIndex++).setCellValue(xIterations.get(j));
            row.createCell(cellIndex++).setCellValue(rrs.get(j));
            row.createCell(cellIndex++).setCellValue(tetas.get(j));
            row.createCell(cellIndex++).setCellValue(ss.get(j));
            row.createCell(cellIndex++).setCellValue(is.get(j));
            row.createCell(cellIndex++).setCellValue(grs.get(j));
            row.createCell(cellIndex++).setCellValue(fishes.get(j));
        }
    }

    private void createIterationRows(Sheet sheet, int index,
                                     List<Integer> xIterations,
                                     List<Double> rrs,
                                     List<Double> tetas) {
        int rowIndex = index;
        for (int j = 0; j < xIterations.size(); j++) {
            Row row = sheet.createRow(rowIndex++);
            int cellIndex = 0;

            row.createCell(cellIndex++).setCellValue(xIterations.get(j));
            row.createCell(cellIndex++).setCellValue(rrs.get(j));
            row.createCell(cellIndex++).setCellValue(tetas.get(j));
        }
    }

    private String getPlotFilepath(RunConfiguration runConfiguration) {
        String functionName = runConfiguration.function().getName();
        String selectorName = runConfiguration.selector().getName();
        String operatorName = runConfiguration.operator().getName();
        String populationType = runConfiguration.populationType().name();
        String encoding = runConfiguration.encoding().name();
        int populationSize = runConfiguration.populationSize();

        return functionName + "/" +
                populationSize + "/" +
                selectorName + "/" +
                operatorName + "/" +
                populationType + "/" +
                encoding + "/";
    }

    private void drawPlot(List<? extends Number> x, List<? extends Number> y, String out, String filename) {
        try {
            Plot plot = getPlot(x, y, out, filename);
            plot.savefig(out + filename + ".png").dpi(300);
            plot.executeSilently();
        } catch (IOException | PythonExecutionException e) {
            e.printStackTrace();
        }
    }

    private void drawPlot(List<? extends Number> x, List<? extends Number> y,
                          String out, String filename,
                          double minY, double maxY) {
        try {
            Plot plot = getPlot(x, y, out, filename);
            plot.ylim(minY, maxY);
            plot.savefig(out + filename + ".png").dpi(300);
            plot.executeSilently();
        } catch (IOException | PythonExecutionException e) {
            e.printStackTrace();
        }
    }

    private Plot getPlot(List<? extends Number> x, List<? extends Number> y, String out, String filename) {
        File theDir1 = new File(out);
        if (!theDir1.exists()) {
            theDir1.mkdirs();
        }

        Plot plt = Plot.create();
        plt.plot().add(x, y);
        plt.title(filename);
        return plt;
    }

    private void drawPlot(List<Integer> x, List<Double> y, List<Double> y1,
                          String out, String filename,
                          String firstLabel, String secondLabel,
                          double minY, double maxY) {
        try {
            File theDir1 = new File(out);
            if (!theDir1.exists()) {
                theDir1.mkdirs();
            }

            Plot plt = Plot.create();
            plt.plot().add(x, y).color("orange").label(firstLabel);
            plt.plot().add(x, y1).color("blue").label(secondLabel);
            plt.legend().loc("center right");
            plt.ylim(minY, maxY);
            plt.title(filename);
            plt.savefig(out + filename + ".png").dpi(300);
            plt.executeSilently();
        } catch (IOException | PythonExecutionException e) {
            e.printStackTrace();
        }
    }

    private void drawPlot(List<Integer> x, List<Double> y, List<Double> y1,
                          String out, String filename,
                          String firstLabel, String secondLabel) {
        try {
            File theDir1 = new File(out);
            if (!theDir1.exists()) {
                theDir1.mkdirs();
            }

            Plot plt = Plot.create();
            plt.plot().add(x, y).color("orange").label(firstLabel);
            plt.plot().add(x, y1).color("blue").label(secondLabel);
            plt.legend().loc("center right");
            plt.title(filename);
            plt.savefig(out + filename + ".png").dpi(300);
            plt.executeSilently();
        } catch (IOException | PythonExecutionException e) {
            e.printStackTrace();
        }
    }

    private int getFirstNullRowIndex(Sheet sheet) {
        int index = 0;
        for (Row row : sheet) {
            if (row == null) {
                return index;
            }
            index++;
        }
        return index;
    }

    private void saveWorkbook(Workbook workbook, String exportPath) {
        try {
            File fd = new File(exportPath);
            if (!fd.getParentFile().exists()) {
                fd.getParentFile().mkdirs();
            }
            try (FileOutputStream outputStream = new FileOutputStream(exportPath)) {
                workbook.write(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Workbook getAllConfigsWorkbook(String allStatsPath) {
        try {
            File file = new File(allStatsPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                createAllStatsFile(allStatsPath);
                file = new File(allStatsPath);
            }
            try (FileInputStream inputStream = new FileInputStream(file)) {
                return new XSSFWorkbook(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void createAllStatsFile(String allStatsPath) throws IOException, InvalidFormatException {
        try (FileOutputStream outputStream = new FileOutputStream(allStatsPath);
             Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("All Stats");
            createRunPoolHeaderRow(sheet, 0);
            workbook.write(outputStream);
        }
    }

    private void createPlotsGenerationDataHeader(Sheet sheet) {
        Row row = sheet.createRow(0);
        int i = 0;

        row.createCell(i++).setCellValue("Generation #");
        row.createCell(i++).setCellValue("avgF");
        row.createCell(i++).setCellValue("maxF");
        row.createCell(i++).setCellValue("optimalRatio");
        row.createCell(i++).setCellValue("bestRatio");
        row.createCell(i++).setCellValue("sigmaF");
        row.createCell(i++).setCellValue("unique_X");
        row.createCell(i++).setCellValue("Pr");
    }

    private void createPlotsGenerationDataHeaderConst(Sheet sheet) {
        Row row = sheet.createRow(0);
        int i = 0;

        row.createCell(i++).setCellValue("Generation #");
        row.createCell(i++).setCellValue("unique_X");
    }

    private void createPlotsIterationDataHeader(Sheet sheet, int rowIndex) {
        Row row = sheet.createRow(rowIndex);
        int i = 0;

        row.createCell(i++).setCellValue("Iteration #");
        row.createCell(i++).setCellValue("RR");
        row.createCell(i++).setCellValue("Teta");
        row.createCell(i++).setCellValue("difference");
        row.createCell(i++).setCellValue("intensity");
        row.createCell(i++).setCellValue("Gr");
        row.createCell(i++).setCellValue("Pfet");
    }

    private void createRunHeaderRow(Sheet sheet) {
        Row row = sheet.createRow(0);
        int i = 0;

        row.createCell(i++).setCellValue("Run #");

        // metrics for all functions
        row.createCell(i++).setCellValue("NI");
        row.createCell(i++).setCellValue("hasConverged");
        row.createCell(i++).setCellValue("IsSuc");

        row.createCell(i++).setCellValue("RR_start");
        row.createCell(i++).setCellValue("RR_fin");
        row.createCell(i++).setCellValue("RR_min");
        row.createCell(i++).setCellValue("NI_RR_min");
        row.createCell(i++).setCellValue("RR_max");
        row.createCell(i++).setCellValue("NI_RR_max");
        row.createCell(i++).setCellValue("RR_avg");

        row.createCell(i++).setCellValue("Teta_start");
        row.createCell(i++).setCellValue("Teta_fin");
        row.createCell(i++).setCellValue("Teta_min");
        row.createCell(i++).setCellValue("NI_Teta_min");
        row.createCell(i++).setCellValue("Teta_max");
        row.createCell(i++).setCellValue("NI_Teta_max");
        row.createCell(i++).setCellValue("Teta_avg");

        row.createCell(i++).setCellValue("unique_X_start");
        row.createCell(i++).setCellValue("unique_X_fin");

        // metrics for all functions except FConstAll
        row.createCell(i++).setCellValue("F_found");
        row.createCell(i++).setCellValue("F_avg");

        row.createCell(i++).setCellValue("NI_loose");
        row.createCell(i++).setCellValue("Num_loose");
        row.createCell(i++).setCellValue("optSaved_NI_loose");
        row.createCell(i++).setCellValue("MaxOptSaved_NI_loose");

        row.createCell(i++).setCellValue("s_start");
        row.createCell(i++).setCellValue("s_fin");
        row.createCell(i++).setCellValue("s_min");
        row.createCell(i++).setCellValue("NI_s_min");
        row.createCell(i++).setCellValue("s_max");
        row.createCell(i++).setCellValue("NI_s_max");
        row.createCell(i++).setCellValue("s_avg");

        row.createCell(i++).setCellValue("I_start");
        row.createCell(i++).setCellValue("I_min");
        row.createCell(i++).setCellValue("NI_I_min");
        row.createCell(i++).setCellValue("I_max");
        row.createCell(i++).setCellValue("NI_I_max");
        row.createCell(i++).setCellValue("I_avg");

        row.createCell(i++).setCellValue("GR_start");
        row.createCell(i++).setCellValue("GR_early");
        row.createCell(i++).setCellValue("GR_late");
        row.createCell(i++).setCellValue("NI_GR_late");
        row.createCell(i++).setCellValue("GR_avg");

        row.createCell(i++).setCellValue("Pr_start");
        row.createCell(i++).setCellValue("Pr_min");
        row.createCell(i++).setCellValue("NI_Pr_min");
        row.createCell(i++).setCellValue("Pr_max");
        row.createCell(i++).setCellValue("NI_Pr_max");
        row.createCell(i++).setCellValue("Pr_avg");

        row.createCell(i++).setCellValue("Fish_start");
        row.createCell(i++).setCellValue("Fish_min");
        row.createCell(i++).setCellValue("NI_Fish_min");
        row.createCell(i++).setCellValue("Fish_max");
        row.createCell(i++).setCellValue("NI_Fish_max");
        row.createCell(i++).setCellValue("Fish_avg");
    }

    private void createRunRow(Sheet sheet, int index, FitnessFunctionV2<?, ?> function, RunStats runStats) {
        Row row = sheet.createRow(index);
        int i = 0;

        row.createCell(i++).setCellValue(index);

        // metrics for all functions
        row.createCell(i++).setCellValue(runStats.ni());
        row.createCell(i++).setCellValue(runStats.hasConverged());
        row.createCell(i++).setCellValue(runStats.isSuc());

        row.createCell(i++).setCellValue(runStats.rrStart());
        row.createCell(i++).setCellValue(runStats.rrFin());
        row.createCell(i++).setCellValue(runStats.rrMin());
        row.createCell(i++).setCellValue(runStats.niRrMin());
        row.createCell(i++).setCellValue(runStats.rrMax());
        row.createCell(i++).setCellValue(runStats.niRrMax());
        row.createCell(i++).setCellValue(runStats.rrAvg());

        row.createCell(i++).setCellValue(runStats.tetaStart());
        row.createCell(i++).setCellValue(runStats.tetaFin());
        row.createCell(i++).setCellValue(runStats.tetaMin());
        row.createCell(i++).setCellValue(runStats.niTetaMin());
        row.createCell(i++).setCellValue(runStats.tetaMax());
        row.createCell(i++).setCellValue(runStats.niTetaMax());
        row.createCell(i++).setCellValue(runStats.tetaAvg());

        row.createCell(i++).setCellValue(runStats.uniqueXStart());
        row.createCell(i++).setCellValue(runStats.uniqueXFin());

        if (!function.isConstant()) {
            // metrics for all functions except FConstAll
            row.createCell(i++).setCellValue(runStats.fFound());
            row.createCell(i++).setCellValue(runStats.fAvg());

            row.createCell(i++).setCellValue(runStats.niLoose());
            row.createCell(i++).setCellValue(runStats.numLoose());
            row.createCell(i++).setCellValue(runStats.optSavedNILoose());
            row.createCell(i++).setCellValue(runStats.maxOptSavedNILoose());

            row.createCell(i++).setCellValue(runStats.sStart());
            row.createCell(i++).setCellValue(runStats.sFin());
            row.createCell(i++).setCellValue(runStats.sMin());
            row.createCell(i++).setCellValue(runStats.niSMin());
            row.createCell(i++).setCellValue(runStats.sMax());
            row.createCell(i++).setCellValue(runStats.niSMax());
            row.createCell(i++).setCellValue(runStats.sAvg());

            row.createCell(i++).setCellValue(runStats.iStart());
            row.createCell(i++).setCellValue(runStats.iMin());
            row.createCell(i++).setCellValue(runStats.niImin());
            row.createCell(i++).setCellValue(runStats.iMax());
            row.createCell(i++).setCellValue(runStats.niImax());
            row.createCell(i++).setCellValue(runStats.iAvg());

            row.createCell(i++).setCellValue(runStats.grStart());
            row.createCell(i++).setCellValue(runStats.grEarly());
            row.createCell(i++).setCellValue(runStats.grLate());
            row.createCell(i++).setCellValue(runStats.niGrLate());
            row.createCell(i++).setCellValue(runStats.grAvg());

            row.createCell(i++).setCellValue(runStats.prStart());
            row.createCell(i++).setCellValue(runStats.prMin());
            row.createCell(i++).setCellValue(runStats.niPrMin());
            row.createCell(i++).setCellValue(runStats.prMax());
            row.createCell(i++).setCellValue(runStats.niPrMax());
            row.createCell(i++).setCellValue(runStats.prAvg());

            row.createCell(i++).setCellValue(runStats.fishStart());
            row.createCell(i++).setCellValue(runStats.fishMin());
            row.createCell(i++).setCellValue(runStats.niFishMin());
            row.createCell(i++).setCellValue(runStats.fishMax());
            row.createCell(i++).setCellValue(runStats.niFishMax());
            row.createCell(i++).setCellValue(runStats.fishAvg());
        }
    }

    private void createRunPoolHeaderRow(Sheet sheet, int index) {
        Row row = sheet.createRow(index);
        int i = 0;

        row.createCell(i++).setCellValue("Config #");
        row.createCell(i++).setCellValue("N");
        row.createCell(i++).setCellValue("Function");
        row.createCell(i++).setCellValue("Selector");
        row.createCell(i++).setCellValue("Operator");
        row.createCell(i++).setCellValue("Population Type");
        row.createCell(i++).setCellValue("Encoding");

        // all functions
        // successful runs
        row.createCell(i++).setCellValue("Suc");
        row.createCell(i++).setCellValue("Min_NI");
        row.createCell(i++).setCellValue("Max_NI");
        row.createCell(i++).setCellValue("Avg_NI");
        row.createCell(i++).setCellValue("Sigma_NI");

        row.createCell(i++).setCellValue("Min_RR_min");
        row.createCell(i++).setCellValue("NI_Min_RR_min");
        row.createCell(i++).setCellValue("Max_RR_max");
        row.createCell(i++).setCellValue("NI_Max_RR_max");
        row.createCell(i++).setCellValue("Avg_RR_min");
        row.createCell(i++).setCellValue("Avg_RR_max");
        row.createCell(i++).setCellValue("Avg_RR_avg");

        row.createCell(i++).setCellValue("Min_Teta_min");
        row.createCell(i++).setCellValue("NI_Min_Teta_min");
        row.createCell(i++).setCellValue("Max_Teta_max");
        row.createCell(i++).setCellValue("NI_Max_Teta_max");
        row.createCell(i++).setCellValue("Avg_Teta_min");
        row.createCell(i++).setCellValue("Avg_Teta_max");
        row.createCell(i++).setCellValue("Avg_Teta_avg");

        row.createCell(i++).setCellValue("Sigma_RR_min");
        row.createCell(i++).setCellValue("Sigma_RR_max");
        row.createCell(i++).setCellValue("Sigma_RR_avg");
        row.createCell(i++).setCellValue("Sigma_Teta_min");
        row.createCell(i++).setCellValue("Sigma_Teta_max");
        row.createCell(i++).setCellValue("Sigma_Teta_avg");

        row.createCell(i++).setCellValue("Min_RR_start");
        row.createCell(i++).setCellValue("Max_RR_start");
        row.createCell(i++).setCellValue("Avg_RR_start");
        row.createCell(i++).setCellValue("Sigma_RR_start");

        row.createCell(i++).setCellValue("Min_Teta_start");
        row.createCell(i++).setCellValue("Max_Teta_start");
        row.createCell(i++).setCellValue("Avg_Teta_start");
        row.createCell(i++).setCellValue("Sigma_Teta_start");

        row.createCell(i++).setCellValue("Avg_RR_fin");
        row.createCell(i++).setCellValue("Sigma_RR_fin");
        row.createCell(i++).setCellValue("Avg_Teta_fin");
        row.createCell(i++).setCellValue("Sigma_Teta_fin");

        row.createCell(i++).setCellValue("Min_unique_X_start");
        row.createCell(i++).setCellValue("Max_unique_X_start");
        row.createCell(i++).setCellValue("Avg_unique_X_start");
        row.createCell(i++).setCellValue("Sigma_unique_X_start");

        row.createCell(i++).setCellValue("Min_unique_X_fin");
        row.createCell(i++).setCellValue("Max_unique_X_fin");
        row.createCell(i++).setCellValue("Avg_unique_X_fin");
        row.createCell(i++).setCellValue("Sigma_unique_X_fin");

        // all functions except FConstAll

        // non-successful but converged runs
        row.createCell(i++).setCellValue("nonSuc");
        row.createCell(i++).setCellValue("nonMin_NI");
        row.createCell(i++).setCellValue("nonMax_NI");
        row.createCell(i++).setCellValue("nonAvg_NI");
        row.createCell(i++).setCellValue("nonSigma_NI");
        row.createCell(i++).setCellValue("nonMax_F_found");
        row.createCell(i++).setCellValue("nonAvg_F_found");
        row.createCell(i++).setCellValue("nonSigma_F_found");

        // successful runs
        row.createCell(i++).setCellValue("Min_s_min");
        row.createCell(i++).setCellValue("NI_s_min");
        row.createCell(i++).setCellValue("Max_s_max");
        row.createCell(i++).setCellValue("NI_s_max");
        row.createCell(i++).setCellValue("Avg_s_min");
        row.createCell(i++).setCellValue("Avg_s_max");
        row.createCell(i++).setCellValue("Avg_s_avg");
        row.createCell(i++).setCellValue("Min_s_start");
        row.createCell(i++).setCellValue("Max_s_start");
        row.createCell(i++).setCellValue("Avg_s_start");
        row.createCell(i++).setCellValue("Sigma_s_start");

        row.createCell(i++).setCellValue("Min_I_min");
        row.createCell(i++).setCellValue("NI_I_min");
        row.createCell(i++).setCellValue("Max_I_max");
        row.createCell(i++).setCellValue("NI_I_max");
        row.createCell(i++).setCellValue("Avg_I_min");
        row.createCell(i++).setCellValue("Avg_I_max");
        row.createCell(i++).setCellValue("Avg_I_avg");
        row.createCell(i++).setCellValue("Sigma_I_min");
        row.createCell(i++).setCellValue("Sigma_I_max");
        row.createCell(i++).setCellValue("Sigma_I_avg");
        row.createCell(i++).setCellValue("Min_I_start");
        row.createCell(i++).setCellValue("Max_I_start");
        row.createCell(i++).setCellValue("Avg_I_start");
        row.createCell(i++).setCellValue("Sigma_I_start");

        row.createCell(i++).setCellValue("MinGR_early");
        row.createCell(i++).setCellValue("MaxGR_early");
        row.createCell(i++).setCellValue("AvgGR_early");
        row.createCell(i++).setCellValue("MinGR_late");
        row.createCell(i++).setCellValue("MaxGR_late");
        row.createCell(i++).setCellValue("AvgGR_late");
        row.createCell(i++).setCellValue("MinGR_avg");
        row.createCell(i++).setCellValue("MaxGR_avg");
        row.createCell(i++).setCellValue("AvgGR_avg");
        row.createCell(i++).setCellValue("Min_GR_start");
        row.createCell(i++).setCellValue("Max_GR_start");
        row.createCell(i++).setCellValue("Avg_GR_start");
        row.createCell(i++).setCellValue("Sigma_GR_start");

        row.createCell(i++).setCellValue("Min_Pr_min");
        row.createCell(i++).setCellValue("NI_Pr_min");
        row.createCell(i++).setCellValue("Max_Pr_max");
        row.createCell(i++).setCellValue("NI_Pr_max");
        row.createCell(i++).setCellValue("Avg_Pr_min");
        row.createCell(i++).setCellValue("Avg_Pr_max");
        row.createCell(i++).setCellValue("Avg_Pr_avg");
        row.createCell(i++).setCellValue("Sigma_Pr_min");
        row.createCell(i++).setCellValue("Sigma_Pr_max");
        row.createCell(i++).setCellValue("Sigma_Pr_avg");
        row.createCell(i++).setCellValue("Min_Pr_start");
        row.createCell(i++).setCellValue("Max_Pr_start");
        row.createCell(i++).setCellValue("Avg_Pr_start");
        row.createCell(i++).setCellValue("Sigma_Pr_start");

        // all runs
        row.createCell(i++).setCellValue("NI_with_Loose");
        row.createCell(i++).setCellValue("Avg_NI_loose");
        row.createCell(i++).setCellValue("Sigma_NI_loose");
        row.createCell(i++).setCellValue("Avg_Num_loose");
        row.createCell(i++).setCellValue("Sigma_Num_loose");
        row.createCell(i++).setCellValue("Avg_optSaved_NI_loose");
        row.createCell(i++).setCellValue("Sigma_optSaved_NI_loose");
        row.createCell(i++).setCellValue("Avg_MaxOptSaved_NI_loose");
        row.createCell(i++).setCellValue("Sigma_MaxOptSaved_NI_loose");
    }

    private void createRunPoolRow(Sheet sheet, int index, int configNumber, RunPoolStats runPoolStats) {
        Row row = sheet.createRow(index);
        int i = 0;

        RunConfiguration runConfiguration = runPoolStats.runConfiguration();

        row.createCell(i++).setCellValue(configNumber);
        row.createCell(i++).setCellValue(runConfiguration.populationSize());
        row.createCell(i++).setCellValue(runConfiguration.function().getName());
        row.createCell(i++).setCellValue(runConfiguration.selector().getName());
        row.createCell(i++).setCellValue(runConfiguration.operator().getName());
        row.createCell(i++).setCellValue(runConfiguration.populationType().name());
        row.createCell(i++).setCellValue(runConfiguration.encoding().name());

        // all functions
        // successful runs
        row.createCell(i++).setCellValue(runPoolStats.suc());
        row.createCell(i++).setCellValue(runPoolStats.minNI());
        row.createCell(i++).setCellValue(runPoolStats.maxNI());
        row.createCell(i++).setCellValue(runPoolStats.avgNI());
        row.createCell(i++).setCellValue(runPoolStats.sigmaNI());

        row.createCell(i++).setCellValue(runPoolStats.minRRMin());
        row.createCell(i++).setCellValue(runPoolStats.niMinRRMin());
        row.createCell(i++).setCellValue(runPoolStats.maxRRMax());
        row.createCell(i++).setCellValue(runPoolStats.niMaxRRMax());
        row.createCell(i++).setCellValue(runPoolStats.avgRRMin());
        row.createCell(i++).setCellValue(runPoolStats.avgRRMax());
        row.createCell(i++).setCellValue(runPoolStats.avgRRAvg());

        row.createCell(i++).setCellValue(runPoolStats.minTetaMin());
        row.createCell(i++).setCellValue(runPoolStats.niMinTetaMin());
        row.createCell(i++).setCellValue(runPoolStats.maxTetaMax());
        row.createCell(i++).setCellValue(runPoolStats.niMaxTetaMax());
        row.createCell(i++).setCellValue(runPoolStats.avgTetaMin());
        row.createCell(i++).setCellValue(runPoolStats.avgTetaMax());
        row.createCell(i++).setCellValue(runPoolStats.avgTetaAvg());

        row.createCell(i++).setCellValue(runPoolStats.sigmaRRMin());
        row.createCell(i++).setCellValue(runPoolStats.sigmaRRMax());
        row.createCell(i++).setCellValue(runPoolStats.sigmaRRAvg());
        row.createCell(i++).setCellValue(runPoolStats.sigmaTetaMin());
        row.createCell(i++).setCellValue(runPoolStats.sigmaTetaMax());
        row.createCell(i++).setCellValue(runPoolStats.sigmaTetaAvg());

        row.createCell(i++).setCellValue(runPoolStats.minRRStart());
        row.createCell(i++).setCellValue(runPoolStats.maxRRStart());
        row.createCell(i++).setCellValue(runPoolStats.avgRRStart());
        row.createCell(i++).setCellValue(runPoolStats.sigmaRRStart());

        row.createCell(i++).setCellValue(runPoolStats.minTetaStart());
        row.createCell(i++).setCellValue(runPoolStats.maxTetaStart());
        row.createCell(i++).setCellValue(runPoolStats.avgTetaStart());
        row.createCell(i++).setCellValue(runPoolStats.sigmaTetaStart());

        row.createCell(i++).setCellValue(runPoolStats.avgRRFin());
        row.createCell(i++).setCellValue(runPoolStats.sigmaRRFin());
        row.createCell(i++).setCellValue(runPoolStats.avgTetaFin());
        row.createCell(i++).setCellValue(runPoolStats.sigmaTetaFin());

        row.createCell(i++).setCellValue(runPoolStats.minUniqueXStart());
        row.createCell(i++).setCellValue(runPoolStats.maxUniqueXStart());
        row.createCell(i++).setCellValue(runPoolStats.avgUniqueXStart());
        row.createCell(i++).setCellValue(runPoolStats.sigmaUniqueXStart());

        row.createCell(i++).setCellValue(runPoolStats.minUniqueXFin());
        row.createCell(i++).setCellValue(runPoolStats.maxUniqueXFin());
        row.createCell(i++).setCellValue(runPoolStats.avgUniqueXFin());
        row.createCell(i++).setCellValue(runPoolStats.sigmaUniqueXFin());

        // all functions except FConstAll
        if (!runPoolStats.runConfiguration().function().isConstant()) {
            // non-successful but converged runs
            row.createCell(i++).setCellValue(runPoolStats.nonSuc());
            row.createCell(i++).setCellValue(runPoolStats.nonMinNI());
            row.createCell(i++).setCellValue(runPoolStats.nonMaxNI());
            row.createCell(i++).setCellValue(runPoolStats.nonAvgNI());
            row.createCell(i++).setCellValue(runPoolStats.nonSigmaNI());
            row.createCell(i++).setCellValue(runPoolStats.nonMaxFFound());
            row.createCell(i++).setCellValue(runPoolStats.nonAvgFFound());
            row.createCell(i++).setCellValue(runPoolStats.nonSigmaFFound());


            // successful runs
            row.createCell(i++).setCellValue(runPoolStats.minSMin());
            row.createCell(i++).setCellValue(runPoolStats.niSMin());
            row.createCell(i++).setCellValue(runPoolStats.maxSMax());
            row.createCell(i++).setCellValue(runPoolStats.niSMax());
            row.createCell(i++).setCellValue(runPoolStats.avgSMin());
            row.createCell(i++).setCellValue(runPoolStats.avgSMax());
            row.createCell(i++).setCellValue(runPoolStats.avgSAvg());
            row.createCell(i++).setCellValue(runPoolStats.minSStart());
            row.createCell(i++).setCellValue(runPoolStats.maxSStart());
            row.createCell(i++).setCellValue(runPoolStats.avgSStart());
            row.createCell(i++).setCellValue(runPoolStats.sigmaSStart());

            row.createCell(i++).setCellValue(runPoolStats.minImin());
            row.createCell(i++).setCellValue(runPoolStats.niMinImin());
            row.createCell(i++).setCellValue(runPoolStats.maxImax());
            row.createCell(i++).setCellValue(runPoolStats.niMaxImax());
            row.createCell(i++).setCellValue(runPoolStats.avgImin());
            row.createCell(i++).setCellValue(runPoolStats.avgImax());
            row.createCell(i++).setCellValue(runPoolStats.avgIavg());
            row.createCell(i++).setCellValue(runPoolStats.sigmaImin());
            row.createCell(i++).setCellValue(runPoolStats.sigmaImax());
            row.createCell(i++).setCellValue(runPoolStats.sigmaIavg());
            row.createCell(i++).setCellValue(runPoolStats.minIstart());
            row.createCell(i++).setCellValue(runPoolStats.maxIstart());
            row.createCell(i++).setCellValue(runPoolStats.avgIstart());
            row.createCell(i++).setCellValue(runPoolStats.sigmaIstart());

            row.createCell(i++).setCellValue(runPoolStats.minGrEarly());
            row.createCell(i++).setCellValue(runPoolStats.maxGrEarly());
            row.createCell(i++).setCellValue(runPoolStats.avgGrEarly());
            row.createCell(i++).setCellValue(runPoolStats.minGrLate());
            row.createCell(i++).setCellValue(runPoolStats.maxGrLate());
            row.createCell(i++).setCellValue(runPoolStats.avgGrLate());
            row.createCell(i++).setCellValue(runPoolStats.minGrAvg());
            row.createCell(i++).setCellValue(runPoolStats.maxGrAvg());
            row.createCell(i++).setCellValue(runPoolStats.avgGrAvg());
            row.createCell(i++).setCellValue(runPoolStats.minGrStart());
            row.createCell(i++).setCellValue(runPoolStats.maxGrStart());
            row.createCell(i++).setCellValue(runPoolStats.avgGrStart());
            row.createCell(i++).setCellValue(runPoolStats.sigmaGrStart());

            row.createCell(i++).setCellValue(runPoolStats.minPrMin());
            row.createCell(i++).setCellValue(runPoolStats.niMinPrMin());
            row.createCell(i++).setCellValue(runPoolStats.maxPrMax());
            row.createCell(i++).setCellValue(runPoolStats.niMaxPrMax());
            row.createCell(i++).setCellValue(runPoolStats.avgPrMin());
            row.createCell(i++).setCellValue(runPoolStats.avgPrMax());
            row.createCell(i++).setCellValue(runPoolStats.avgPrAvg());
            row.createCell(i++).setCellValue(runPoolStats.sigmaPrMin());
            row.createCell(i++).setCellValue(runPoolStats.sigmaPrMax());
            row.createCell(i++).setCellValue(runPoolStats.sigmaPrAvg());
            row.createCell(i++).setCellValue(runPoolStats.minPrStart());
            row.createCell(i++).setCellValue(runPoolStats.maxPrStart());
            row.createCell(i++).setCellValue(runPoolStats.avgPrStart());
            row.createCell(i++).setCellValue(runPoolStats.sigmaPrStart());

            // all runs
            row.createCell(i++).setCellValue(runPoolStats.niWithLoose());
            row.createCell(i++).setCellValue(runPoolStats.avgNILoose());
            row.createCell(i++).setCellValue(runPoolStats.sigmaNILoose());
            row.createCell(i++).setCellValue(runPoolStats.avgNumLoose());
            row.createCell(i++).setCellValue(runPoolStats.sigmaNumLoose());
            row.createCell(i++).setCellValue(runPoolStats.avgOptSavedNILoose());
            row.createCell(i++).setCellValue(runPoolStats.sigmaOptSavedNILoose());
            row.createCell(i++).setCellValue(runPoolStats.avgMaxOptSavedNILoose());
            row.createCell(i++).setCellValue(runPoolStats.sigmaMaxOptSavedNILoose());
        }
    }
}
