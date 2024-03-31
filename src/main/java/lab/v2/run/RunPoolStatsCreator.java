package lab.v2.run;

import org.apache.commons.math3.util.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static lab.v2.util.CalculationUtils.*;
import static lab.v2.util.MetricUtils.getStandardDeviation;

public class RunPoolStatsCreator {

    public RunPoolStats create(List<RunStats> allRunStats, RunConfiguration runConfiguration) {
        if (allRunStats.isEmpty()) {
            throw new IllegalStateException("Cannot create run pool stats for zero run stats!");
        }

        // all functions
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


        // all functions except FConstAll
        double nonSuc = 0;
        int nonMinNI = 0;
        int nonMaxNI = 0;
        double nonAvgNI = 0;
        double nonSigmaNI = 0;
        double nonMaxFFound = 0;
        double nonAvgFFound = 0;
        double nonSigmaFFound = 0;

        double minSMin = 0;
        int niSMin = 0;
        double maxSMax = 0;
        int niSMax = 0;
        double avgSMin = 0;
        double avgSMax = 0;
        double avgSAvg = 0;
        double minSStart = 0;
        double maxSStart = 0;
        double avgSStart = 0;
        double sigmaSStart = 0;

        int niWithLoose = 0;
        double avgNILoose = 0;
        double sigmaNILoose = 0;
        double avgNumLoose = 0;
        double sigmaNumLoose = 0;
        double avgOptSavedNILoose = 0;
        double sigmaOptSavedNILoose = 0;
        double avgMaxOptSavedNILoose = 0;
        double sigmaMaxOptSavedNILoose = 0;


        if (!runConfiguration.function().isConstant()) {

            // non-successful but converged runs
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

            // successful runs
            List<Pair<Double, Integer>> sMinIterations = getSMinToIteration(sucRunStats);
            Pair<Double, Integer> minSMinIteration = getMinValueToIteration(sMinIterations);
            minSMin = minSMinIteration.getKey();
            niSMin = minSMinIteration.getValue();

            List<Pair<Double, Integer>> sMaxIterations = getSMaxToIteration(sucRunStats);
            Pair<Double, Integer> maxSMaxIteration = getMaxValueToIteration(sMaxIterations);
            maxSMax = maxSMaxIteration.getKey();
            niSMax = maxSMaxIteration.getValue();

            avgSMin = getAverage(getDoubleValues(sucRunStats, RunStats::sMin));
            avgSMax = getAverage(getDoubleValues(sucRunStats, RunStats::sMax));
            avgSAvg = getAverage(getDoubleValues(sucRunStats, RunStats::sAvg));
            List<Double> sStarts = getDoubleValues(sucRunStats, RunStats::sStart);
            minSStart = getMinDouble(sStarts);
            maxSStart = getMaxDouble(sStarts);
            avgSStart = getAverage(sStarts);
            sigmaSStart = getStandardDeviation(sStarts, avgSStart);

            // all runs
            List<RunStats> runsStatsWithLoose = allRunStats.stream()
                    .filter(runStats -> runStats.numLoose() > 0)
                    .toList();
            niWithLoose = runsStatsWithLoose.size();

            if (niWithLoose > 0) {
                List<Integer> niLooses = getIntValues(runsStatsWithLoose, RunStats::niLoose);
                avgNILoose = getAverage(niLooses);
                sigmaNILoose = getStandardDeviation(niLooses, avgNILoose);

                List<Integer> numLooses = getIntValues(runsStatsWithLoose, RunStats::numLoose);
                avgNumLoose = getAverage(numLooses);
                sigmaNumLoose = getStandardDeviation(numLooses, avgNumLoose);

                List<Integer> optSavedNILooses = getIntValues(runsStatsWithLoose, RunStats::optSavedNILoose);
                avgOptSavedNILoose = getAverage(optSavedNILooses);
                sigmaOptSavedNILoose = getStandardDeviation(optSavedNILooses, avgOptSavedNILoose);

                List<Integer> maxOptSavedNILooses = getIntValues(runsStatsWithLoose, RunStats::maxOptSavedNILoose);
                avgMaxOptSavedNILoose = getAverage(maxOptSavedNILooses);
                sigmaMaxOptSavedNILoose = getStandardDeviation(maxOptSavedNILooses, avgMaxOptSavedNILoose);
            }
        }


        return RunPoolStats.builder()
                .withRunConfiguration(runConfiguration)
                .withRunPoolStats(allRunStats)

                // all functions
                // successful runs
                .withSuc(suc)
                .withMinNI(minNI)
                .withMaxNI(maxNI)
                .withAvgNI(avgNI)
                .withSigmaNI(sigmaNI)

                // all functions except FConstAll
                // non-successful but converged runs
                .withNonSuc(nonSuc)
                .withNonMinNI(nonMinNI)
                .withNonMaxNI(nonMaxNI)
                .withNonAvgNI(nonAvgNI)
                .withNonSigmaNI(nonSigmaNI)
                .withNonMaxFFound(nonMaxFFound)
                .withNonAvgFFound(nonAvgFFound)
                .withNonSigmaFFound(nonSigmaFFound)

                // suc runs
                .withMinSMin(minSMin)
                .withNiSMin(niSMin)
                .withMaxSMax(maxSMax)
                .withNiSMax(niSMax)
                .withAvgSMin(avgSMin)
                .withAvgSMax(avgSMax)
                .withAvgSAvg(avgSAvg)
                .withMinSStart(minSStart)
                .withMaxSStart(maxSStart)
                .withAvgSStart(avgSStart)
                .withSigmaSStart(sigmaSStart)

                // all runs
                .withNiWithLoose(niWithLoose)
                .withAvgNILoose(avgNILoose)
                .withSigmaNILoose(sigmaNILoose)
                .withAvgNumLoose(avgNumLoose)
                .withSigmaNumLoose(sigmaNumLoose)
                .withAvgOptSavedNILoose(avgOptSavedNILoose)
                .withSigmaOptSavedNILoose(sigmaOptSavedNILoose)
                .withAvgMaxOptSavedNILoose(avgMaxOptSavedNILoose)
                .withSigmaMaxOptSavedNILoose(sigmaMaxOptSavedNILoose)

                .build();
    }

