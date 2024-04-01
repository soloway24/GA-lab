package lab.v2.export;

import lab.parameters.Encoding;
import lab.v2.function.FitnessFunctionV2;
import lab.v2.operator.Operator;
import lab.v2.run.RunConfiguration;
import lab.v2.run.RunPoolStats;
import lab.v2.run.RunStats;
import lab.v2.selection.Selector;
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

public class Export {

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

    public void exportRunPoolStats(RunPoolStats runPoolStats) {
        RunConfiguration runConfiguration = runPoolStats.runConfiguration();
        FitnessFunctionV2<?, ? extends Number> function = runConfiguration.function();
        Selector selector = runConfiguration.selector();
        Operator operator = runConfiguration.operator();
        Encoding encoding = runConfiguration.encoding();
        List<RunStats> allRunStats = runPoolStats.allRunStats();

        String filename = getFileName(runConfiguration);

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Stats");

        String tablePath = TABLES_PATH + runConfiguration.populationSize() + "/" + filename + ".xlsx";
        String allStatsTablePath = TABLES_PATH + runConfiguration.populationSize() + "/" + ALL_STATS_TABLE_NAME;
        Workbook allStatsWorkbook = getAllConfigsWorkbook(allStatsTablePath);
//        String plotExportPath = PLOTS_PATH + filename;
//
//        File theDir1 = new File(plotExportPath);
//        if (!theDir1.exists()) {
//            theDir1.mkdirs();
//        }

        createRunHeaderRow(sheet);
        IntStream.range(0, allRunStats.size())
                .forEach(i -> createRunRow(sheet, i + 1, function, allRunStats.get(i)));

        int runPoolStatsRowIndex = allRunStats.size() + 1 + 2;
        createRunPoolHeaderRow(sheet, runPoolStatsRowIndex);
        createRunPoolRow(sheet, runPoolStatsRowIndex + 1, 1, runPoolStats);

        IntStream.range(0, COLUMN_NUMBER)
                .forEach(sheet::autoSizeColumn);

        Sheet allStatsSheet = allStatsWorkbook.getSheetAt(0);
        int runPoolRowIndex = getFirstNullRowIndex(allStatsSheet);
        createRunPoolRow(allStatsSheet, runPoolRowIndex, runPoolRowIndex, runPoolStats);
        IntStream.range(0, COLUMN_NUMBER)
                .forEach(allStatsSheet::autoSizeColumn);
//        for (int i = 0; i < 5 && i < allRunStats.size(); i++) {
//            List<Long> x = allRunStats.get(i).stats.stream().mapToLong(entry -> entry.NI).boxed().toList();
//            List<Double> RR = allRunStats.get(i).stats.stream().mapToDouble(entry -> entry.RR).boxed().toList();
//            List<Double> TETA = allRunStats.get(i).stats.stream().mapToDouble(entry -> entry.TETA).boxed().toList();
//
//            drawPlot(x, RR, plotExportPath + "/" + (i + 1) + "/", "RR");
//            drawPlot(x, TETA, plotExportPath + "/" + (i + 1) + "/", "TETA");
//            drawPlot(x, RR, TETA, plotExportPath + "/" + (i + 1) + "/", "RR_TETA");
//
//            if (allRunStats.get(0).getFitnessFunction() != FitnessFunction.F_ALL_CONST) {
//                List<Double> F_avg = allRunStats.get(i).stats.stream().mapToDouble(entry -> entry.F_avg).boxed().toList();
//                List<Double> F_found = allRunStats.get(i).stats.stream().mapToDouble(entry -> entry.F_found).boxed().toList();
//                List<Double> Intensity = allRunStats.get(i).stats.stream().mapToDouble(entry -> entry.Intensity).boxed().toList();
//                List<Double> Diversity = allRunStats.get(i).stats.stream().mapToDouble(entry -> entry.Diversity).boxed().toList();
//                List<Double> GR = allRunStats.get(i).stats.stream().mapToDouble(entry -> entry.GR).boxed().toList();
//                List<Double> Sigma = allRunStats.get(i).stats.stream().mapToDouble(entry -> entry.Sigma).boxed().toList();
//                List<Double> BestPercent = allRunStats.get(i).stats.stream().mapToDouble(entry -> entry.BestPercent).boxed().toList();
//
//                drawPlot(x, F_avg, plotExportPath + "/" + (i + 1) + "/", "F_avg");
//                drawPlot(x, F_found, plotExportPath + "/" + (i + 1) + "/", "F_found");
//                drawPlot(x, Intensity, plotExportPath + "/" + (i + 1) + "/", "Intensity");
//                drawPlot(x, Diversity, plotExportPath + "/" + (i + 1) + "/", "Diversity");
//                drawPlot(x, GR, plotExportPath + "/" + (i + 1) + "/", "GR");
//                drawPlot(x, Sigma, plotExportPath + "/" + (i + 1) + "/", "Sigma");
//                drawPlot(x, BestPercent, plotExportPath + "/" + (i + 1) + "/", "Percentage_of_best_individuals");
//                drawPlot(x, Intensity, Diversity, plotExportPath + "/" + (i + 1) + "/", "Intensity_Diversity");
//            }
//
//            for (int j = 0; j < 5 && j < allRunStats.get(i).stats.size(); j++) {
//                drawHistograms(plotExportPath + "/" + (i + 1) + "/" + j + "/", allRunStats.get(i).stats.get(j));
//            }
//
//            int index = allRunStats.get(i).stats.size() - 1;
//            drawHistograms(plotExportPath + "/" + (i + 1) + "/final/", allRunStats.get(i).stats.get(index));
//        }

        saveWorkbook(workbook, tablePath);
        saveWorkbook(allStatsWorkbook, allStatsTablePath);
    }

//    private static void drawHistograms(String exportPath, RunStatsData data) {
//        File theDir1 = new File(exportPath);
//        if (!theDir1.exists()) {
//            theDir1.mkdirs();
//        }
//
//        drawHistogram(data.ones, exportPath, "count_ones", 0, GeneticUtils.FITNESS_FUNCTION.getLength());
//        if (GeneticUtils.FITNESS_FUNCTION == FitnessFunction.QUAD || GeneticUtils.FITNESS_FUNCTION == FitnessFunction.QUAD_SYM) {
//            drawHistogram(data.fitness, exportPath, "fitness", GeneticUtils.FITNESS_FUNCTION.getMin(), GeneticUtils.FITNESS_FUNCTION.getMax());
//            drawHistogram(data.phenotypes, exportPath, "phenotypes", GeneticUtils.FITNESS_FUNCTION.getMinX(), GeneticUtils.FITNESS_FUNCTION.getMaxX());
//        }
//    }

//    private static void drawPlot(List<Long> x, List<Double> y, String out, String filename) {
//        try {
//
//            File theDir1 = new File(out);
//            if (!theDir1.exists()) {
//                theDir1.mkdirs();
//            }
//
//            Plot plt = Plot.create();
//            plt.plot().add(x, y);
//            plt.title(filename);
//            plt.savefig(out + filename + ".png");
//            plt.executeSilently();
//        } catch (IOException | PythonExecutionException e) {
//            e.printStackTrace();
//        }
//    }

//    private static void drawPlot(List<Long> x, List<Double> y, List<Double> y1, String out, String filename) {
//        try {
//            File theDir1 = new File(out);
//            if (!theDir1.exists()) {
//                theDir1.mkdirs();
//            }
//
//            Plot plt = Plot.create();
//            plt.plot().add(x, y).color("red");
//            plt.plot().add(x, y1).color("blue");
//
//            plt.title(filename);
//            plt.savefig(out + filename + ".png");
//            plt.executeSilently();
//        } catch (IOException | PythonExecutionException e) {
//            e.printStackTrace();
//        }
//    }


//    private static List<Double> bins(double min, double max, float step) {
//        List<Double> res = new ArrayList<>();
//        for (double i = min - 1; i <= max + 1; i += step) {
//            res.add(i);
//        }
//        return res;
//    }

//    private static void drawHistogram(List<? extends Number> x, String out, String filename, double minX, double maxX) {
//
//        if (x == null)
//            return;
//
//        File theDir = new File(out);
//        if (!theDir.exists()) {
//            theDir.mkdirs();
//        }
//
//        try {
//            Plot plt = Plot.create();
//            plt.hist().add(x).orientation(HistBuilder.Orientation.vertical).bins(bins(minX, maxX, 1));
//            plt.title(filename);
//            plt.ylim(0, N);
//
//            double dist = (maxX - minX) / 10.f;
//            plt.xlim(minX - dist, maxX + dist);
//            plt.savefig(out + filename + ".png");
//
//            plt.executeSilently();
//        } catch (IOException | PythonExecutionException e) {
//            e.printStackTrace();
//        }
//    }

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
//    public static void exportRunPools(List<RunPoolStatsData> runPools, String prefix) {
//        XSSFWorkbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Stats");
//
//        String exportPath = TABLES_PATH + prefix + "all_stats.xlsx";
//
//        createRunPoolHeaderRow(sheet, 0);
//        for (int i = 0; i < runPools.size(); i++) {
//            createRunPoolRow(sheet, i + 1, runPools.get(i));
//        }
//
//        try {
//            File currDir = new File(".");
//            String path = currDir.getAbsolutePath();
//            String fileLocation = path.substring(0, path.length() - 1) + exportPath;
//            File fd = new File(fileLocation);
//            fd.getParentFile().mkdirs();
//
//            FileOutputStream outputStream = new FileOutputStream(fileLocation);
//            workbook.write(outputStream);
//            workbook.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private static void createRunHeaderRow(Sheet sheet) {
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
        row.createCell(i).setCellValue("s_avg");
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
            row.createCell(i).setCellValue(runStats.sAvg());
        }
    }

    private static void createRunPoolHeaderRow(Sheet sheet, int index) {
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

    private static void createRunPoolRow(Sheet sheet, int index, int configNumber, RunPoolStats runPoolStats) {
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
