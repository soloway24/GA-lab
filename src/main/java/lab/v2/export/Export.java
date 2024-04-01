package lab.v2.export;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import com.github.sh0nk.matplotlib4j.builder.HistBuilder;
import lab.function.FitnessFunction;
import lab.parameters.Encoding;
import lab.stats.RunPoolStatsData;
import lab.stats.RunStatsData;
import lab.utils.GeneticUtils;
import lab.v2.function.FitnessFunctionV2;
import lab.v2.operator.Operator;
import lab.v2.run.RunConfiguration;
import lab.v2.run.RunPoolStats;
import lab.v2.run.RunStats;
import lab.v2.selection.Selector;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static lab.utils.GeneticUtils.N;

public class Export {

    private static final String STATS_PATH = Paths.get(".")
            .toAbsolutePath()
            .normalize()
            .toString()
            .replace("\\", "/")
            + "/stats_v2/";

    private static final String TABLES_PATH = STATS_PATH + "tables/";
    private static final String PLOTS_PATH = STATS_PATH + "plots/";

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
//        String plotExportPath = PLOTS_PATH + filename;
//
//        File theDir1 = new File(plotExportPath);
//        if (!theDir1.exists()) {
//            theDir1.mkdirs();
//        }

        createRunHeaderRow(sheet);
        IntStream.range(0, allRunStats.size())
                .forEach(i -> createRunRow(sheet, i + 1, function, allRunStats.get(i)));

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
    }

    private static void drawHistograms(String exportPath, RunStatsData data) {
        File theDir1 = new File(exportPath);
        if (!theDir1.exists()) {
            theDir1.mkdirs();
        }

        drawHistogram(data.ones, exportPath, "count_ones", 0, GeneticUtils.FITNESS_FUNCTION.getLength());
        if (GeneticUtils.FITNESS_FUNCTION == FitnessFunction.QUAD || GeneticUtils.FITNESS_FUNCTION == FitnessFunction.QUAD_SYM) {
            drawHistogram(data.fitness, exportPath, "fitness", GeneticUtils.FITNESS_FUNCTION.getMin(), GeneticUtils.FITNESS_FUNCTION.getMax());
            drawHistogram(data.phenotypes, exportPath, "phenotypes", GeneticUtils.FITNESS_FUNCTION.getMinX(), GeneticUtils.FITNESS_FUNCTION.getMaxX());
        }
    }

    private static void drawPlot(List<Long> x, List<Double> y, String out, String filename) {
        try {

            File theDir1 = new File(out);
            if (!theDir1.exists()) {
                theDir1.mkdirs();
            }

            Plot plt = Plot.create();
            plt.plot().add(x, y);
            plt.title(filename);
            plt.savefig(out + filename + ".png");
            plt.executeSilently();
        } catch (IOException | PythonExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void drawPlot(List<Long> x, List<Double> y, List<Double> y1, String out, String filename) {
        try {
            File theDir1 = new File(out);
            if (!theDir1.exists()) {
                theDir1.mkdirs();
            }

            Plot plt = Plot.create();
            plt.plot().add(x, y).color("red");
            plt.plot().add(x, y1).color("blue");

            plt.title(filename);
            plt.savefig(out + filename + ".png");
            plt.executeSilently();
        } catch (IOException | PythonExecutionException e) {
            e.printStackTrace();
        }
    }


    private static List<Double> bins(double min, double max, float step) {
        List<Double> res = new ArrayList<>();
        for (double i = min - 1; i <= max + 1; i += step) {
            res.add(i);
        }
        return res;
    }

    private static void drawHistogram(List<? extends Number> x, String out, String filename, double minX, double maxX) {

        if (x == null)
            return;

        File theDir = new File(out);
        if (!theDir.exists()) {
            theDir.mkdirs();
        }

        try {
            Plot plt = Plot.create();
            plt.hist().add(x).orientation(HistBuilder.Orientation.vertical).bins(bins(minX, maxX, 1));
            plt.title(filename);
            plt.ylim(0, N);

            double dist = (maxX - minX) / 10.f;
            plt.xlim(minX - dist, maxX + dist);
            plt.savefig(out + filename + ".png");

            plt.executeSilently();
        } catch (IOException | PythonExecutionException e) {
            e.printStackTrace();
        }
    }


    private static void saveWorkbook(XSSFWorkbook workbook, String exportPath) {
        try {
            File fd = new File(exportPath);
            if (!fd.getParentFile().exists()) {
                fd.getParentFile().mkdirs();
            }

            FileOutputStream outputStream = new FileOutputStream(exportPath);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportRunPools(List<RunPoolStatsData> runPools, String prefix) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Stats");

        String exportPath = "stats/runs/" + prefix + "all_stats.xlsx";

        createHeaderRow(sheet, 0);
        for (int i = 0; i < runPools.size(); i++) {
            createPoolRow(sheet, i + 1, runPools.get(i));
        }

        try {
            File currDir = new File(".");
            String path = currDir.getAbsolutePath();
            String fileLocation = path.substring(0, path.length() - 1) + exportPath;
            File fd = new File(fileLocation);
            fd.getParentFile().mkdirs();

            FileOutputStream outputStream = new FileOutputStream(fileLocation);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private static void createHeaderRow(Sheet sheet, int index) {
        Row row = sheet.createRow(index);
        int i = 0;
        row.createCell(i++).setCellValue("Function");
        row.createCell(i++).setCellValue("N");
        row.createCell(i++).setCellValue("Tournament");
        row.createCell(i++).setCellValue("Selection Probability");
        row.createCell(i++).setCellValue("Genetic operators");
        row.createCell(i++).setCellValue("Encoding");
        row.createCell(i++).setCellValue("Suc");
        row.createCell(i++).setCellValue("NI_min");
        row.createCell(i++).setCellValue("NI_max");
        row.createCell(i++).setCellValue("NI_Avg");
        row.createCell(i++).setCellValue("NI_Sigma");

        row.createCell(i++).setCellValue("Min_RR_min");
        row.createCell(i++).setCellValue("Max_RR_max");
        row.createCell(i++).setCellValue("Avg_RR_min");
        row.createCell(i++).setCellValue("Avg_RR_max");
        row.createCell(i++).setCellValue("Avg_RR_avg");
        row.createCell(i++).setCellValue("NI_RR_min");
        row.createCell(i++).setCellValue("NI_RR_max");
        row.createCell(i++).setCellValue("Sigma_RR_max");
        row.createCell(i++).setCellValue("Sigma_RR_min");
        row.createCell(i++).setCellValue("Sigma_RR_avg");
        row.createCell(i++).setCellValue("Min_Teta_min");
        row.createCell(i++).setCellValue("Max_Teta_max");
        row.createCell(i++).setCellValue("Avg_Teta_min");
        row.createCell(i++).setCellValue("Avg_Teta_max");
        row.createCell(i++).setCellValue("Avg_Teta_avg");
        row.createCell(i++).setCellValue("NI_Teta_min");
        row.createCell(i++).setCellValue("NI_Teta_max");
        row.createCell(i++).setCellValue("Sigma_Teta_max");
        row.createCell(i++).setCellValue("Sigma_Teta_min");
        row.createCell(i++).setCellValue("Sigma_Teta_avg");

        // if (GeneticUtils.FITNESS_FUNCTION != FitnessFunction.F_ALL_CONST) {
        row.createCell(i++).setCellValue("NI_I_min");
        row.createCell(i++).setCellValue("NI_I_max");
        row.createCell(i++).setCellValue("Min_I_min");
        row.createCell(i++).setCellValue("Max_I_max");
        row.createCell(i++).setCellValue("Avg_I_min");
        row.createCell(i++).setCellValue("Avg_I_max");
        row.createCell(i++).setCellValue("Avg_I_avg");
        row.createCell(i++).setCellValue("Sigma_I_max");
        row.createCell(i++).setCellValue("Sigma_I_min");
        row.createCell(i++).setCellValue("Sigma_I_avg");
        row.createCell(i++).setCellValue("Min_GR_early");
        row.createCell(i++).setCellValue("Max_GR_early");
        row.createCell(i++).setCellValue("Avg_GR_early");
        row.createCell(i++).setCellValue("Min_GR_late");
        row.createCell(i++).setCellValue("Max_GR_late");
        row.createCell(i++).setCellValue("Avg_GR_late");
        row.createCell(i++).setCellValue("Min_GR_avg");
        row.createCell(i++).setCellValue("Max_GR_avg");
        row.createCell(i++).setCellValue("Avg_GR_avg");
        row.createCell(i++).setCellValue("Min_s_min");
        row.createCell(i++).setCellValue("Max_s_max");
        row.createCell(i++).setCellValue("Avg_s_min");
        row.createCell(i++).setCellValue("Avg_s_max");
        row.createCell(i++).setCellValue("Avg_s_avg");
        row.createCell(i++).setCellValue("NI_s_min");
        row.createCell(i).setCellValue("NI_s_max");
        //  }
    }

    private static void createPoolRow(Sheet sheet, int index, RunPoolStatsData pool) {
        Row row = sheet.createRow(index);
        int i = 0;
        row.createCell(i++).setCellValue(pool.fitnessFunction.getOutPath());
        row.createCell(i++).setCellValue(pool.N);
        row.createCell(i++).setCellValue(pool.contestType.getOutPath());
        row.createCell(i++).setCellValue(pool.P_SWAP);
        row.createCell(i++).setCellValue(pool.geneticOperatorsApplication.getOutPath());
        row.createCell(i++).setCellValue(pool.encoding.getOutPath());
        row.createCell(i++).setCellValue(pool.Suc);
        row.createCell(i++).setCellValue(pool.Min_NI);
        row.createCell(i++).setCellValue(pool.Max_NI);
        row.createCell(i++).setCellValue(pool.Avg_NI);
        row.createCell(i++).setCellValue(pool.Sigma_NI);
        row.createCell(i++).setCellValue(pool.Min_RR_min);
        row.createCell(i++).setCellValue(pool.Max_RR_max);
        row.createCell(i++).setCellValue(pool.Avg_RR_min);
        row.createCell(i++).setCellValue(pool.Avg_RR_max);
        row.createCell(i++).setCellValue(pool.Avg_RR_avg);
        row.createCell(i++).setCellValue(pool.NI_RR_min);
        row.createCell(i++).setCellValue(pool.NI_RR_max);
        row.createCell(i++).setCellValue(pool.Sigma_RR_max);
        row.createCell(i++).setCellValue(pool.Sigma_RR_min);
        row.createCell(i++).setCellValue(pool.Sigma_RR_avg);
        row.createCell(i++).setCellValue(pool.Min_Teta_min);
        row.createCell(i++).setCellValue(pool.Max_Teta_max);
        row.createCell(i++).setCellValue(pool.Avg_Teta_min);
        row.createCell(i++).setCellValue(pool.Avg_Teta_max);
        row.createCell(i++).setCellValue(pool.Avg_Teta_avg);
        row.createCell(i++).setCellValue(pool.NI_Teta_min);
        row.createCell(i++).setCellValue(pool.NI_Teta_max);
        row.createCell(i++).setCellValue(pool.Sigma_Teta_max);
        row.createCell(i++).setCellValue(pool.Sigma_Teta_min);
        row.createCell(i++).setCellValue(pool.Sigma_Teta_avg);

        if (pool.fitnessFunction != FitnessFunction.F_ALL_CONST) {
            row.createCell(i++).setCellValue(pool.NI_I_min);
            row.createCell(i++).setCellValue(pool.NI_I_max);
            row.createCell(i++).setCellValue(pool.Min_I_min);
            row.createCell(i++).setCellValue(pool.Max_I_max);
            row.createCell(i++).setCellValue(pool.Avg_I_min);
            row.createCell(i++).setCellValue(pool.Avg_I_max);
            row.createCell(i++).setCellValue(pool.Avg_I_avg);
            row.createCell(i++).setCellValue(pool.Sigma_I_max);
            row.createCell(i++).setCellValue(pool.Sigma_I_min);
            row.createCell(i++).setCellValue(pool.Sigma_I_avg);
            row.createCell(i++).setCellValue(pool.Min_GR_early);
            row.createCell(i++).setCellValue(pool.Max_GR_early);
            row.createCell(i++).setCellValue(pool.Avg_GR_early);
            row.createCell(i++).setCellValue(pool.Min_GR_late);
            row.createCell(i++).setCellValue(pool.Max_GR_late);
            row.createCell(i++).setCellValue(pool.Avg_GR_late);
            row.createCell(i++).setCellValue(pool.Min_GR_avg);
            row.createCell(i++).setCellValue(pool.Max_GR_avg);
            row.createCell(i++).setCellValue(pool.Avg_GR_avg);

            row.createCell(i++).setCellValue(pool.Min_s_min);
            row.createCell(i++).setCellValue(pool.Max_s_max);
            row.createCell(i++).setCellValue(pool.Avg_s_min);
            row.createCell(i++).setCellValue(pool.Avg_s_max);
            row.createCell(i++).setCellValue(pool.Avg_s_avg);
            row.createCell(i++).setCellValue(pool.NI_s_min);
            row.createCell(i).setCellValue(pool.NI_s_max);
        }
    }
}
