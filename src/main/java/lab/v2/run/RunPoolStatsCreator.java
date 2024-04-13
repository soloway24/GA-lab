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

        double minRRMin = 0;
        int niMinRRMin = 0;
        double maxRRMax = 0;
        int niMaxRRMax = 0;
        double avgRRMin = 0;
        double avgRRMax = 0;
        double avgRRAvg = 0;

        double minTetaMin = 0;
        int niMinTetaMin = 0;
        double maxTetaMax = 0;
        int niMaxTetaMax = 0;
        double avgTetaMin = 0;
        double avgTetaMax = 0;
        double avgTetaAvg = 0;

        double sigmaRRMin = 0;
        double sigmaRRMax = 0;
        double sigmaRRAvg = 0;
        double sigmaTetaMin = 0;
        double sigmaTetaMax = 0;
        double sigmaTetaAvg = 0;

        double minRRStart = 0;
        double maxRRStart = 0;
        double avgRRStart = 0;
        double sigmaRRStart = 0;

        double minTetaStart = 0;
        double maxTetaStart = 0;
        double avgTetaStart = 0;
        double sigmaTetaStart = 0;

        double avgRRFin = 0;
        double sigmaRRFin = 0;
        double avgTetaFin = 0;
        double sigmaTetaFin = 0;

        int minUniqueXStart = 0;
        int maxUniqueXStart = 0;
        double avgUniqueXStart = 0;
        double sigmaUniqueXStart = 0;

        int minUniqueXFin = 0;
        int maxUniqueXFin = 0;
        double avgUniqueXFin = 0;
        double sigmaUniqueXFin = 0;


        List<RunStats> sucRunStats = getSucRunStats(allRunStats);

        if (!sucRunStats.isEmpty()) {
            List<Integer> sucNIs = getIntValues(sucRunStats, RunStats::ni);

            suc = (double) sucRunStats.size() / allRunStats.size();
            minNI = getMinInt(sucNIs);
            maxNI = getMaxInt(sucNIs);
            avgNI = getAverage(sucNIs);
            sigmaNI = getStandardDeviation(sucNIs, avgNI);


            List<Pair<Double, Integer>> rrMinIterations = getMetricValueToIteration(sucRunStats, RunStats::rrMin, RunStats::niRrMin);
            Pair<Double, Integer> minRRMinIteration = getMinValueToIteration(rrMinIterations);
            minRRMin = minRRMinIteration.getKey();
            niMinRRMin = minRRMinIteration.getValue();

            List<Pair<Double, Integer>> rrMaxIterations = getMetricValueToIteration(sucRunStats, RunStats::rrMax, RunStats::niRrMax);
            Pair<Double, Integer> maxRRMaxIteration = getMaxValueToIteration(rrMaxIterations);
            maxRRMax = maxRRMaxIteration.getKey();
            niMaxRRMax = maxRRMaxIteration.getValue();

            avgRRMin = getAverage(getDoubleValues(sucRunStats, RunStats::rrMin));
            avgRRMax = getAverage(getDoubleValues(sucRunStats, RunStats::rrMax));
            avgRRAvg = getAverage(getDoubleValues(sucRunStats, RunStats::rrAvg));

            List<Pair<Double, Integer>> tetaMinIterations = getMetricValueToIteration(sucRunStats, RunStats::tetaMin, RunStats::niTetaMin);
            Pair<Double, Integer> minTetaMinIteration = getMinValueToIteration(tetaMinIterations);
            minTetaMin = minTetaMinIteration.getKey();
            niMinTetaMin = minTetaMinIteration.getValue();

            List<Pair<Double, Integer>> tetaMaxIterations = getMetricValueToIteration(sucRunStats, RunStats::tetaMax, RunStats::niTetaMax);
            Pair<Double, Integer> maxTetaMaxIteration = getMaxValueToIteration(tetaMaxIterations);
            maxTetaMax = maxTetaMaxIteration.getKey();
            niMaxTetaMax = maxTetaMaxIteration.getValue();

            avgTetaMin = getAverage(getDoubleValues(sucRunStats, RunStats::tetaMin));
            avgTetaMax = getAverage(getDoubleValues(sucRunStats, RunStats::tetaMax));
            avgTetaAvg = getAverage(getDoubleValues(sucRunStats, RunStats::tetaAvg));

            sigmaRRMin = getStandardDeviation(getDoubleValues(sucRunStats, RunStats::rrMin), avgRRMin);
            sigmaRRMax = getStandardDeviation(getDoubleValues(sucRunStats, RunStats::rrMax), avgRRMax);
            sigmaRRAvg = getStandardDeviation(getDoubleValues(sucRunStats, RunStats::rrAvg), avgRRAvg);

            sigmaTetaMin = getStandardDeviation(getDoubleValues(sucRunStats, RunStats::tetaMin), avgTetaMin);
            sigmaTetaMax = getStandardDeviation(getDoubleValues(sucRunStats, RunStats::tetaMax), avgTetaMax);
            sigmaTetaAvg = getStandardDeviation(getDoubleValues(sucRunStats, RunStats::tetaAvg), avgTetaAvg);

            List<Double> rrStarts = getDoubleValues(sucRunStats, RunStats::rrStart);
            minRRStart = getMinDouble(rrStarts);
            maxRRStart = getMaxDouble(rrStarts);
            avgRRStart = getAverage(rrStarts);
            sigmaRRStart = getStandardDeviation(rrStarts, avgRRStart);

            List<Double> tetaStarts = getDoubleValues(sucRunStats, RunStats::tetaStart);
            minTetaStart = getMinDouble(tetaStarts);
            maxTetaStart = getMaxDouble(tetaStarts);
            avgTetaStart = getAverage(tetaStarts);
            sigmaTetaStart = getStandardDeviation(tetaStarts, avgTetaStart);

            List<Double> rrFins = getDoubleValues(sucRunStats, RunStats::rrFin);
            avgRRFin = getAverage(rrFins);
            sigmaRRFin = getStandardDeviation(rrFins, avgRRFin);

            List<Double> tetaFins = getDoubleValues(sucRunStats, RunStats::tetaFin);
            avgTetaFin = getAverage(tetaFins);
            sigmaTetaFin = getStandardDeviation(tetaFins, avgTetaFin);

            List<Integer> uniqueXStarts = getIntValues(sucRunStats, RunStats::uniqueXStart);
            minUniqueXStart = getMinInt(uniqueXStarts);
            maxUniqueXStart = getMaxInt(uniqueXStarts);
            avgUniqueXStart = getAverage(uniqueXStarts);
            sigmaUniqueXStart = getStandardDeviation(uniqueXStarts, avgUniqueXStart);

            List<Integer> uniqueXFins = getIntValues(sucRunStats, RunStats::uniqueXFin);
            minUniqueXFin = getMinInt(uniqueXFins);
            maxUniqueXFin = getMaxInt(uniqueXFins);
            avgUniqueXFin = getAverage(uniqueXFins);
            sigmaUniqueXFin = getStandardDeviation(uniqueXFins, avgUniqueXFin);
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

        double minImin = 0;
        int niMinImin = 0;
        double maxImax = 0;
        int niMaxImax = 0;
        double avgImin = 0;
        double avgImax = 0;
        double avgIavg = 0;
        double sigmaImin = 0;
        double sigmaImax = 0;
        double sigmaIavg = 0;
        double minIstart = 0;
        double maxIstart = 0;
        double avgIstart = 0;
        double sigmaIstart = 0;

        double minGrEarly = 0;
        double maxGrEarly = 0;
        double avgGrEarly = 0;
        double minGrLate = 0;
        double maxGrLate = 0;
        double avgGrLate = 0;
        double minGrAvg = 0;
        double maxGrAvg = 0;
        double avgGrAvg = 0;
        double minGrStart = 0;
        double maxGrStart = 0;
        double avgGrStart = 0;
        double sigmaGrStart = 0;

        double minPrMin = 0;
        int niMinPrMin = 0;
        double maxPrMax = 0;
        int niMaxPrMax = 0;
        double avgPrMin = 0;
        double avgPrMax = 0;
        double avgPrAvg = 0;
        double sigmaPrMin = 0;
        double sigmaPrMax = 0;
        double sigmaPrAvg = 0;
        double minPrStart = 0;
        double maxPrStart = 0;
        double avgPrStart = 0;
        double sigmaPrStart = 0;

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
            List<Pair<Double, Integer>> sMinIterations = getMetricValueToIteration(sucRunStats, RunStats::sMin, RunStats::niSMin);
            Pair<Double, Integer> minSMinIteration = getMinValueToIteration(sMinIterations);
            minSMin = minSMinIteration.getKey();
            niSMin = minSMinIteration.getValue();

            List<Pair<Double, Integer>> sMaxIterations = getMetricValueToIteration(sucRunStats, RunStats::sMax, RunStats::niSMax);
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

            List<Pair<Double, Integer>> iMinIterations = getMetricValueToIteration(sucRunStats, RunStats::iMin, RunStats::niImin);
            List<Pair<Double, Integer>> iMaxIterations = getMetricValueToIteration(sucRunStats, RunStats::iMax, RunStats::niImax);
            Pair<Double, Integer> minIminIteration = getMinValueToIteration(iMinIterations);
            Pair<Double, Integer> maxImaxIteration = getMaxValueToIteration(iMaxIterations);
            minImin = minIminIteration.getKey();
            niMinImin = minIminIteration.getValue();
            maxImax = maxImaxIteration.getKey();
            niMaxImax = maxImaxIteration.getValue();

            List<Double> iMins = getDoubleValues(sucRunStats, RunStats::iMin);
            List<Double> iMaxs = getDoubleValues(sucRunStats, RunStats::iMax);
            List<Double> iAvgs = getDoubleValues(sucRunStats, RunStats::iAvg);
            avgImin = getAverage(iMins);
            avgImax = getAverage(iMaxs);
            avgIavg = getAverage(iAvgs);
            sigmaImin = getStandardDeviation(iMins, avgImin);
            sigmaImax = getStandardDeviation(iMaxs, avgImax);
            sigmaIavg = getStandardDeviation(iAvgs, avgIavg);

            List<Double> iStarts = getDoubleValues(sucRunStats, RunStats::iStart);
            minIstart = getMinDouble(iStarts);
            maxIstart = getMaxDouble(iStarts);
            avgIstart = getAverage(iStarts);
            sigmaIstart = getStandardDeviation(iStarts, avgIstart);


            List<Double> grEarlys = getDoubleValues(sucRunStats, RunStats::grEarly);
            List<Double> grLates = getDoubleValues(sucRunStats, RunStats::grLate);
            List<Double> grAvgs = getDoubleValues(sucRunStats, RunStats::grAvg);
            List<Double> grStarts = getDoubleValues(sucRunStats, RunStats::grStart);

            minGrEarly = getMinDouble(grEarlys);
            maxGrEarly = getMaxDouble(grEarlys);
            avgGrEarly = getAverage(grEarlys);

            minGrLate = getMinDouble(grLates);
            maxGrLate = getMaxDouble(grLates);
            avgGrLate = getAverage(grLates);

            minGrAvg = getMinDouble(grAvgs);
            maxGrAvg = getMaxDouble(grAvgs);
            avgGrAvg = getAverage(grAvgs);

            minGrStart = getMinDouble(grStarts);
            maxGrStart = getMaxDouble(grStarts);
            avgGrStart = getAverage(grStarts);
            sigmaGrStart = getStandardDeviation(grStarts, avgGrStart);

            List<Pair<Double, Integer>> prMinGenerations = getMetricValueToIteration(sucRunStats, RunStats::prMin, RunStats::niPrMin);
            List<Pair<Double, Integer>> prMaxGenerations = getMetricValueToIteration(sucRunStats, RunStats::prMax, RunStats::niPrMax);
            Pair<Double, Integer> minPrMinIteration = getMinValueToIteration(prMinGenerations);
            Pair<Double, Integer> maxPrMaxIteration = getMaxValueToIteration(prMaxGenerations);
            minPrMin = minPrMinIteration.getKey();
            niMinPrMin = minPrMinIteration.getValue();
            maxPrMax = maxPrMaxIteration.getKey();
            niMaxPrMax = maxPrMaxIteration.getValue();

            List<Double> prMins = getDoubleValues(sucRunStats, RunStats::prMin);
            List<Double> prMaxs = getDoubleValues(sucRunStats, RunStats::prMax);
            List<Double> prAvgs = getDoubleValues(sucRunStats, RunStats::prAvg);
            avgPrMin = getAverage(prMins);
            avgPrMax = getAverage(prMaxs);
            avgPrAvg = getAverage(prAvgs);
            sigmaPrMin = getStandardDeviation(prMins, avgPrMin);
            sigmaPrMax = getStandardDeviation(prMaxs, avgPrMax);
            sigmaPrAvg = getStandardDeviation(prAvgs, avgPrAvg);

            List<Double> prStarts = getDoubleValues(sucRunStats, RunStats::prStart);
            minPrStart = getMinDouble(prStarts);
            maxPrStart = getMaxDouble(prStarts);
            avgPrStart = getAverage(prStarts);
            sigmaPrStart = getStandardDeviation(prStarts, avgPrStart);

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
                .withAllRunStats(allRunStats)

                // all functions
                // successful runs
                .withSuc(suc)
                .withMinNI(minNI)
                .withMaxNI(maxNI)
                .withAvgNI(avgNI)
                .withSigmaNI(sigmaNI)

                .withMinRRMin(minRRMin)
                .withNiMinRRMin(niMinRRMin)
                .withMaxRRMax(maxRRMax)
                .withNiMaxRRMax(niMaxRRMax)
                .withAvgRRMin(avgRRMin)
                .withAvgRRMax(avgRRMax)
                .withAvgRRAvg(avgRRAvg)
                .withMinTetaMin(minTetaMin)
                .withNiMinTetaMin(niMinTetaMin)
                .withMaxTetaMax(maxTetaMax)
                .withNiMaxTetaMax(niMaxTetaMax)
                .withAvgTetaMin(avgTetaMin)
                .withAvgTetaMax(avgTetaMax)
                .withAvgTetaAvg(avgTetaAvg)

                .withSigmaRRMin(sigmaRRMin)
                .withSigmaRRMax(sigmaRRMax)
                .withSigmaRRAvg(sigmaRRAvg)
                .withSigmaTetaMin(sigmaTetaMin)
                .withSigmaTetaMax(sigmaTetaMax)
                .withSigmaTetaAvg(sigmaTetaAvg)

                .withMinRRStart(minRRStart)
                .withMaxRRStart(maxRRStart)
                .withAvgRRStart(avgRRStart)
                .withSigmaRRStart(sigmaRRStart)

                .withMinTetaStart(minTetaStart)
                .withMaxTetaStart(maxTetaStart)
                .withAvgTetaStart(avgTetaStart)
                .withSigmaTetaStart(sigmaTetaStart)

                .withAvgRRFin(avgRRFin)
                .withSigmaRRFin(sigmaRRFin)
                .withAvgTetaFin(avgTetaFin)
                .withSigmaTetaFin(sigmaTetaFin)

                .withMinUniqueXStart(minUniqueXStart)
                .withMaxUniqueXStart(maxUniqueXStart)
                .withAvgUniqueXStart(avgUniqueXStart)
                .withSigmaUniqueXStart(sigmaUniqueXStart)

                .withMinUniqueXFin(minUniqueXFin)
                .withMaxUniqueXFin(maxUniqueXFin)
                .withAvgUniqueXFin(avgUniqueXFin)
                .withSigmaUniqueXFin(sigmaUniqueXFin)


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

                // successful runs
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

                .withMinImin(minImin)
                .withNiMinImin(niMinImin)
                .withMaxImax(maxImax)
                .withNiMaxImax(niMaxImax)
                .withAvgImin(avgImin)
                .withAvgImax(avgImax)
                .withAvgIavg(avgIavg)
                .withSigmaImin(sigmaImin)
                .withSigmaImax(sigmaImax)
                .withSigmaIavg(sigmaIavg)
                .withMinIstart(minIstart)
                .withMaxIstart(maxIstart)
                .withAvgIstart(avgIstart)
                .withSigmaIstart(sigmaIstart)

                .withMinGrEarly(minGrEarly)
                .withMaxGrEarly(maxGrEarly)
                .withAvgGrEarly(avgGrEarly)
                .withMinGrLate(minGrLate)
                .withMaxGrLate(maxGrLate)
                .withAvgGrLate(avgGrLate)
                .withMinGrAvg(minGrAvg)
                .withMaxGrAvg(maxGrAvg)
                .withAvgGrAvg(avgGrAvg)
                .withMinGrStart(minGrStart)
                .withMaxGrStart(maxGrStart)
                .withAvgGrStart(avgGrStart)
                .withSigmaGrStart(sigmaGrStart)

                .withMinPrMin(minPrMin)
                .withNiMinPrMin(niMinPrMin)
                .withMaxPrMax(maxPrMax)
                .withNiMaxPrMax(niMaxPrMax)
                .withAvgPrMin(avgPrMin)
                .withAvgPrMax(avgPrMax)
                .withAvgPrAvg(avgPrAvg)
                .withSigmaPrMin(sigmaPrMin)
                .withSigmaPrMax(sigmaPrMax)
                .withSigmaPrAvg(sigmaPrAvg)
                .withMinPrStart(minPrStart)
                .withMaxPrStart(maxPrStart)
                .withAvgPrStart(avgPrStart)
                .withSigmaPrStart(sigmaPrStart)

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

    private List<Pair<Double, Integer>> getMetricValueToIteration(List<RunStats> sucRunStats,
                                                                  Function<RunStats, Double> metricValueFunction,
                                                                  Function<RunStats, Integer> iterationFunction) {
        return sucRunStats.stream()
                .map(runStats -> Pair.create(metricValueFunction.apply(runStats), iterationFunction.apply(runStats)))
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
