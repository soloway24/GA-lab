package lab.run;

import lab.metric.*;
import lab.util.CalculationUtils;
import lab.util.MetricUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

@Component
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
            minNI = CalculationUtils.getMinInt(sucNIs);
            maxNI = CalculationUtils.getMaxInt(sucNIs);
            avgNI = CalculationUtils.getAverage(sucNIs);
            sigmaNI = MetricUtils.getStandardDeviation(sucNIs, avgNI);


            List<Pair<Double, Integer>> rrMinIterations = getMetricValueToIteration(sucRunStats, RunStats::rrMin, RunStats::niRrMin);
            Pair<Double, Integer> minRRMinIteration = getMinValueToIteration(rrMinIterations);
            minRRMin = minRRMinIteration.getKey();
            niMinRRMin = minRRMinIteration.getValue();

            List<Pair<Double, Integer>> rrMaxIterations = getMetricValueToIteration(sucRunStats, RunStats::rrMax, RunStats::niRrMax);
            Pair<Double, Integer> maxRRMaxIteration = getMaxValueToIteration(rrMaxIterations);
            maxRRMax = maxRRMaxIteration.getKey();
            niMaxRRMax = maxRRMaxIteration.getValue();

            avgRRMin = CalculationUtils.getAverage(getDoubleValues(sucRunStats, RunStats::rrMin));
            avgRRMax = CalculationUtils.getAverage(getDoubleValues(sucRunStats, RunStats::rrMax));
            avgRRAvg = CalculationUtils.getAverage(getDoubleValues(sucRunStats, RunStats::rrAvg));

            List<Pair<Double, Integer>> tetaMinIterations = getMetricValueToIteration(sucRunStats, RunStats::tetaMin, RunStats::niTetaMin);
            Pair<Double, Integer> minTetaMinIteration = getMinValueToIteration(tetaMinIterations);
            minTetaMin = minTetaMinIteration.getKey();
            niMinTetaMin = minTetaMinIteration.getValue();

            List<Pair<Double, Integer>> tetaMaxIterations = getMetricValueToIteration(sucRunStats, RunStats::tetaMax, RunStats::niTetaMax);
            Pair<Double, Integer> maxTetaMaxIteration = getMaxValueToIteration(tetaMaxIterations);
            maxTetaMax = maxTetaMaxIteration.getKey();
            niMaxTetaMax = maxTetaMaxIteration.getValue();

            avgTetaMin = CalculationUtils.getAverage(getDoubleValues(sucRunStats, RunStats::tetaMin));
            avgTetaMax = CalculationUtils.getAverage(getDoubleValues(sucRunStats, RunStats::tetaMax));
            avgTetaAvg = CalculationUtils.getAverage(getDoubleValues(sucRunStats, RunStats::tetaAvg));

            sigmaRRMin = MetricUtils.getStandardDeviation(getDoubleValues(sucRunStats, RunStats::rrMin), avgRRMin);
            sigmaRRMax = MetricUtils.getStandardDeviation(getDoubleValues(sucRunStats, RunStats::rrMax), avgRRMax);
            sigmaRRAvg = MetricUtils.getStandardDeviation(getDoubleValues(sucRunStats, RunStats::rrAvg), avgRRAvg);

            sigmaTetaMin = MetricUtils.getStandardDeviation(getDoubleValues(sucRunStats, RunStats::tetaMin), avgTetaMin);
            sigmaTetaMax = MetricUtils.getStandardDeviation(getDoubleValues(sucRunStats, RunStats::tetaMax), avgTetaMax);
            sigmaTetaAvg = MetricUtils.getStandardDeviation(getDoubleValues(sucRunStats, RunStats::tetaAvg), avgTetaAvg);

            List<Double> rrStarts = getDoubleValues(sucRunStats, RunStats::rrStart);
            minRRStart = CalculationUtils.getMinDouble(rrStarts);
            maxRRStart = CalculationUtils.getMaxDouble(rrStarts);
            avgRRStart = CalculationUtils.getAverage(rrStarts);
            sigmaRRStart = MetricUtils.getStandardDeviation(rrStarts, avgRRStart);

            List<Double> tetaStarts = getDoubleValues(sucRunStats, RunStats::tetaStart);
            minTetaStart = CalculationUtils.getMinDouble(tetaStarts);
            maxTetaStart = CalculationUtils.getMaxDouble(tetaStarts);
            avgTetaStart = CalculationUtils.getAverage(tetaStarts);
            sigmaTetaStart = MetricUtils.getStandardDeviation(tetaStarts, avgTetaStart);

            List<Double> rrFins = getDoubleValues(sucRunStats, RunStats::rrFin);
            avgRRFin = CalculationUtils.getAverage(rrFins);
            sigmaRRFin = MetricUtils.getStandardDeviation(rrFins, avgRRFin);

            List<Double> tetaFins = getDoubleValues(sucRunStats, RunStats::tetaFin);
            avgTetaFin = CalculationUtils.getAverage(tetaFins);
            sigmaTetaFin = MetricUtils.getStandardDeviation(tetaFins, avgTetaFin);

            List<Integer> uniqueXStarts = getIntValues(sucRunStats, RunStats::uniqueXStart);
            minUniqueXStart = CalculationUtils.getMinInt(uniqueXStarts);
            maxUniqueXStart = CalculationUtils.getMaxInt(uniqueXStarts);
            avgUniqueXStart = CalculationUtils.getAverage(uniqueXStarts);
            sigmaUniqueXStart = MetricUtils.getStandardDeviation(uniqueXStarts, avgUniqueXStart);

            List<Integer> uniqueXFins = getIntValues(sucRunStats, RunStats::uniqueXFin);
            minUniqueXFin = CalculationUtils.getMinInt(uniqueXFins);
            maxUniqueXFin = CalculationUtils.getMaxInt(uniqueXFins);
            avgUniqueXFin = CalculationUtils.getAverage(uniqueXFins);
            sigmaUniqueXFin = MetricUtils.getStandardDeviation(uniqueXFins, avgUniqueXFin);
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

        int minNiFHM = 0;
        int maxNiFHM = 0;
        double avgNiFHM = 0;
        double sigmaNiFHM = 0;

        int minNiFHSM = 0;
        int maxNiFHSM = 0;
        double avgNiFHSM = 0;
        double sigmaNiFHSM = 0;

        int minNi25of = 0;
        int maxNi25of = 0;
        double avgNi25of = 0;
        double sigmaNi25of = 0;

        int minNi50of = 0;
        int maxNi50of = 0;
        double avgNi50of = 0;
        double sigmaNi50of = 0;

        int minNi75of = 0;
        int maxNi75of = 0;
        double avgNi75of = 0;
        double sigmaNi75of = 0;

        int minNi90of = 0;
        int maxNi90of = 0;
        double avgNi90of = 0;
        double sigmaNi90of = 0;

        int minNi75h = 0;
        int maxNi75h = 0;
        double avgNi75h = 0;
        double sigmaNi75h = 0;

        double minAvg75h = 0;
        double maxAvg75h = 0;
        double avgAvg75h = 0;
        double sigmaAvg75h = 0;

        int minNumOpt75h = 0;
        int maxNumOpt75h = 0;
        double avgNumOpt75h = 0;
        double sigmaNumOpt75h = 0;


        int minNi90h = 0;
        int maxNi90h = 0;
        double avgNi90h = 0;
        double sigmaNi90h = 0;

        double minAvg90h = 0;
        double maxAvg90h = 0;
        double avgAvg90h = 0;
        double sigmaAvg90h = 0;

        int minNumOpt90h = 0;
        int maxNumOpt90h = 0;
        double avgNumOpt90h = 0;
        double sigmaNumOpt90h = 0;


        int minNi95h = 0;
        int maxNi95h = 0;
        double avgNi95h = 0;
        double sigmaNi95h = 0;

        double minAvg95h = 0;
        double maxAvg95h = 0;
        double avgAvg95h = 0;
        double sigmaAvg95h = 0;

        int minNumOpt95h = 0;
        int maxNumOpt95h = 0;
        double avgNumOpt95h = 0;
        double sigmaNumOpt95h = 0;

        int minNiAlH = 0;
        int maxNiAlH = 0;
        double avgNiAlH = 0;
        double sigmaNiAlH = 0;

        double minFAlH = 0;
        double maxFAlH = 0;
        double avgFAlH = 0;
        double sigmaFAlH = 0;

        double minFishMin = 0;
        int niMinFishMin = 0;
        double maxFishMax = 0;
        int niMaxFishMax = 0;
        double avgFishMin = 0;
        double avgFishMax = 0;
        double avgFishAvg = 0;
        double sigmaFishMin = 0;
        double sigmaFishMax = 0;
        double sigmaFishAvg = 0;
        double minFishStart = 0;
        double maxFishStart = 0;
        double avgFishStart = 0;
        double sigmaFishStart = 0;

        double minKendallMin = 0;
        int niMinKendallMin = 0;
        double maxKendallMax = 0;
        int niMaxKendallMax = 0;
        double avgKendallMin = 0;
        double avgKendallMax = 0;
        double avgKendallAvg = 0;
        double sigmaKendallMin = 0;
        double sigmaKendallMax = 0;
        double sigmaKendallAvg = 0;
        double minKendallStart = 0;
        double maxKendallStart = 0;
        double avgKendallStart = 0;
        double sigmaKendallStart = 0;

        if (!runConfiguration.function().isConstant()) {

            // non-successful but converged runs
            List<RunStats> nonSucConvergedRunStats = getNonSucConvergedRunStats(allRunStats);
            if (!nonSucConvergedRunStats.isEmpty()) {
                List<Integer> nonSucConvergedNIs = getIntValues(nonSucConvergedRunStats, RunStats::ni);
                nonSuc = (double) nonSucConvergedRunStats.size() / allRunStats.size();
                nonMinNI = CalculationUtils.getMinInt(nonSucConvergedNIs);
                nonMaxNI = CalculationUtils.getMaxInt(nonSucConvergedNIs);
                nonAvgNI = CalculationUtils.getAverage(nonSucConvergedNIs);
                nonSigmaNI = MetricUtils.getStandardDeviation(nonSucConvergedNIs, nonAvgNI);

                List<Double> nonSucFFounds = getDoubleValues(nonSucConvergedRunStats, RunStats::fFound);
                nonMaxFFound = CalculationUtils.getMaxDouble(nonSucFFounds);
                nonAvgFFound = CalculationUtils.getAverage(nonSucFFounds);
                nonSigmaFFound = MetricUtils.getStandardDeviation(nonSucFFounds, nonAvgFFound);
            }

            // successful runs
            if (!sucRunStats.isEmpty()) {
                // difference
                List<Pair<Double, Integer>> sMinIterations = getMetricValueToIteration(sucRunStats, RunStats::sMin, RunStats::niSMin);
                Pair<Double, Integer> minSMinIteration = getMinValueToIteration(sMinIterations);
                minSMin = minSMinIteration.getKey();
                niSMin = minSMinIteration.getValue();

                List<Pair<Double, Integer>> sMaxIterations = getMetricValueToIteration(sucRunStats, RunStats::sMax, RunStats::niSMax);
                Pair<Double, Integer> maxSMaxIteration = getMaxValueToIteration(sMaxIterations);
                maxSMax = maxSMaxIteration.getKey();
                niSMax = maxSMaxIteration.getValue();

                avgSMin = CalculationUtils.getAverage(getDoubleValues(sucRunStats, RunStats::sMin));
                avgSMax = CalculationUtils.getAverage(getDoubleValues(sucRunStats, RunStats::sMax));
                avgSAvg = CalculationUtils.getAverage(getDoubleValues(sucRunStats, RunStats::sAvg));
                List<Double> sStarts = getDoubleValues(sucRunStats, RunStats::sStart);
                minSStart = CalculationUtils.getMinDouble(sStarts);
                maxSStart = CalculationUtils.getMaxDouble(sStarts);
                avgSStart = CalculationUtils.getAverage(sStarts);
                sigmaSStart = MetricUtils.getStandardDeviation(sStarts, avgSStart);

                // selection intensity
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
                avgImin = CalculationUtils.getAverage(iMins);
                avgImax = CalculationUtils.getAverage(iMaxs);
                avgIavg = CalculationUtils.getAverage(iAvgs);
                sigmaImin = MetricUtils.getStandardDeviation(iMins, avgImin);
                sigmaImax = MetricUtils.getStandardDeviation(iMaxs, avgImax);
                sigmaIavg = MetricUtils.getStandardDeviation(iAvgs, avgIavg);

                List<Double> iStarts = getDoubleValues(sucRunStats, RunStats::iStart);
                minIstart = CalculationUtils.getMinDouble(iStarts);
                maxIstart = CalculationUtils.getMaxDouble(iStarts);
                avgIstart = CalculationUtils.getAverage(iStarts);
                sigmaIstart = MetricUtils.getStandardDeviation(iStarts, avgIstart);

                // Growth rate
                List<Double> grEarlys = getDoubleValues(sucRunStats, RunStats::grEarly);
                List<Double> grLates = getDoubleValues(sucRunStats, RunStats::grLate);
                List<Double> grAvgs = getDoubleValues(sucRunStats, RunStats::grAvg);
                List<Double> grStarts = getDoubleValues(sucRunStats, RunStats::grStart);

                minGrEarly = CalculationUtils.getMinDouble(grEarlys);
                maxGrEarly = CalculationUtils.getMaxDouble(grEarlys);
                avgGrEarly = CalculationUtils.getAverage(grEarlys);

                minGrLate = CalculationUtils.getMinDouble(grLates);
                maxGrLate = CalculationUtils.getMaxDouble(grLates);
                avgGrLate = CalculationUtils.getAverage(grLates);

                minGrAvg = CalculationUtils.getMinDouble(grAvgs);
                maxGrAvg = CalculationUtils.getMaxDouble(grAvgs);
                avgGrAvg = CalculationUtils.getAverage(grAvgs);

                minGrStart = CalculationUtils.getMinDouble(grStarts);
                maxGrStart = CalculationUtils.getMaxDouble(grStarts);
                avgGrStart = CalculationUtils.getAverage(grStarts);
                sigmaGrStart = MetricUtils.getStandardDeviation(grStarts, avgGrStart);

                // Selection pressure
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
                avgPrMin = CalculationUtils.getAverage(prMins);
                avgPrMax = CalculationUtils.getAverage(prMaxs);
                avgPrAvg = CalculationUtils.getAverage(prAvgs);
                sigmaPrMin = MetricUtils.getStandardDeviation(prMins, avgPrMin);
                sigmaPrMax = MetricUtils.getStandardDeviation(prMaxs, avgPrMax);
                sigmaPrAvg = MetricUtils.getStandardDeviation(prAvgs, avgPrAvg);

                List<Double> prStarts = getDoubleValues(sucRunStats, RunStats::prStart);
                minPrStart = CalculationUtils.getMinDouble(prStarts);
                maxPrStart = CalculationUtils.getMaxDouble(prStarts);
                avgPrStart = CalculationUtils.getAverage(prStarts);
                sigmaPrStart = MetricUtils.getStandardDeviation(prStarts, avgPrStart);

                // Fisher's Exact test
                List<Pair<Double, Integer>> fishMinGenerations = getMetricValueToIteration(sucRunStats, RunStats::fishMin, RunStats::niFishMin);
                List<Pair<Double, Integer>> fishMaxGenerations = getMetricValueToIteration(sucRunStats, RunStats::fishMax, RunStats::niFishMax);
                Pair<Double, Integer> minfishMinIteration = getMinValueToIteration(fishMinGenerations);
                Pair<Double, Integer> maxfishMaxIteration = getMaxValueToIteration(fishMaxGenerations);
                minFishMin = minfishMinIteration.getKey();
                niMinFishMin = minfishMinIteration.getValue();
                maxFishMax = maxfishMaxIteration.getKey();
                niMaxFishMax = maxfishMaxIteration.getValue();

                List<Double> fishMins = getDoubleValues(sucRunStats, RunStats::fishMin);
                List<Double> fishMaxs = getDoubleValues(sucRunStats, RunStats::fishMax);
                List<Double> fishAvgs = getDoubleValues(sucRunStats, RunStats::fishAvg);
                avgFishMin = CalculationUtils.getAverage(fishMins);
                avgFishMax = CalculationUtils.getAverage(fishMaxs);
                avgFishAvg = CalculationUtils.getAverage(fishAvgs);
                sigmaFishMin = MetricUtils.getStandardDeviation(fishMins, avgFishMin);
                sigmaFishMax = MetricUtils.getStandardDeviation(fishMaxs, avgFishMax);
                sigmaFishAvg = MetricUtils.getStandardDeviation(fishAvgs, avgFishAvg);

                List<Double> fishStarts = getDoubleValues(sucRunStats, RunStats::fishStart);
                minFishStart = CalculationUtils.getMinDouble(fishStarts);
                maxFishStart = CalculationUtils.getMaxDouble(fishStarts);
                avgFishStart = CalculationUtils.getAverage(fishStarts);
                sigmaFishStart = MetricUtils.getStandardDeviation(fishStarts, avgFishStart);

                // Kendall's Tau-B
                List<Pair<Double, Integer>> kendallMinGenerations = getMetricValueToIteration(sucRunStats, RunStats::kendallMin, RunStats::niKendallMin);
                List<Pair<Double, Integer>> kendallMaxGenerations = getMetricValueToIteration(sucRunStats, RunStats::kendallMax, RunStats::niKendallMax);
                Pair<Double, Integer> minkendallMinIteration = getMinValueToIteration(kendallMinGenerations);
                Pair<Double, Integer> maxkendallMaxIteration = getMaxValueToIteration(kendallMaxGenerations);
                minKendallMin = minkendallMinIteration.getKey();
                niMinKendallMin = minkendallMinIteration.getValue();
                maxKendallMax = maxkendallMaxIteration.getKey();
                niMaxKendallMax = maxkendallMaxIteration.getValue();

                List<Double> kendallMins = getDoubleValues(sucRunStats, RunStats::kendallMin);
                List<Double> kendallMaxs = getDoubleValues(sucRunStats, RunStats::kendallMax);
                List<Double> kendallAvgs = getDoubleValues(sucRunStats, RunStats::kendallAvg);
                avgKendallMin = CalculationUtils.getAverage(kendallMins);
                avgKendallMax = CalculationUtils.getAverage(kendallMaxs);
                avgKendallAvg = CalculationUtils.getAverage(kendallAvgs);
                sigmaKendallMin = MetricUtils.getStandardDeviation(kendallMins, avgKendallMin);
                sigmaKendallMax = MetricUtils.getStandardDeviation(kendallMaxs, avgKendallMax);
                sigmaKendallAvg = MetricUtils.getStandardDeviation(kendallAvgs, avgKendallAvg);

                List<Double> kendallStarts = getDoubleValues(sucRunStats, RunStats::kendallStart);
                minKendallStart = CalculationUtils.getMinDouble(kendallStarts);
                maxKendallStart = CalculationUtils.getMaxDouble(kendallStarts);
                avgKendallStart = CalculationUtils.getAverage(kendallStarts);
                sigmaKendallStart = MetricUtils.getStandardDeviation(kendallStarts, avgKendallStart);
            }

            // all runs
            List<RunStats> runsStatsWithLoose = allRunStats.stream()
                    .filter(runStats -> runStats.numLoose() > 0)
                    .toList();
            niWithLoose = runsStatsWithLoose.size();

            if (niWithLoose > 0) {
                List<Integer> niLooses = getIntValues(runsStatsWithLoose, RunStats::niLoose);
                avgNILoose = CalculationUtils.getAverage(niLooses);
                sigmaNILoose = MetricUtils.getStandardDeviation(niLooses, avgNILoose);

                List<Integer> numLooses = getIntValues(runsStatsWithLoose, RunStats::numLoose);
                avgNumLoose = CalculationUtils.getAverage(numLooses);
                sigmaNumLoose = MetricUtils.getStandardDeviation(numLooses, avgNumLoose);

                List<Integer> optSavedNILooses = getIntValues(runsStatsWithLoose, RunStats::optSavedNILoose);
                avgOptSavedNILoose = CalculationUtils.getAverage(optSavedNILooses);
                sigmaOptSavedNILoose = MetricUtils.getStandardDeviation(optSavedNILooses, avgOptSavedNILoose);

                List<Integer> maxOptSavedNILooses = getIntValues(runsStatsWithLoose, RunStats::maxOptSavedNILoose);
                avgMaxOptSavedNILoose = CalculationUtils.getAverage(maxOptSavedNILooses);
                sigmaMaxOptSavedNILoose = MetricUtils.getStandardDeviation(maxOptSavedNILooses, avgMaxOptSavedNILoose);
            }

            List<Integer> niFHMs = getIntValues(allRunStats, RunStats::niFHM);
            minNiFHM = CalculationUtils.getMinInt(niFHMs);
            maxNiFHM = CalculationUtils.getMaxInt(niFHMs);
            avgNiFHM = CalculationUtils.getAverage(niFHMs);
            sigmaNiFHM = MetricUtils.getStandardDeviation(niFHMs, avgNiFHM);

            List<Integer> niFHSMs = getIntValues(allRunStats, RunStats::niFHSM);
            minNiFHSM = CalculationUtils.getMinInt(niFHSMs);
            maxNiFHSM = CalculationUtils.getMaxInt(niFHSMs);
            avgNiFHSM = CalculationUtils.getAverage(niFHSMs);
            sigmaNiFHSM = MetricUtils.getStandardDeviation(niFHSMs, avgNiFHSM);

            List<Integer> ni25ofs = getIntValues(allRunStats, RunStats::ni25of);
            minNi25of = CalculationUtils.getMinInt(ni25ofs);
            maxNi25of = CalculationUtils.getMaxInt(ni25ofs);
            avgNi25of = CalculationUtils.getAverage(ni25ofs);
            sigmaNi25of = MetricUtils.getStandardDeviation(ni25ofs, avgNi25of);

            List<Integer> ni50ofs = getIntValues(allRunStats, RunStats::ni50of);
            minNi50of = CalculationUtils.getMinInt(ni50ofs);
            maxNi50of = CalculationUtils.getMaxInt(ni50ofs);
            avgNi50of = CalculationUtils.getAverage(ni50ofs);
            sigmaNi50of = MetricUtils.getStandardDeviation(ni50ofs, avgNi50of);

            List<Integer> ni75ofs = getIntValues(allRunStats, RunStats::ni75of);
            minNi75of = CalculationUtils.getMinInt(ni75ofs);
            maxNi75of = CalculationUtils.getMaxInt(ni75ofs);
            avgNi75of = CalculationUtils.getAverage(ni75ofs);
            sigmaNi75of = MetricUtils.getStandardDeviation(ni75ofs, avgNi75of);

            List<Integer> ni90ofs = getIntValues(allRunStats, RunStats::ni90of);
            minNi90of = CalculationUtils.getMinInt(ni90ofs);
            maxNi90of = CalculationUtils.getMaxInt(ni90ofs);
            avgNi90of = CalculationUtils.getAverage(ni90ofs);
            sigmaNi90of = MetricUtils.getStandardDeviation(ni90ofs, avgNi90of);

            List<Integer> ni75hs = getHomogeneityIntValues(allRunStats, SingleHomogeneityMetrics::ni75h);
            minNi75h = CalculationUtils.getMinInt(ni75hs);
            maxNi75h = CalculationUtils.getMaxInt(ni75hs);
            avgNi75h = CalculationUtils.getAverage(ni75hs);
            sigmaNi75h = MetricUtils.getStandardDeviation(ni75hs, avgNi75h);

            List<Double> avg75hs = getHomogeneityDoubleValues(allRunStats, SingleHomogeneityMetrics::avg75h);
            minAvg75h = CalculationUtils.getMinDouble(avg75hs);
            maxAvg75h = CalculationUtils.getMaxDouble(avg75hs);
            avgAvg75h = CalculationUtils.getAverage(avg75hs);
            sigmaAvg75h = MetricUtils.getStandardDeviation(avg75hs, avgAvg75h);

            List<Integer> numOpt75hs = getHomogeneityIntValues(allRunStats, SingleHomogeneityMetrics::numOpt75h);
            minNumOpt75h = CalculationUtils.getMinInt(numOpt75hs);
            maxNumOpt75h = CalculationUtils.getMaxInt(numOpt75hs);
            avgNumOpt75h = CalculationUtils.getAverage(numOpt75hs);
            sigmaNumOpt75h = MetricUtils.getStandardDeviation(numOpt75hs, avgNumOpt75h);


            List<Integer> ni90hs = getHomogeneityIntValues(allRunStats, SingleHomogeneityMetrics::ni90h);
            minNi90h = CalculationUtils.getMinInt(ni90hs);
            maxNi90h = CalculationUtils.getMaxInt(ni90hs);
            avgNi90h = CalculationUtils.getAverage(ni90hs);
            sigmaNi90h = MetricUtils.getStandardDeviation(ni90hs, avgNi90h);

            List<Double> avg90hs = getHomogeneityDoubleValues(allRunStats, SingleHomogeneityMetrics::avg90h);
            minAvg90h = CalculationUtils.getMinDouble(avg90hs);
            maxAvg90h = CalculationUtils.getMaxDouble(avg90hs);
            avgAvg90h = CalculationUtils.getAverage(avg90hs);
            sigmaAvg90h = MetricUtils.getStandardDeviation(avg90hs, avgAvg90h);

            List<Integer> numOpt90hs = getHomogeneityIntValues(allRunStats, SingleHomogeneityMetrics::numOpt90h);
            minNumOpt90h = CalculationUtils.getMinInt(numOpt90hs);
            maxNumOpt90h = CalculationUtils.getMaxInt(numOpt90hs);
            avgNumOpt90h = CalculationUtils.getAverage(numOpt90hs);
            sigmaNumOpt90h = MetricUtils.getStandardDeviation(numOpt90hs, avgNumOpt90h);


            List<Integer> ni95hs = getHomogeneityIntValues(allRunStats, SingleHomogeneityMetrics::ni95h);
            minNi95h = CalculationUtils.getMinInt(ni95hs);
            maxNi95h = CalculationUtils.getMaxInt(ni95hs);
            avgNi95h = CalculationUtils.getAverage(ni95hs);
            sigmaNi95h = MetricUtils.getStandardDeviation(ni95hs, avgNi95h);

            List<Double> avg95hs = getHomogeneityDoubleValues(allRunStats, SingleHomogeneityMetrics::avg95h);
            minAvg95h = CalculationUtils.getMinDouble(avg95hs);
            maxAvg95h = CalculationUtils.getMaxDouble(avg95hs);
            avgAvg95h = CalculationUtils.getAverage(avg95hs);
            sigmaAvg95h = MetricUtils.getStandardDeviation(avg95hs, avgAvg95h);

            List<Integer> numOpt95hs = getHomogeneityIntValues(allRunStats, SingleHomogeneityMetrics::numOpt95h);
            minNumOpt95h = CalculationUtils.getMinInt(numOpt95hs);
            maxNumOpt95h = CalculationUtils.getMaxInt(numOpt95hs);
            avgNumOpt95h = CalculationUtils.getAverage(numOpt95hs);
            sigmaNumOpt95h = MetricUtils.getStandardDeviation(numOpt95hs, avgNumOpt95h);

            List<Integer> niAlHs = getIntValues(allRunStats, RunStats::niAlH);
            minNiAlH = CalculationUtils.getMinInt(niAlHs);
            maxNiAlH = CalculationUtils.getMaxInt(niAlHs);
            avgNiAlH = CalculationUtils.getAverage(niAlHs);
            sigmaNiAlH = MetricUtils.getStandardDeviation(niAlHs, avgNiAlH);

            List<Double> fAlHs = getDoubleValues(allRunStats, RunStats::fAlH);
            minFAlH = CalculationUtils.getMinDouble(fAlHs);
            maxFAlH = CalculationUtils.getMaxDouble(fAlHs);
            avgFAlH = CalculationUtils.getAverage(fAlHs);
            sigmaFAlH = MetricUtils.getStandardDeviation(fAlHs, avgFAlH);
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

                .withMinFishMin(minFishMin)
                .withNiMinFishMin(niMinFishMin)
                .withMaxFishMax(maxFishMax)
                .withNiMaxFishMax(niMaxFishMax)
                .withAvgFishMin(avgFishMin)
                .withAvgFishMax(avgFishMax)
                .withAvgFishAvg(avgFishAvg)
                .withSigmaFishMin(sigmaFishMin)
                .withSigmaFishMax(sigmaFishMax)
                .withSigmaFishAvg(sigmaFishAvg)
                .withMinFishStart(minFishStart)
                .withMaxFishStart(maxFishStart)
                .withAvgFishStart(avgFishStart)
                .withSigmaFishStart(sigmaFishStart)

                .withKendallMetrics(
                        KendallMetrics.builder()
                                .withMinKendallMin(minKendallMin)
                                .withNiMinKendallMin(niMinKendallMin)
                                .withMaxKendallMax(maxKendallMax)
                                .withNiMaxKendallMax(niMaxKendallMax)
                                .withAvgKendallMin(avgKendallMin)
                                .withAvgKendallMax(avgKendallMax)
                                .withAvgKendallAvg(avgKendallAvg)
                                .withSigmaKendallMin(sigmaKendallMin)
                                .withSigmaKendallMax(sigmaKendallMax)
                                .withSigmaKendallAvg(sigmaKendallAvg)
                                .withMinKendallStart(minKendallStart)
                                .withMaxKendallStart(maxKendallStart)
                                .withAvgKendallStart(avgKendallStart)
                                .withSigmaKendallStart(sigmaKendallStart)
                                .build()
                )

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

                .withMinNiFHM(minNiFHM)
                .withMaxNiFHM(maxNiFHM)
                .withAvgNiFHM(avgNiFHM)
                .withSigmaNiFHM(sigmaNiFHM)

                .withMinNiFHSM(minNiFHSM)
                .withMaxNiFHSM(maxNiFHSM)
                .withAvgNiFHSM(avgNiFHSM)
                .withSigmaNiFHSM(sigmaNiFHSM)

                .withNiOfMetrics(
                        NiOfMetrics.builder()
                                .withMinNi25of(minNi25of)
                                .withMaxNi25of(maxNi25of)
                                .withAvgNi25of(avgNi25of)
                                .withSigmaNi25of(sigmaNi25of)

                                .withMinNi50of(minNi50of)
                                .withMaxNi50of(maxNi50of)
                                .withAvgNi50of(avgNi50of)
                                .withSigmaNi50of(sigmaNi50of)

                                .withMinNi75of(minNi75of)
                                .withMaxNi75of(maxNi75of)
                                .withAvgNi75of(avgNi75of)
                                .withSigmaNi75of(sigmaNi75of)

                                .withMinNi90of(minNi90of)
                                .withMaxNi90of(maxNi90of)
                                .withAvgNi90of(avgNi90of)
                                .withSigmaNi90of(sigmaNi90of)
                                .build())

                .withHomogeneityMetrics(RunPoolHomogeneityMetrics.builder()
                        .withMinNi75h(minNi75h)
                        .withMaxNi75h(maxNi75h)
                        .withAvgNi75h(avgNi75h)
                        .withSigmaNi75h(sigmaNi75h)

                        .withMinAvg75h(minAvg75h)
                        .withMaxAvg75h(maxAvg75h)
                        .withAvgAvg75h(avgAvg75h)
                        .withSigmaAvg75h(sigmaAvg75h)

                        .withMinNumOpt75h(minNumOpt75h)
                        .withMaxNumOpt75h(maxNumOpt75h)
                        .withAvgNumOpt75h(avgNumOpt75h)
                        .withSigmaNumOpt75h(sigmaNumOpt75h)


                        .withMinNi90h(minNi90h)
                        .withMaxNi90h(maxNi90h)
                        .withAvgNi90h(avgNi90h)
                        .withSigmaNi90h(sigmaNi90h)

                        .withMinAvg90h(minAvg90h)
                        .withMaxAvg90h(maxAvg90h)
                        .withAvgAvg90h(avgAvg90h)
                        .withSigmaAvg90h(sigmaAvg90h)

                        .withMinNumOpt90h(minNumOpt90h)
                        .withMaxNumOpt90h(maxNumOpt90h)
                        .withAvgNumOpt90h(avgNumOpt90h)
                        .withSigmaNumOpt90h(sigmaNumOpt90h)


                        .withMinNi95h(minNi95h)
                        .withMaxNi95h(maxNi95h)
                        .withAvgNi95h(avgNi95h)
                        .withSigmaNi95h(sigmaNi95h)

                        .withMinAvg95h(minAvg95h)
                        .withMaxAvg95h(maxAvg95h)
                        .withAvgAvg95h(avgAvg95h)
                        .withSigmaAvg95h(sigmaAvg95h)

                        .withMinNumOpt95h(minNumOpt95h)
                        .withMaxNumOpt95h(maxNumOpt95h)
                        .withAvgNumOpt95h(avgNumOpt95h)
                        .withSigmaNumOpt95h(sigmaNumOpt95h)
                        .build())

                .withAlleleHomogeneityMetrics(AlleleHomogeneityMetrics.builder()
                        .withMinNiAlH(minNiAlH)
                        .withMaxNiAlH(maxNiAlH)
                        .withAvgNiAlH(avgNiAlH)
                        .withSigmaNiAlH(sigmaNiAlH)

                        .withMinFAlH(minFAlH)
                        .withMaxFAlH(maxFAlH)
                        .withAvgFAlH(avgFAlH)
                        .withSigmaFAlH(sigmaFAlH)
                        .build())

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

    private List<Integer> getHomogeneityIntValues(List<RunStats> runStats, Function<SingleHomogeneityMetrics, Integer> mapper) {
        return runStats.stream()
                .map(RunStats::singleHomogeneityMetrics)
                .map(mapper)
                .toList();
    }

    private List<Double> getHomogeneityDoubleValues(List<RunStats> runStats, Function<SingleHomogeneityMetrics, Double> mapper) {
        return runStats.stream()
                .map(RunStats::singleHomogeneityMetrics)
                .map(mapper)
                .toList();
    }

    private List<Double> getDoubleValues(List<RunStats> runStats, Function<? super RunStats, Double> mapper) {
        return runStats.stream()
                .map(mapper)
                .toList();
    }

}
