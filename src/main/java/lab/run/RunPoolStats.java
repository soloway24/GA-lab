package lab.run;

import lab.metric.KendallMetrics;
import lab.metric.NiOfMetrics;
import lab.metric.RunPoolHomogeneityMetrics;
import lombok.Builder;

import java.util.List;
import java.util.stream.IntStream;

@Builder(setterPrefix = "with")
public record RunPoolStats(RunConfiguration runConfiguration,
                           List<RunStats> allRunStats,

                           // all functions
                           // successful runs
                           double suc,
                           int minNI,
                           int maxNI,
                           double avgNI,
                           double sigmaNI,

                           double minRRMin,
                           int niMinRRMin,
                           double maxRRMax,
                           int niMaxRRMax,
                           double avgRRMin,
                           double avgRRMax,
                           double avgRRAvg,
                           double minTetaMin,
                           int niMinTetaMin,
                           double maxTetaMax,
                           int niMaxTetaMax,
                           double avgTetaMin,
                           double avgTetaMax,
                           double avgTetaAvg,

                           double sigmaRRMin,
                           double sigmaRRMax,
                           double sigmaRRAvg,
                           double sigmaTetaMin,
                           double sigmaTetaMax,
                           double sigmaTetaAvg,

                           double minRRStart,
                           double maxRRStart,
                           double avgRRStart,
                           double sigmaRRStart,

                           double minTetaStart,
                           double maxTetaStart,
                           double avgTetaStart,
                           double sigmaTetaStart,

                           double avgRRFin,
                           double sigmaRRFin,
                           double avgTetaFin,
                           double sigmaTetaFin,

                           int minUniqueXStart,
                           int maxUniqueXStart,
                           double avgUniqueXStart,
                           double sigmaUniqueXStart,

                           int minUniqueXFin,
                           int maxUniqueXFin,
                           double avgUniqueXFin,
                           double sigmaUniqueXFin,

                           // all functions except FConstAll
                           // non-successful but converged runs
                           double nonSuc,
                           int nonMinNI,
                           int nonMaxNI,
                           double nonAvgNI,
                           double nonSigmaNI,
                           double nonMaxFFound,
                           double nonAvgFFound,
                           double nonSigmaFFound,

                           // successful runs
                           double minSMin,
                           int niSMin,
                           double maxSMax,
                           int niSMax,
                           double avgSMin,
                           double avgSMax,
                           double avgSAvg,
                           double minSStart,
                           double maxSStart,
                           double avgSStart,
                           double sigmaSStart,
                           double minImin,
                           int niMinImin,
                           double maxImax,
                           int niMaxImax,
                           double avgImin,
                           double avgImax,
                           double avgIavg,
                           double sigmaImin,
                           double sigmaImax,
                           double sigmaIavg,
                           double minIstart,
                           double maxIstart,
                           double avgIstart,
                           double sigmaIstart,

                           double minGrEarly,
                           double maxGrEarly,
                           double avgGrEarly,
                           double minGrLate,
                           double maxGrLate,
                           double avgGrLate,
                           double minGrAvg,
                           double maxGrAvg,
                           double avgGrAvg,
                           double minGrStart,
                           double maxGrStart,
                           double avgGrStart,
                           double sigmaGrStart,

                           double minPrMin,
                           int niMinPrMin,
                           double maxPrMax,
                           int niMaxPrMax,
                           double avgPrMin,
                           double avgPrMax,
                           double avgPrAvg,
                           double sigmaPrMin,
                           double sigmaPrMax,
                           double sigmaPrAvg,
                           double minPrStart,
                           double maxPrStart,
                           double avgPrStart,
                           double sigmaPrStart,

                           double minFishMin,
                           int niMinFishMin,
                           double maxFishMax,
                           int niMaxFishMax,
                           double avgFishMin,
                           double avgFishMax,
                           double avgFishAvg,
                           double sigmaFishMin,
                           double sigmaFishMax,
                           double sigmaFishAvg,
                           double minFishStart,
                           double maxFishStart,
                           double avgFishStart,
                           double sigmaFishStart,

                           KendallMetrics kendallMetrics,

                           // all runs
                           int niWithLoose,
                           double avgNILoose,
                           double sigmaNILoose,
                           double avgNumLoose,
                           double sigmaNumLoose,
                           double avgOptSavedNILoose,
                           double sigmaOptSavedNILoose,
                           double avgMaxOptSavedNILoose,
                           double sigmaMaxOptSavedNILoose,

                           int minNiFHM,
                           int maxNiFHM,
                           double avgNiFHM,
                           double sigmaNiFHM,

                           int minNiFHSM,
                           int maxNiFHSM,
                           double avgNiFHSM,
                           double sigmaNiFHSM,

                           NiOfMetrics niOfMetrics,
                           RunPoolHomogeneityMetrics homogeneityMetrics
) {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RunPoolStats[\n");
        sb.append("runConfiguration=").append(runConfiguration).append("\n");
        sb.append("runStats=").append("\n");

        IntStream.range(0, allRunStats.size())
                .forEach(i -> sb.append("Run ").append(i + 1).append(": ").append(allRunStats.get(i)).append("\n"));

        // all functions
        // successful runs
        sb.append("suc = ").append(suc).append(", ");
        sb.append("minNI = ").append(minNI).append(", ");
        sb.append("avgNI = ").append(avgNI).append(", ");
        sb.append("sigmaNI = ").append(sigmaNI).append(", ");
        sb.append("maxNI = ").append(maxNI).append(", ");

        sb.append("minRRMin = ").append(minRRMin).append(", ");
        sb.append("niMinRRMin = ").append(niMinRRMin).append(", ");
        sb.append("maxRRMax = ").append(maxRRMax).append(", ");
        sb.append("niMaxRRMax = ").append(niMaxRRMax).append(", ");
        sb.append("avgRRMin = ").append(avgRRMin).append(", ");
        sb.append("avgRRMax = ").append(avgRRMax).append(", ");
        sb.append("avgRRAvg = ").append(avgRRAvg).append(", ");
        sb.append("minTetaMin = ").append(minTetaMin).append(", ");
        sb.append("niMinTetaMin = ").append(niMinTetaMin).append(", ");
        sb.append("maxTetaMax = ").append(maxTetaMax).append(", ");
        sb.append("niMaxTetaMax = ").append(niMaxTetaMax).append(", ");
        sb.append("avgTetaMin = ").append(avgTetaMin).append(", ");
        sb.append("avgTetaMax = ").append(avgTetaMax).append(", ");
        sb.append("avgTetaAvg = ").append(avgTetaAvg).append(", ");

        sb.append("sigmaRRMin = ").append(sigmaRRMin).append(", ");
        sb.append("sigmaRRMax = ").append(sigmaRRMax).append(", ");
        sb.append("sigmaRRAvg = ").append(sigmaRRAvg).append(", ");
        sb.append("sigmaTetaMin = ").append(sigmaTetaMin).append(", ");
        sb.append("sigmaTetaMax = ").append(sigmaTetaMax).append(", ");
        sb.append("sigmaTetaAvg = ").append(sigmaTetaAvg).append(", ");

        sb.append("minRRStart = ").append(minRRStart).append(", ");
        sb.append("maxRRStart = ").append(maxRRStart).append(", ");
        sb.append("avgRRStart = ").append(avgRRStart).append(", ");
        sb.append("sigmaRRStart = ").append(sigmaRRStart).append(", ");
        sb.append("minTetaStart = ").append(minTetaStart).append(", ");
        sb.append("maxTetaStart = ").append(maxTetaStart).append(", ");
        sb.append("avgTetaStart = ").append(avgTetaStart).append(", ");
        sb.append("sigmaTetaStart = ").append(sigmaTetaStart).append(", ");
        sb.append("avgRRFin = ").append(avgRRFin).append(", ");
        sb.append("sigmaRRFin = ").append(sigmaRRFin).append(", ");
        sb.append("avgTetaFin = ").append(avgTetaFin).append(", ");
        sb.append("sigmaTetaFin = ").append(sigmaTetaFin).append(", ");

        sb.append("minUniqueXStart = ").append(minUniqueXStart).append(", ");
        sb.append("maxUniqueXStart = ").append(maxUniqueXStart).append(", ");
        sb.append("avgUniqueXStart = ").append(avgUniqueXStart).append(", ");
        sb.append("sigmaUniqueXStart = ").append(sigmaUniqueXStart).append(", ");
        sb.append("minUniqueXFin = ").append(minUniqueXFin).append(", ");
        sb.append("maxUniqueXFin = ").append(maxUniqueXFin).append(", ");
        sb.append("avgUniqueXFin = ").append(avgUniqueXFin).append(", ");
        sb.append("sigmaUniqueXFin = ").append(sigmaUniqueXFin).append(", ");

        // all functions except FConstAll
        // non-successful but converged runs
        sb.append("nonSuc = ").append(nonSuc).append(", ");
        sb.append("nonMinNI = ").append(nonMinNI).append(", ");
        sb.append("nonMaxNI = ").append(nonMaxNI).append(", ");
        sb.append("nonAvgNI = ").append(nonAvgNI).append(", ");
        sb.append("nonSigmaNI = ").append(nonSigmaNI).append(", ");
        sb.append("nonMaxFFound = ").append(nonMaxFFound).append(", ");
        sb.append("nonAvgFFound = ").append(nonAvgFFound).append(", ");
        sb.append("nonSigmaFFound = ").append(nonSigmaFFound).append(", ");

        // successful runs
        sb.append("minSMin = ").append(minSMin).append(", ");
        sb.append("niSMin = ").append(niSMin).append(", ");
        sb.append("maxSMax = ").append(maxSMax).append(", ");
        sb.append("niSMax = ").append(niSMax).append(", ");
        sb.append("avgSMin = ").append(avgSMin).append(", ");
        sb.append("avgSMax = ").append(avgSMax).append(", ");
        sb.append("avgSAvg = ").append(avgSAvg).append(", ");
        sb.append("minSStart = ").append(minSStart).append(", ");
        sb.append("maxSStart = ").append(maxSStart).append(", ");
        sb.append("avgSStart = ").append(avgSStart).append(", ");
        sb.append("sigmaSStart = ").append(sigmaSStart).append(", ");

        // all runs
        sb.append("niWithLoose = ").append(niWithLoose).append(", ");
        sb.append("avgNILoose = ").append(avgNILoose).append(", ");
        sb.append("sigmaNILoose = ").append(sigmaNILoose).append(", ");
        sb.append("avgNumLoose = ").append(avgNumLoose).append(", ");
        sb.append("sigmaNumLoose = ").append(sigmaNumLoose).append(", ");
        sb.append("avgOptSavedNILoose = ").append(avgOptSavedNILoose).append(", ");
        sb.append("sigmaOptSavedNILoose = ").append(sigmaOptSavedNILoose).append(", ");
        sb.append("avgMaxOptSavedNILoose = ").append(avgMaxOptSavedNILoose).append(", ");
        sb.append("sigmaMaxOptSavedNILoose = ").append(sigmaMaxOptSavedNILoose);


        sb.append("]");

        return sb.toString();
    }
}
