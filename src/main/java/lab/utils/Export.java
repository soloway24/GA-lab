package lab.utils;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import com.github.sh0nk.matplotlib4j.builder.HistBuilder;
import lab.model.Run;
import lab.model.RunPool;
import lab.parameters.FitnessFunction;
import lab.stats.RunPoolStatsData;
import lab.stats.RunStatsData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static lab.utils.GeneticUtils.FITNESS_FUNCTION;
import static lab.utils.GeneticUtils.N;

public class Export {
    private static int index = 1;

    public static String getRootString() {
        return Paths.get(".").toAbsolutePath().normalize().toString().replace("\\", "/") + "/stats/";
    }

    public static String baseFilename() {
        return "/" + N + String.format("/%s_%s_%.2f_%s_%s",
                GeneticUtils.FITNESS_FUNCTION.getOutPath(),
                GeneticUtils.CONTEST_TYPE.getOutPath(),
                GeneticUtils.P_SWAP,
                GeneticUtils.GENETIC_OPERATORS.getOutPath(),
                GeneticUtils.ENCODING.getOutPath());
    }

    public static void exportStandaloneRuns(RunPool runPool) {
        List<Run> runs = runPool.getRuns();
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Stats");
        String exportPath = getRootString() + "runs/" + baseFilename() + "/all_stats.xlsx";

        FitnessFunction fitnessFunction = runPool.getRuns().get(0).getFitnessFunction();

        String plotExportPath;
        if (fitnessFunction == FitnessFunction.F_ALL_CONST || fitnessFunction == FitnessFunction.FHD) {
            plotExportPath = getRootString() + "plots_binary" + baseFilename();
        } else {
            plotExportPath = getRootString() + "plots_real/" + N + "/" + GeneticUtils.ENCODING.getOutPath() + String.format("/%s_%s_%.2f_%s_",
                    GeneticUtils.FITNESS_FUNCTION.getOutPath(),
                    GeneticUtils.CONTEST_TYPE.getOutPath(),
                    GeneticUtils.P_SWAP,
                    GeneticUtils.GENETIC_OPERATORS.getOutPath());
        }

        File theDir1 = new File(plotExportPath);
        if (!theDir1.exists()) {
            theDir1.mkdirs();
        }

        createRunHeaderRow(sheet, 0);
        for (int i = 0; i < runs.size(); i++) {
            createRow(sheet, i + 1, runs.get(i));
            createIterationStats(i + 1, runs.get(i));
        }

        for (int i = 0; i < 5 && i < runs.size(); i++) {
            List<Long> x = runs.get(i).stats.stream().mapToLong(entry -> entry.NI).boxed().toList();
            List<Double> RR = runs.get(i).stats.stream().mapToDouble(entry -> entry.RR).boxed().toList();
            List<Double> TETA = runs.get(i).stats.stream().mapToDouble(entry -> entry.TETA).boxed().toList();

            drawPlot(x, RR, plotExportPath + "/" + (i + 1) + "/", "RR");
            drawPlot(x, TETA, plotExportPath + "/" + (i + 1) + "/", "TETA");
            drawPlot(x, RR, TETA, plotExportPath + "/" + (i + 1) + "/", "RR_TETA");

            if (runs.get(0).getFitnessFunction() != FitnessFunction.F_ALL_CONST) {
                List<Double> F_avg = runs.get(i).stats.stream().mapToDouble(entry -> entry.F_avg).boxed().toList();
                List<Double> F_found = runs.get(i).stats.stream().mapToDouble(entry -> entry.F_found).boxed().toList();
                List<Double> Intensity = runs.get(i).stats.stream().mapToDouble(entry -> entry.Intensity).boxed().toList();
                List<Double> Diversity = runs.get(i).stats.stream().mapToDouble(entry -> entry.Diversity).boxed().toList();
                List<Double> GR = runs.get(i).stats.stream().mapToDouble(entry -> entry.GR).boxed().toList();
                List<Double> Sigma = runs.get(i).stats.stream().mapToDouble(entry -> entry.Sigma).boxed().toList();
                List<Double> BestPercent = runs.get(i).stats.stream().mapToDouble(entry -> entry.BestPercent).boxed().toList();

                drawPlot(x, F_avg, plotExportPath + "/" + (i + 1) + "/", "F_avg");
                drawPlot(x, F_found, plotExportPath + "/" + (i + 1) + "/", "F_found");
                drawPlot(x, Intensity, plotExportPath + "/" + (i + 1) + "/", "Intensity");
                drawPlot(x, Diversity, plotExportPath + "/" + (i + 1) + "/", "Diversity");
                drawPlot(x, GR, plotExportPath + "/" + (i + 1) + "/", "GR");
                drawPlot(x, Sigma, plotExportPath + "/" + (i + 1) + "/", "Sigma");
                drawPlot(x, BestPercent, plotExportPath + "/" + (i + 1) + "/", "Percentage_of_best_individuals");
                drawPlot(x, Intensity, Diversity, plotExportPath + "/" + (i + 1) + "/", "Intensity_Diversity");
            }

            for (int j = 0; j < 5 && j < runs.get(i).stats.size(); j++) {
                drawHistograms(plotExportPath + "/" + (i + 1) + "/" + j + "/", runs.get(i).stats.get(j));
            }

            int index = runs.get(i).stats.size() - 1;
            drawHistograms(plotExportPath + "/" + (i + 1) + "/final/", runs.get(i).stats.get(index));
        }

        saveWorkbook(workbook, exportPath);
    }