    private List<Pair<Double, Integer>> getSMinToIteration(List<RunStats> sucRunStats) {
        return sucRunStats.stream()
                .map(runStats -> Pair.create(runStats.sMin(), runStats.niSMin()))
                .toList();
    }

    private List<Pair<Double, Integer>> getSMaxToIteration(List<RunStats> sucRunStats) {
        return sucRunStats.stream()
                .map(runStats -> Pair.create(runStats.sMax(), runStats.niSMax()))
                .toList();
    }

    private Pair<Double, Integer> getMinValueToIteration(List<Pair<Double, Integer>> valuesToIteration) {
        return valuesToIteration.stream()
                .min(getValueToIterationMinComparator())
                .orElseThrow(() -> new IllegalStateException("Cannot get min value to iteration of zero pairs."));
    }

    private Pair<Double, Integer> getMaxValueToIteration(List<Pair<Double, Integer>> valuesToIteration) {
        return valuesToIteration.stream()
                .max(getValueToIterationMaxComparator())
                .orElseThrow(() -> new IllegalStateException("Cannot get max value to iteration of zero pairs."));
    }

    private Comparator<Pair<Double, Integer>> getValueToIterationMinComparator() {
        return (o1, o2) -> {
            int keyComparison = o1.getKey().compareTo(o2.getKey());
            if (keyComparison == 0) {
                return o1.getValue().compareTo(o2.getValue());
            }
            return keyComparison;
        };
    }

    private Comparator<Pair<Double, Integer>> getValueToIterationMaxComparator() {
        return (o1, o2) -> {
            int keyComparison = o1.getKey().compareTo(o2.getKey());
            if (keyComparison == 0) {
                return o2.getValue().compareTo(o1.getValue());
            }
            return keyComparison;
        };
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
