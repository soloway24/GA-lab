package lab.v2;

import lab.v2.run.RunConfiguration;
import lab.v2.run.RunPoolStats;
import lab.v2.run.RunStats;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.IntStream;

public class Exporter {

    private static final String STATS_PATH = Paths.get(".").toAbsolutePath()
            .normalize().toString()
            .replace("\\", "/")
            + "/stats_v2/";


    public void exportAllRunPoolStats(List<RunPoolStats> allRunPoolStats) {
        allRunPoolStats.forEach(this::exportRunPoolStats);
    }

    public void exportRunPoolStats(RunPoolStats runPoolStats) {
        String fileName = getFileName(runPoolStats.runConfiguration());
        String filePath = STATS_PATH + fileName;

        createStatsDirectory();
        File file = new File(filePath);
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeToFile(file, runPoolStats);
    }

    private void createStatsDirectory() {
        File statsDirectory = new File(STATS_PATH);
        if (statsDirectory.exists() && statsDirectory.isDirectory()) {
            return;
        }
        statsDirectory.mkdir();
    }

    private void writeToFile(File file, RunPoolStats runPoolStats) {
        List<RunStats> allRunStats = runPoolStats.runPoolStats();
        try (FileWriter writer = new FileWriter(file)) {
            IntStream.range(0, allRunStats.size())
                    .forEach(i -> writeOneRunStats(writer, allRunStats.get(i), i));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeOneRunStats(FileWriter fileWriter, RunStats runStats, int index) {
        try {
            fileWriter.write("Run " + index + ": " + runStats.finalPopulation().toString() + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
}