    private static void drawHistograms(String exportPath, RunStatsData data) {
        File theDir1 = new File(exportPath);
        if (!theDir1.exists()) {
            theDir1.mkdirs();
        }

        drawHistogram(data.ones, exportPath, "count_ones", 0, GeneticUtils.FITNESS_FUNCTION.getL());
        if (GeneticUtils.FITNESS_FUNCTION == FitnessFunction.F1 || GeneticUtils.FITNESS_FUNCTION == FitnessFunction.F2) {
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

    private static void createIterationStats(int index, Run run) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Iterations");
        String exportPath = getRootString() + "/runs/" + baseFilename() + "/" + index + "_stats.xlsx";

        createIterationsHeaderRow(sheet, 0);
        for (int i = 0; i < run.stats.size() && i < 1048575; i++) {
            createIterationRow(sheet, i + 1, run.stats.get(i), run.getFitnessFunction());
        }
        saveWorkbook(workbook, exportPath);
    }


    private static void createIterationsHeaderRow(Sheet sheet, int index) {
        Row row = sheet.createRow(index);
        int i = 0;
        row.createCell(i++).setCellValue("NI");
        row.createCell(i++).setCellValue("RR");
        row.createCell(i).setCellValue("TETA");

      //  if (GeneticUtils.FITNESS_FUNCTION != FitnessFunction.F_ALL_CONST) {
            row.createCell(i++).setCellValue("F_found");
            row.createCell(i++).setCellValue("F_avg");
            row.createCell(i++).setCellValue("Intensity");
            row.createCell(i++).setCellValue("Diversity");
            row.createCell(i++).setCellValue("Sigma");
            row.createCell(i++).setCellValue("Percentage of best individuals");
            row.createCell(i++).setCellValue("GR");
     //   }
    }

    private static void createIterationRow(Sheet sheet, int index, RunStatsData runStatsData, FitnessFunction function) {
        Row row = sheet.createRow(index);
        int i = 0;
        row.createCell(i++).setCellValue(runStatsData.NI);
        row.createCell(i++).setCellValue(runStatsData.RR);
        row.createCell(i).setCellValue(runStatsData.TETA);

        if (function != FitnessFunction.F_ALL_CONST) {
            row.createCell(i++).setCellValue(runStatsData.F_found);
            row.createCell(i++).setCellValue(runStatsData.F_avg);
            row.createCell(i++).setCellValue(runStatsData.Intensity);
            row.createCell(i++).setCellValue(runStatsData.Diversity);
            row.createCell(i++).setCellValue(runStatsData.Sigma);
            row.createCell(i++).setCellValue(runStatsData.BestPercent);
            row.createCell(i++).setCellValue(runStatsData.GR);
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

    private static void createRunHeaderRow(Sheet sheet, int index) {
        Row row = sheet.createRow(index);
        int i = 0;
        row.createCell(i++).setCellValue("Suc");
        row.createCell(i++).setCellValue("NI");
        row.createCell(i++).setCellValue("RR_min");
        row.createCell(i++).setCellValue("RR_max");
        row.createCell(i++).setCellValue("RR_avg");
        row.createCell(i++).setCellValue("NI_RR_min");
        row.createCell(i++).setCellValue("NI_RR_max");
        row.createCell(i++).setCellValue("Teta_min");
        row.createCell(i++).setCellValue("Teta_max");
        row.createCell(i++).setCellValue("Teta_avg");
        row.createCell(i++).setCellValue("NI_Teta_min");
        row.createCell(i++).setCellValue("NI_Teta_max");

        row.createCell(i++).setCellValue("F_found");
        row.createCell(i++).setCellValue("F_avg");
        row.createCell(i++).setCellValue("I_min");
        row.createCell(i++).setCellValue("I_max");
        row.createCell(i++).setCellValue("I_avg");
        row.createCell(i++).setCellValue("NI_I_min");
        row.createCell(i++).setCellValue("NI_I_max");
        row.createCell(i++).setCellValue("GR_early");
        row.createCell(i++).setCellValue("GR_avg");
        row.createCell(i++).setCellValue("GR_late");
        row.createCell(i++).setCellValue("NI_GR_late");
        row.createCell(i++).setCellValue("s_min");
        row.createCell(i++).setCellValue("s_max");
        row.createCell(i++).setCellValue("s_avg");
        row.createCell(i++).setCellValue("NI_s_min");
        row.createCell(i).setCellValue("NI_s_max");

    }

    private static void createRow(Sheet sheet, int index, Run run) {
        Row row = sheet.createRow(index);
        int i = 0;
        row.createCell(i++).setCellValue(run.Suc());
        row.createCell(i++).setCellValue(run.nIterationBeforeStop);

        row.createCell(i++).setCellValue(run.RR_min);
        row.createCell(i++).setCellValue(run.RR_max);
        row.createCell(i++).setCellValue(run.RR_avg);
        row.createCell(i++).setCellValue(run.NI_RR_min);
        row.createCell(i++).setCellValue(run.NI_RR_max);

        row.createCell(i++).setCellValue(run.Teta_min);
        row.createCell(i++).setCellValue(run.Teta_max);
        row.createCell(i++).setCellValue(run.Teta_avg);
        row.createCell(i++).setCellValue(run.NI_Teta_min);
        row.createCell(i++).setCellValue(run.NI_Teta_max);

        if (run.getFitnessFunction() != FitnessFunction.F_ALL_CONST) {
            row.createCell(i++).setCellValue(run.F_found);
            row.createCell(i++).setCellValue(run.F_avg);

            row.createCell(i++).setCellValue(run.I_min);
            row.createCell(i++).setCellValue(run.I_max);
            row.createCell(i++).setCellValue(run.I_avg);
            row.createCell(i++).setCellValue(run.NI_I_min);
            row.createCell(i++).setCellValue(run.NI_I_max);

            row.createCell(i++).setCellValue(run.GR_early);
            row.createCell(i++).setCellValue(run.GR_avg);
            row.createCell(i++).setCellValue(run.GR_late);
            row.createCell(i++).setCellValue(run.NI_GR_late);

            row.createCell(i++).setCellValue(run.s_min);
            row.createCell(i++).setCellValue(run.s_max);
            row.createCell(i++).setCellValue(run.s_avg);
            row.createCell(i++).setCellValue(run.NI_s_min);
            row.createCell(i).setCellValue(run.NI_s_max);
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
