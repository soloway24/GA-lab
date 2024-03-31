package lab.v2.run;

import java.util.List;
import java.util.function.Function;

import static lab.v2.util.CalculationUtils.*;
import static lab.v2.util.MetricUtils.getStandardDeviation;

public class RunPoolStatsCreator {

    public RunPoolStats create(List<RunStats> allRunStats, RunConfiguration runConfiguration) {
        if (allRunStats.isEmpty()) {
            throw new IllegalStateException("Cannot create run pool stats for zero run stats!");
        }

        // successful runs
        double suc = 0;
        int minNI = 0;
        int maxNI = 0;
        double avgNI = 0;
        double sigmaNI = 0;

        List<RunStats> sucRunStats = getSucRunStats(allRunStats);

        if (!sucRunStats.isEmpty()) {
            List<Integer> sucNIs = getIntValues(sucRunStats, RunStats::ni);

            suc = (double) sucRunStats.size() / allRunStats.size();
            minNI = getMinInt(sucNIs);
            maxNI = getMaxInt(sucNIs);
            avgNI = getAverage(sucNIs);
            sigmaNI = getStandardDeviation(sucNIs, avgNI);
        }


        // non-successful but converged runs
        double nonSuc = 0;
        int nonMinNI = 0;
        int nonMaxNI = 0;
        double nonAvgNI = 0;
        double nonSigmaNI = 0;
        double nonMaxFFound = 0;
        double nonAvgFFound = 0;
        double nonSigmaFFound = 0;

        List<RunStats> nonSucConvergedRunStats = getNonSucConvergedRunStats(allRunStats);

        if (!nonSucConvergedRunStats.isEmpty()) {
            List<Integer> nonSucConvergedNIs = getIntValues(nonSucConvergedRunStats, RunStats::ni);
            nonSuc = (double) nonSucConvergedRunStats.size() / allRunStats.size();
            nonMinNI = getMinInt(nonSucConvergedNIs);
            nonMaxNI = getMaxInt(nonSucConvergedNIs);
            nonAvgNI = getAverage(nonSucConvergedNIs);
            nonSigmaNI = getStandardDeviation(nonSucConvergedNIs, nonAvgNI);

            List<Double> nonSucFFounds = getDoubleValues(nonSucConvergedRunStats, RunStats::fFound);
            nonMaxFFound = getMaxDouble(nonSucFFounds);
            nonAvgFFound = getAverage(nonSucFFounds);
            nonSigmaFFound = getStandardDeviation(nonSucFFounds, nonAvgFFound);
        }

        return RunPoolStats.builder()
                .withRunConfiguration(runConfiguration)
                .withRunPoolStats(allRunStats)

                // successful runs
                .withSuc(suc)
                .withMinNI(minNI)
                .withMaxNI(maxNI)
                .withAvgNI(avgNI)
                .withSigmaNI(sigmaNI)

                // non-successful but converged runs
                .withNonSuc(nonSuc)
                .withNonMinNI(nonMinNI)
                .withNonMaxNI(nonMaxNI)
                .withNonAvgNI(nonAvgNI)
                .withNonSigmaNI(nonSigmaNI)
                .withNonMaxFFound(nonMaxFFound)
                .withNonAvgFFound(nonAvgFFound)
                .withNonSigmaFFound(nonSigmaFFound)

                .build();
    }

    private List<RunStats> getSucRunStats(List<RunStats> allRunStats) {
        return allRunStats.stream()
                .filter(RunStats::isSuc)
                .toList();
    }

    private List<RunStats> getNonSucConvergedRunStats(List<RunStats> allRunStats) {
        return allRunStats.stream()
                .filter(runStats -> !runStats.isSuc())
                .filter(RunStats::hasConverged)
                .toList();
    }

    private List<Integer> getIntValues(List<RunStats> runStats, Function<? super RunStats, Integer> mapper) {
        return runStats.stream()
                .map(mapper)
                .toList();
    }

    private List<Double> getDoubleValues(List<RunStats> runStats, Function<? super RunStats, Double> mapper) {
        return runStats.stream()
                .map(mapper)
                .toList();
    }

}
